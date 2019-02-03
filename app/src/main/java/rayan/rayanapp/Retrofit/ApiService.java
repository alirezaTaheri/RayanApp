package rayan.rayanapp.Retrofit;

import io.reactivex.Observable;
import io.reactivex.Single;
import rayan.rayanapp.Retrofit.Models.BaseResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by alireza321 on 20/12/2018.
 */

public interface ApiService {
    @GET("api")
    Call<String> testAPI();

    @POST("api/v1/users/login")
    @FormUrlEncoded
    Observable<BaseResponse> login(@Field("username") String username, @Field("password") String password);

    @GET("api/v2/groups/")
    Observable<BaseResponse> getGroups(@Header("Authorization") String token);
}
