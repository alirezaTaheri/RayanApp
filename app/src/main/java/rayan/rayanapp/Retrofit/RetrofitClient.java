package rayan.rayanapp.Retrofit;

import java.io.IOException;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by alireza321 on 20/12/2018.
 */

public class RetrofitClient {
    private static Retrofit retrofit;

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
                .addInterceptor(interceptor).build();
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
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
}
