package rayan.rayanapp.Retrofit;

import java.util.Map;

import io.reactivex.Observable;
import rayan.rayanapp.Retrofit.remote.GetSingleRemoteDataResponse;
import rayan.rayanapp.Retrofit.remote.MainData;
import rayan.rayanapp.Retrofit.remote.RemoteBrandsResponse;
import rayan.rayanapp.Retrofit.remote.RemoteDataResponse;
import rayan.rayanapp.Retrofit.remote.RemoteModelsResponse;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.PhysicalVerificationRequest_RemoteHub;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.PrimaryConfigRequest_RemoteHub_v1;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.SendDataRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.device.RemoteHubBaseResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.AddAdminRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteHubToGroupRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.AddUserByMobileRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.ChangePasswordRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.CreateGroupRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.CreateTopicRemoteHubRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.DeleteDeviceRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.DeleteRemoteHubRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditDeviceTopicRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteHubTopicRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditUserRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.SendFilesToDevicePermitRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.ChangeAccessPointRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.ChangeNameRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.EndSettingsRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.FactoryResetRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.MqttTopicRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.PlugPhysicalVerificationRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.Ready4SettingsRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.ToggleDevice;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.ToggleDeviceWithLastCommand;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.UpdateDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.UpdateRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.VerifyDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.DeleteGroupRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.DeleteUserRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditGroupRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.GroupsResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.GroupsResponsev3;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteDatasResponse;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteHubResponse;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteHubsResponse;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemotesResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.SendFilesToDevicePermitResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.AllFilesListResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.DeviceBaseResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.ConfirmCodeRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.ForgetPasswordRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.RegisterUserRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.TlmsDoneResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.ToggleDeviceResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.VerifyDeviceResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.VersionResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.YesResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by alireza321 on 20/12/2018.
 */

public interface ApiService {
    @GET("api")
    Call<String> testAPI();

    @POST("api/v3/users/login")
    @FormUrlEncoded
    Observable<BaseResponse> login(@Field("username") String username, @Field("password") String password);

    @POST("api/v3/users/login")
    @FormUrlEncoded
    Call<BaseResponse> loginCall(@Field("username") String username, @Field("password") String password);

    @POST("api/v3/users/login")
    @Headers({"mock: login"})
    @FormUrlEncoded
    Observable<BaseResponse> loginv3(@Field("username") String username, @Field("password") String password);

    @GET("api/v3/groups/")
    Observable<GroupsResponse> getGroups(@Header("Authorization") String token);

    @GET("api/v3/groups/")
    @Headers({"mock: get_groups"})
    Observable<GroupsResponsev3> getGroupsv3(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @POST("api/v3/groups/addusermobile")
    Observable<BaseResponse> addUserByMobile(@Header("Authorization") String token, @Body AddUserByMobileRequest addUserByMobileRequest);

    @POST("api/v3/groups/addadmin")
    Observable<BaseResponse> addAdmin(@Header("Authorization") String token, @Body AddAdminRequest addAdminRequest);

    @POST("api/v3/groups/deleteuser")
    Observable<BaseResponse> deleteUser(@Header("Authorization") String token, @Body DeleteUserRequest deleteUserRequest);

    @POST("api/v3/groups/delete_device")
    Observable<BaseResponse> deleteDeviceFromGroup(@Header("Authorization") String token, @Body DeleteDeviceRequest deleteDeviceRequest);

    @POST("api/v3/groups/deleteadmin")
    Observable<BaseResponse> deleteAdmin(@Header("Authorization") String token, @Body DeleteUserRequest deleteUserRequest);


    @POST("api/v3/groups/delete")
    Observable<BaseResponse> deleteGroup(@Header("Authorization") String token, @Body DeleteGroupRequest deleteGroupRequest);

    @POST("api/v3/groups/delete")
    Observable<BaseResponse> deleteGroupv3(@Header("Authorization") String token, @Body DeleteGroupRequest deleteGroupRequest);

    @POST("api/v3/groups/edit")
    Observable<BaseResponse> editGroup(@Header("Authorization") String token, @Body EditGroupRequest editGroupRequest);

    @POST("api/v3/groups")
    Observable<BaseResponse> createGroup(@Header("Authorization") String token, @Body CreateGroupRequest createGroupRequest);

    @POST("api/v3/groups/edit_topic_device")
    Observable<DeviceResponse> editDeviceTopic(@Header("Authorization") String token, @Body EditDeviceTopicRequest editDeviceTopicRequest);

    @POST("api/v3/devicev2s/edit")
    Observable<DeviceResponse> editDevice(@Header("Authorization") String token, @Body EditDeviceRequest editDeviceRequest);

    @POST("api/v3/groups/add_topic_device")
    Observable<DeviceResponse> createTopic(@Header("Authorization") String token, @Body CreateTopicRequest createTopicRequest);

    @POST("api/v3/users/edit")
    Observable<BaseResponse> editUser(@Header("Authorization") String token, @Body EditUserRequest editUserRequest);

    @POST("api/v3/users/changepass")
    Observable<BaseResponse> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest changePasswordRequest);

    @POST("api/v3/devicev2s")
    @Headers({"mock: registerDevice"})
    Observable<DeviceResponse> registerDevice(@Header("Authorization") String token, @Body RegisterDeviceRequest registerDeviceRequest);

    @POST("api/v3/groups/add_device")
    Observable<BaseResponse> addDeviceToGroup(@Header("Authorization") String token, @Body AddDeviceToGroupRequest addDeviceToGroupRequest);

    @POST
    Observable<Response<String>> changeName(@Header("auth") String auth, @Url String url, @Body ChangeNameRequest changeNameRequest);
    @POST
    Observable<DeviceBaseResponse> ITET(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<VersionResponse> getVersion(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<DeviceBaseResponse> plugStatusVerification(@Url String url, @Body PlugPhysicalVerificationRequest request);
    @POST
    Observable<ToggleDeviceResponse> toggle(@Url String url, @Body BaseRequest baseRequest);
    @POST
    @Headers("Cache-Control: no-cache")
    Observable<Response<String>> togglePin1(@Header("auth") String auth, @Url String url, @Body ToggleDevice toggleDevice);
    @POST
    @Headers("Cache-Control: no-cache")
    Observable<Response<ToggleDeviceResponse>> togglePin123(@Header("auth") String auth, @Url String url, @Body ToggleDevice toggleDevice);
    @POST
    @Headers("Cache-Control: no-cache")
    Observable<Response<VerifyDeviceResponse>> verifyDevice(@Header("auth") String auth, @Url String url, @Body VerifyDeviceRequest verifyDeviceRequest);
    @POST
    Observable<ToggleDeviceResponse> togglePin2(@Url String url, @Body ToggleDevice toggleDevice);
    //    @Headers( "Content-Type: application/json; charset=utf-8")
//    @Headers("Cache-Control: no-cache")
    @Headers("Cache-Control: no-cache")
    @POST
    Observable<Response<String>> togglePin1Pin2(@Header("auth") String auth, @Url String url, @Body ToggleDeviceWithLastCommand toggleDevice);
    @POST
    Observable<TlmsDoneResponse> tlms(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<YesResponse> NODE(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<Response<String>> settings(@Header("auth") String auth, @Url String url, @Body Ready4SettingsRequest ready4SettingsRequest);
    @POST
    Observable<rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.SetPrimaryConfigResponse> sendFirstConfigV1(@Url String url, @Body rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.SetPrimaryConfigRequest setPrimaryConfigRequest);
    @POST
    Observable<SetPrimaryConfigResponse> sendFirstConfigV2(@Url String url, @Body SetPrimaryConfigRequest setPrimaryConfigRequest);
    @POST
    Observable<Response<String>> factoryReset(@Header("auth") String auth, @Url String url, @Body FactoryResetRequest baseRequest);
    //
    @POST
    Observable<Response<String>> deviceUpdate(@Header("auth") String auth, @Url String url, @Body UpdateRequest baseRequest);
    @POST
    Observable<DeviceBaseResponse> deviceDoUpdate(@Url String url, @Body UpdateDeviceRequest updateDeviceRequest);
    @POST
    Observable<AllFilesListResponse> deviceFileList(@Url String url, @Body BaseRequest baseRequest);
    @POST
    Observable<SendFilesToDevicePermitResponse> deviceSendFilePermit(@Url String url, @Body SendFilesToDevicePermitRequest sendFilesToDevicePermitRequest);
    @POST
    Observable<Response<String>> changeAccessPoint(@Header("auth") String auth, @Url String url, @Body ChangeAccessPointRequest changeAccessPointRequest);
    @POST
    Observable<DeviceBaseResponse> sendMqtt(@Url String url, @Body MqttTopicRequest mqttTopicRequest);
    @POST
    Observable<Response<String>> endSettings(@Header("auth") String auth, @Url String url, @Body EndSettingsRequest baseRequest);

    @POST("api/v3/users/register")
    Observable<BaseResponse> registerUser(@Header("Authorization") String token, @Body RegisterUserRequest registerUserRequest);

    @POST("api/v3/users/code")
    Observable<BaseResponse> confirmCode(@Header("Authorization") String token, @Body ConfirmCodeRequest confirmCodeRequest);

    @POST("api/v3/users/forgetpass")
    Observable<BaseResponse> forgetPassword(@Header("Authorization") String token, @Body ForgetPasswordRequest forgetPasswordRequest);

//    ============================== Remote Hubs =============================

    @POST("api/v3/remotehubs")
    @Headers({"mock: addRemoteHub"})
    Observable<RemoteHubResponse> addRemoteHub(@Header("Authorization") String token, @Body AddRemoteHubRequest addRemoteHubRequest);

    @GET("api/v3/remotehubs/")
    Observable<RemoteHubsResponse> getRemoteHubs(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @GET("api/v3/remotes/")
    Observable<RemotesResponse> getRemotes(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @POST("api/v3/groups/edit_topic_remote_hub")
    Observable<RemoteHubResponse> editRemoteHubTopic(@Header("Authorization") String token, @Body EditRemoteHubTopicRequest editRemoteHubTopicRequest);

    @POST("api/v3/groups/add_remote_hub")
    @Headers({"mock: addRemoteHubToGroup"})
    Observable<BaseResponse> addRemoteHubToGroup(@Header("Authorization") String token, @Body AddRemoteHubToGroupRequest addDeviceToGroupRequest);

    @POST("api/v3/groups/add_topic_remote_hub")
    Observable<RemoteHubResponse> createTopicRemoteHub(@Header("Authorization") String token, @Body CreateTopicRemoteHubRequest createTopicRequest);

    @POST("api/v3/remotehubs/add_remote")
    @Headers({"mock: addRemote"})
    Observable<RemoteHubsResponse> addRemote(@Header("Authorization") String token, @Body AddRemoteRequest addRemoteRequest);

    @POST("api/v3/remotehubs/edit")
    Observable<RemoteHubResponse> editRemoteHub(@Header("Authorization") String token, @Body EditRemoteHubRequest editRemoteHubRequest);

    @POST("api/v3/remotehubs/edit_remote")
    @Headers({"mock: editRemote"})
    Observable<RemoteHubResponse> editRemote(@Header("Authorization") String token, @Body EditRemoteRequest editRemoteRequest);

    @FormUrlEncoded
    @POST("api/v3/remotehubs/delete_remote")
    Observable<RemoteHubsResponse> deleteRemote(@Header("Authorization") String token, @Field("remoteHub_id") String remoteHub_id, @Field("remote_id") String remote_id);

    @POST("api/v3/groups/delete_remote_hub")
    @Headers({"mock: delete_remote_hub"})
    Observable<BaseResponse> deleteRemoteHubFromGroup(@Header("Authorization") String token, @Body DeleteRemoteHubRequest deleteRemoteHubRequest);

    @GET("api/v3/remotehubs/")
    @Headers({"mock: getRemoteHubs"})
    Observable<RemoteHubsResponse> getRemoteHubsv3(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @GET("api/v3/remotes/")
    Observable<RemotesResponse> getRemotesv3(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @GET("api/v3/maindataes/")
    Observable<RemoteDatasResponse> getRemoteDatasv3(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @GET("api/v3/maindataes/")
    @Headers({"mock: getRemoteDatasById"})
    Observable<GetSingleRemoteDataResponse> getRemoteDatasById(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @POST("api/v3/maindataes/")
    @Headers({"mock: addRemoteData"})
    Observable<RemoteDataResponse> addRemoteData(@Header("Authorization") String token, @Body MainData request);

    @POST("api/v3/maindataes/edit")
    @Headers({"mock: editRemoteData"})
    Observable<RemoteDataResponse> editRemoteData(@Header("Authorization") String token, @Body MainData request);

    //    ---------------------------- Version 1 ----------------------------------
    @POST
    @Headers({"Content-Type: application/json", "User-Agent: RayanApp"})
    Observable<RemoteHubBaseResponse> sendFirstConfig_RemoteHub_v1(@Url String url, @Body PrimaryConfigRequest_RemoteHub_v1 setPrimaryConfigRequest);

    @POST
    @Headers({"Content-Type: application/json", "User-Agent: RayanApp"})
    Observable<RemoteHubBaseResponse> to_remoteHub_physical_verification(@Url String url, @Body PhysicalVerificationRequest_RemoteHub baseRequest);

    @POST
    @Headers({"Content-Type: application/json", "User-Agent: RayanApp"})
    Observable<Response<String>> send_data_remoteHub_v1(@Url String url, @Body SendDataRemoteHubRequest request);

    @POST
    @Headers({"Content-Type: application/json", "User-Agent: RayanApp"})
    Observable<Response<String>> enter_settings_remoteHub_v1(@Url String url, @Body rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.BaseRequest request);

    @POST
    @Headers({"Content-Type: application/json", "User-Agent: RayanApp"})
    Observable<Response<String>> exit_settings_remoteHub_v1(@Url String url, @Body rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.BaseRequest request);

    @POST
    @Headers({"Content-Type: application/json", "User-Agent: RayanApp","CONNECT_TIMEOUT:10000", "READ_TIMEOUT:10000", "WRITE_TIMEOUT:10000"})
    Observable<Response<String>> enter_learn_remoteHub_v1(@Url String url, @Body rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.BaseRequest request);

    @POST
    @Headers({"Content-Type: application/json", "User-Agent: RayanApp"})
    Observable<Response<String>> exit_learn_remoteHub_v1(@Url String url, @Body rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.BaseRequest request);

    @POST
    @Headers({"Content-Type: application/json", "User-Agent: RayanApp","CONNECT_TIMEOUT:10000", "READ_TIMEOUT:10000", "WRITE_TIMEOUT:10000"})
    Observable<Response<String>> get_IR_signal_remoteHub_v1(@Url String url, @Body rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.BaseRequest request);

    //    ------------------------------- Remote --------------------------------------
    @GET("api/v3/maindataes/brands")
    Observable<RemoteBrandsResponse> getAllRemoteBrands(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @GET("api/v3/maindataes/models")
    Observable<RemoteModelsResponse> getAllRemoteModles(@Header("Authorization") String token, @QueryMap Map<String, String> params);

}
