package rayan.rayanapp.Retrofit;

import android.content.Context;

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

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import rayan.rayanapp.R;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by alireza321 on 20/12/2018.
 */

public class RetrofitClient {
    private static Retrofit retrofit;
    private static Retrofit retrofit2;

    public static Retrofit getRetrofitInstance(String BASE_URL){
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
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder okhttpClient = new OkHttpClient.Builder();
            okhttpClient.addInterceptor(httpLoggingInterceptor);
            retrofit2 = new Retrofit.Builder()
                    .client(okhttpClient.build())
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
                    .cache(null)
//                .readTimeout(10, TimeUnit.SECONDS)
//                .connectTimeout(10, TimeUnit.SECONDS)
                    ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
