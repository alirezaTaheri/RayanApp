package rayan.rayanapp.Retrofit;

import io.reactivex.Observable;
import rayan.rayanapp.Retrofit.Models.Requests.AddAdminRequest;
import rayan.rayanapp.Retrofit.Models.Requests.AddUserByMobileRequest;
import rayan.rayanapp.Retrofit.Models.Requests.CreateGroupRequest;
import rayan.rayanapp.Retrofit.Models.Responses.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Requests.DeleteGroupRequest;
import rayan.rayanapp.Retrofit.Models.Requests.DeleteUserRequest;
import rayan.rayanapp.Retrofit.Models.Requests.EditGroupRequest;
import rayan.rayanapp.Retrofit.Models.Responses.GroupsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
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

    @POST("api/v2/users/login")
    @FormUrlEncoded
    Observable<BaseResponse> login(@Field("username") String username, @Field("password") String password);

    @GET("api/v2/groups/")
    Observable<GroupsResponse> getGroups(@Header("Authorization") String token);

    @POST("api/v2/groups/addusermobile")
    Observable<BaseResponse> addUserByMobile(@Header("Authorization") String token, @Body AddUserByMobileRequest addUserByMobileRequest);

    @POST("api/v2/groups/addadmin")
    Observable<BaseResponse> addAdmin(@Header("Authorization") String token, @Body AddAdminRequest addAdminRequest);

    @POST("api/v2/groups/deleteuser")
    Observable<BaseResponse> deleteUser(@Header("Authorization") String token, @Body DeleteUserRequest deleteUserRequest);


    @POST("api/v2/groups/delete")
    Observable<BaseResponse> deleteGroup(@Header("Authorization") String token, @Body DeleteGroupRequest deleteGroupRequest);

    @POST("api/v2/groups/edit")
    Observable<BaseResponse> editGroup(@Header("Authorization") String token, @Body EditGroupRequest editGroupRequest);

    @POST("api/v1/groups")
    Observable<BaseResponse> createGroup(@Header("Authorization") String token, @Body CreateGroupRequest createGroupRequest);



}
