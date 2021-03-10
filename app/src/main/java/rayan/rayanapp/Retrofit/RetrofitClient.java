package rayan.rayanapp.Retrofit;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Util.AppConstants;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static rayan.rayanapp.Util.AppConstants.CONNECT_TIMEOUT;
import static rayan.rayanapp.Util.AppConstants.READ_TIMEOUT;
import static rayan.rayanapp.Util.AppConstants.WRITE_TIMEOUT;

/**
 * Created by alireza321 on 20/12/2018.
 */

public class RetrofitClient {
    private static Retrofit retrofit;
    private static Retrofit retrofit2;

    public static Retrofit getRetrofitInstance(String BASE_URL){
        HandleFakeRequest.fillBodyMap();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Authorization", "13246578").build();
                return chain.proceed(request);
            }
        });
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cache(null)
//                .readTimeout(10, TimeUnit.SECONDS)
//                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
//                    .client(client)
                    .client(getUnsafeOkHttpClient().build())
                    .baseUrl(BASE_URL)
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
    public static Retrofit getRetrofitInstanceScalar(String BASE_URL){
        if (retrofit2 == null){
            HandleFakeRequest.fillBodyMap();
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder okhttpClient = new OkHttpClient.Builder();
            okhttpClient.addInterceptor(httpLoggingInterceptor);
            retrofit2 = new Retrofit.Builder()
                    .client(okhttpClient.addInterceptor(new LoginInterceptor()).build())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit2;
    }
    public static <T> void request(Single<retrofit2.Response<T>> single, ApiListener<T> listener) {
        Disposable disposable = single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((retrofit2.Response<T> tResponse) -> {
            if (tResponse.isSuccessful()) {
                listener.onSuccess(tResponse.body());
            } else {
                listener.onServerError(tResponse.errorBody().toString());
            }
        }, listener::onError);
    }
    public interface ApiListener<T> {

        void onSuccess(T t);

        void onError(Throwable throwable);

        void onServerError(String errorMessage);
    }

    public static OkHttpClient.Builder getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            return builder
                    .addInterceptor(interceptor)
                    .addInterceptor(new LoginInterceptor())
                    .cache(null)
//                .readTimeout(10, TimeUnit.SECONDS)
//                .connectTimeout(10, TimeUnit.SECONDS)
                    ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static class LoginInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = HandleFakeRequest.check(request,chain);
            int connectTimeout = chain.connectTimeoutMillis();
            int readTimeout = chain.readTimeoutMillis();
            int writeTimeout = chain.writeTimeoutMillis();
            String connectNew = request.header(CONNECT_TIMEOUT);
            String readNew = request.header(READ_TIMEOUT);
            String writeNew = request.header(WRITE_TIMEOUT);

            if (!TextUtils.isEmpty(connectNew)) {
                connectTimeout = Integer.valueOf(connectNew);
            }
            if (!TextUtils.isEmpty(readNew)) {
                readTimeout = Integer.valueOf(readNew);
            }
            if (!TextUtils.isEmpty(writeNew)) {
                writeTimeout = Integer.valueOf(writeNew);
            }
            if (response!=null)return response;
            response = chain
                    .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .proceed(request);
            try {
                if (response.code() == 401 || response.message().toLowerCase().contains(AppConstants.UNAUTHORIZED.toLowerCase())) {
                    Log.e("LoginInterceptor", "Request Failed Because of expired Token");
                    Log.e("LoginInterceptor","=-=-=-=-=-=-=-=-=-=-=-=-");
                    Log.e("LoginInterceptor","Response: "+response);
                    Log.e("LoginInterceptor","Token: "+request.header("Authorization"));
                    Log.e("LoginInterceptor","Code & Message: "+response.code() + "  |  " + response.message());
                    String newToken;
                    ApiService apiService = ApiUtils.getApiService();
                    retrofit2.Response<BaseResponse> responseCall = apiService.loginCall(RayanApplication.getPref().getUsername(), RayanApplication.getPref().getPassword()).execute();
                    newToken = responseCall.body().getData().getToken();
                    Log.e("LoginInterceptor", "NewToken has gotten: " + newToken);
                    if (newToken != null) {
                        RayanApplication.getPref().saveToken(newToken);
                        Request newRequest = request.newBuilder().header(AppConstants.HEADER_AUTHORIZATION, newToken).build();
                        Response newResponse = chain.proceed(newRequest);
                        return newResponse;
                    }
                }
            }catch (Exception e){
                Log.e("LoginInterceptor", "Unexpected Error Occurred: "+e);
                e.printStackTrace();
                return response;
            }
            return response;
        }

    }
}
