package rayan.rayanapp.Retrofit;

import io.reactivex.Observable;
import rayan.rayanapp.Retrofit.Models.Requests.api.AddAdminRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.AddUserByMobileRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.ChangePasswordRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.CreateGroupRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditUserRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.SendFilesToDevicePermitRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeAccessPointRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeNameRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.MqttTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.PlugPhysicalVerificationRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ToggleDevice;
import rayan.rayanapp.Retrofit.Models.Requests.device.UpdateDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Requests.api.DeleteGroupRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.DeleteUserRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditGroupRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.GroupsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.SendFilesToDevicePermitResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.AllFilesListResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.ChangeNameResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;
import rayan.rayanapp.Retrofit.Models.Requests.api.ConfirmCodeRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.ForgetPasswordRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.RegisterUserRequest;
import rayan.rayanapp.Retrofit.Models.Responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.TlmsDoneResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.ToggleDeviceResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.YesResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

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

    @POST("api/v2/groups/deleteadmin")
    Observable<BaseResponse> deleteAdmin(@Header("Authorization") String token, @Body DeleteUserRequest deleteUserRequest);


    @POST("api/v2/groups/delete")
    Observable<BaseResponse> deleteGroup(@Header("Authorization") String token, @Body DeleteGroupRequest deleteGroupRequest);

    @POST("api/v2/groups/edit")
    Observable<BaseResponse> editGroup(@Header("Authorization") String token, @Body EditGroupRequest editGroupRequest);

    @POST("api/v2/groups")
    Observable<BaseResponse> createGroup(@Header("Authorization") String token, @Body CreateGroupRequest createGroupRequest);

    @POST("api/v2/groups/edittopic")
    Observable<DeviceResponse> editDevice(@Header("Authorization") String token, @Body EditDeviceRequest editDeviceRequest);

    @POST("api/v2/groups/addtopic")
    Observable<DeviceResponse> createTopic(@Header("Authorization") String token, @Body CreateTopicRequest createTopicRequest);

    @POST("api/v2/users/edit")
    Observable<BaseResponse> editUser(@Header("Authorization") String token, @Body EditUserRequest editUserRequest);

    @POST("api/v2/users/changepass")
    Observable<BaseResponse> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest changePasswordRequest);

    @POST("api/v2/users")
    Observable<DeviceResponse> registerDevice(@Header("Authorization") String token, @Body RegisterDeviceRequest registerDeviceRequest);

    @POST("api/v2/groups/adduser")
    Observable<BaseResponse> addDeviceToGroup(@Header("Authorization") String token, @Body AddDeviceToGroupRequest addDeviceToGroupRequest);

    @POST
    Observable<ChangeNameResponse> changeName(@Url String url, @Body ChangeNameRequest changeNameRequest);
    @POST
    Observable<DeviceBaseResponse> ITET(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<DeviceBaseResponse> getVersion(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<DeviceBaseResponse> plugStatusVerification(@Url String url, @Body PlugPhysicalVerificationRequest request);
    @POST
    Observable<ToggleDeviceResponse> toggle(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<ToggleDeviceResponse> togglePin1(@Url String url, @Body ToggleDevice toggleDevice);
    @POST
    Observable<ToggleDeviceResponse> togglePin2(@Url String url, @Body ToggleDevice toggleDevice);
    @POST
    Observable<TlmsDoneResponse> tlms(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<YesResponse> NODE(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<DeviceBaseResponse> settings(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<SetPrimaryConfigResponse> sendFirstConfig(@Url String url, @Body SetPrimaryConfigRequest setPrimaryConfigRequest);
    @POST
    Observable<DeviceBaseResponse> factoryReset(@Url String url, @Body BaseRequest baseRequest);
    //
    @POST
    Observable<DeviceBaseResponse> deviceUpdate(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<DeviceBaseResponse> deviceDoUpdate(@Url String url, @Body UpdateDeviceRequest updateDeviceRequest);
    @POST
    Observable<AllFilesListResponse> deviceFileList(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<SendFilesToDevicePermitResponse> deviceSendFilePermit(@Url String url, @Body SendFilesToDevicePermitRequest sendFilesToDevicePermitRequest);
    @POST
    Observable<DeviceBaseResponse> changeAccessPoint(@Url String url, @Body ChangeAccessPointRequest changeAccessPointRequest);
    @POST
    Observable<DeviceBaseResponse> sendMqtt(@Url String url, @Body MqttTopicRequest mqttTopicRequest);
    @POST
    Observable<DeviceBaseResponse> endSettings(@Url String url, @Body BaseRequest baseRequest);

    @POST("api/v2/users/register")
    Observable<BaseResponse> registerUser(@Header("Authorization") String token, @Body RegisterUserRequest registerUserRequest);

    @POST("api/v2/users/code")
    Observable<BaseResponse> confirmCode(@Header("Authorization") String token, @Body ConfirmCodeRequest confirmCodeRequest);

    @POST("api/v2/users/forgetpass")
    Observable<BaseResponse> forgetPassword(@Header("Authorization") String token, @Body ForgetPasswordRequest forgetPasswordRequest);

}
