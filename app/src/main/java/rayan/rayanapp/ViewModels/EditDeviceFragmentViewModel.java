package rayan.rayanapp.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.DeleteUserRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.SendFilesToDevicePermitRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeAccessPointRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeNameRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.MqttTopicRequest;
//<<<<<<< HEAD
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
//=======
import rayan.rayanapp.Retrofit.Models.Requests.device.UpdateDeviceRequest;
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
import rayan.rayanapp.Retrofit.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.SendFilesToDevicePermitResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.AllFilesListResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.ChangeNameResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;
import rayan.rayanapp.Util.AppConstants;

public class EditDeviceFragmentViewModel extends DevicesFragmentViewModel {

    private final String TAG = EditDeviceFragmentViewModel.class.getSimpleName();
    public EditDeviceFragmentViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<DeviceResponse> editDevice(String id, String name, String type, String groupId, String ssid){
        final MutableLiveData<DeviceResponse> results = new MutableLiveData<>();
        editDeviceObservable(new EditDeviceRequest(id, groupId, name, type, ssid)).subscribe(editDeviceObserver(results));
        return results;
    }
    private Observable<DeviceResponse> editDeviceObservable(EditDeviceRequest editDeviceRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .editDevice(RayanApplication.getPref().getToken(), editDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceResponse> editDeviceObserver(MutableLiveData<DeviceResponse> results){
        return new DisposableObserver<DeviceResponse>() {

            @Override
            public void onNext(@NonNull DeviceResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e.toString().contains("Unauthorized"))
                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public LiveData<DeviceResponse> createTopic(String id, String code_10, String domain, String groupId){
        final MutableLiveData<DeviceResponse> results = new MutableLiveData<>();
        createTopicObservable(new CreateTopicRequest(id, groupId, code_10, domain)).subscribe(createTopicObserver(results));
        return results;
    }
    private Observable<DeviceResponse> createTopicObservable(CreateTopicRequest createTopicRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .createTopic(RayanApplication.getPref().getToken(), createTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceResponse> createTopicObserver(MutableLiveData<DeviceResponse> results){
        return new DisposableObserver<DeviceResponse>() {

            @Override
            public void onNext(@NonNull DeviceResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e.toString().contains("Unauthorized"))
                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public MutableLiveData<String> zipChangeName(String id , String name, String type, String groupId, String ip, String ssid){
        MutableLiveData<String> response = new MutableLiveData<>();
        byte[] data = name.getBytes();
        String baseName = Base64.encodeToString(data, Base64.DEFAULT);
        Observable.zip(
                toDeviceChangeNameObservable(new ChangeNameRequest(baseName),ip).subscribeOn(Schedulers.io()).doOnNext(changeNameResponse -> {
                    Log.e(TAG, " 00000000 ChangeNameResponse: " + changeNameResponse);
                }),
                editDeviceObservable(new EditDeviceRequest(id, groupId, name, type, ssid)).subscribeOn(Schedulers.io()).doOnNext(deviceResponse -> {
                    Log.e(TAG, "000000000 DeviceResponse: " + deviceResponse);
                }),
                (BiFunction<ChangeNameResponse, DeviceResponse, Object>) (changeNameResponse, deviceResponse) -> {
                    Log.e(TAG, "ChangeNameResponse: " + changeNameResponse);
                    Log.e(TAG, "DeviceResponse: " + deviceResponse);
                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR)){
                        if (deviceResponse.getData().getMessage() != null)
                        return deviceResponse.getData().getMessage();
                        else return AppConstants.ERROR;
                    }
                    if (deviceResponse.getData().getMessage()!=null && deviceResponse.getData().getMessage().toLowerCase().contains(AppConstants.FORBIDDEN)){
                        return AppConstants.FORBIDDEN;
                    }
                    if (changeNameResponse.getCmd().contains(AppConstants.CHANGE_NAME_FALSE)){
                        return AppConstants.CHANGE_NAME_FALSE;
                    }
                    return AppConstants.OPERATION_DONE;
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                Log.e(TAG, "onNext: " + o);
                response.postValue((String)o);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SocketTimeoutException){
                    response.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "ChangeName: onComplete " );
            }
        });
        return response;
    }

    @SuppressLint("CheckResult")
    public MutableLiveData<String> flatMqtt(Device device){
        MutableLiveData<String> result = new MutableLiveData<>();
        createTopicObservable(new CreateTopicRequest(device.getId(), device.getGroupId(),device.getChipId(), AppConstants.MQTT_HOST))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(deviceResponse -> toDeviceMqttObservable(new MqttTopicRequest(AppConstants.MQTT_HOST,
                        deviceResponse.getData().getDevice().getUsername(),
                        ((deviceResponse.getData().getDevice().getPassword() != null) ? deviceResponse.getData().getDevice().getPassword():"Password Not Set"),
                        deviceResponse.getData().getDevice().getTopic().getTopic(),
                        String.valueOf(AppConstants.MQTT_PORT)
                        ),device.getIp()))
        .subscribe(new Observer<DeviceBaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(DeviceBaseResponse deviceBaseResponse) {
                Log.e(TAG, "onNext"+deviceBaseResponse);
                result.postValue(deviceBaseResponse.getCmd());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError" + e);
                if (e instanceof SocketTimeoutException){
                    result.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete Mqtt Sent To Device");
            }
        });
        return result;
    }

    public LiveData<ChangeNameResponse> toDeviceChangeName(String name, String ip){
        final MutableLiveData<ChangeNameResponse> results = new MutableLiveData<>();
        toDeviceChangeNameObservable(new ChangeNameRequest(name),ip).subscribe(toDeviceChangeNameObserver(results));
        return results;
    }
    private Observable<ChangeNameResponse> toDeviceChangeNameObservable(ChangeNameRequest changeNameRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .changeName(getDeviceAddress(ip), changeNameRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<ChangeNameResponse> toDeviceChangeNameObserver(MutableLiveData<ChangeNameResponse> results){
        return new DisposableObserver<ChangeNameResponse>() {

            @Override
            public void onNext(@NonNull ChangeNameResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e.toString().contains("Unauthorized"))
                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    @SuppressLint("CheckResult")
    public LiveData<String> toDeviceFactoryReset(Device device){
        final MutableLiveData<String> results = new MutableLiveData<>();
        deleteUserObservable(new DeleteUserRequest(device.getId(), device.getGroupId())).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .flatMap(deviceBaseResponse -> {
                    if (deviceBaseResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION))
                        return toDeviceFactoryResetObservable(new BaseRequest(AppConstants.FACTORY_RESET), device.getIp());
                    else if (deviceBaseResponse.getStatus().getDescription().equals(AppConstants.ERROR_DESCRIPTION) &&
                            deviceBaseResponse.getData().getMessage().equals(AppConstants.USER_NOT_FOUND_RESPONSE)){
                        results.postValue(AppConstants.USER_NOT_FOUND_RESPONSE);
                        return null;
                    }
                    return null;
                }).subscribe(toDeviceFactoryResetObserver(results));
//        toDeviceFactoryResetObservable(new BaseRequest(AppConstants.FACTORY_RESET),ip).subscribe(toDeviceFactoryResetObserver(results));
        return results;
    }


    private Observable<BaseResponse> deleteUserObservable(DeleteUserRequest deleteUserRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deleteUser(RayanApplication.getPref().getToken(), deleteUserRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<DeviceBaseResponse> toDeviceFactoryResetObservable(BaseRequest baseRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .factoryReset(getDeviceAddress(ip), baseRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceFactoryResetObserver(MutableLiveData<String> results){
        return new DisposableObserver<DeviceBaseResponse>() {

            @Override
            public void onNext(@NonNull DeviceBaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getCmd());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e instanceof SocketTimeoutException){
                    results.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    @SuppressLint("CheckResult")
    public LiveData<String> toDeviceChangeAccessPoint(Device device, String ssid, String password){
        final MutableLiveData<String> results = new MutableLiveData<>();
//        toDeviceChangeAccessPointObservable(new ChangeAccessPointRequest(hname, pwd, ssid, style),ip).subscribe(toDeviceChangeAccessPointObserver(results));
        Observable.zip(
                toDeviceChangeAccessPointObservable(new ChangeAccessPointRequest(device.getName1(), password, ssid, device.getStyle()), device.getIp())
                        .doOnNext(deviceBaseResponse -> {
                            Log.e(TAG, "Do on next for changing accessPoint: " + deviceBaseResponse);
                        }),
                editDeviceObservable(new EditDeviceRequest(device.getId(), device.getGroupId(), device.getName1(), device.getType(), ssid)).subscribeOn(Schedulers.io()).doOnNext(deviceResponse -> {
                    Log.e(TAG, "000000000 DeviceResponse: " + deviceResponse);
                }), (deviceBaseResponse, deviceResponse) -> {
                    Log.e(TAG, "ChangeAccessPointResponse: " + deviceBaseResponse);
                    Log.e(TAG, "DeviceResponse: " + deviceResponse);
                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR)){
                        if (deviceResponse.getData().getMessage() != null)
                            return deviceResponse.getData().getMessage();
                        else return AppConstants.ERROR;
                    }
                    if (deviceResponse.getData().getMessage()!=null && deviceResponse.getData().getMessage().toLowerCase().contains(AppConstants.FORBIDDEN)){
                        return AppConstants.FORBIDDEN;
                    }
                    if (deviceBaseResponse.getCmd().contains(AppConstants.CHANGE_NAME_FALSE)){
                        return AppConstants.CHANGE_NAME_FALSE;
                    }
                    return AppConstants.OPERATION_DONE;
                }
        ).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "OnSubscribe whole zip change accessPoint");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "OnNext Change AccessPoint: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError Change AccessPoint: " + e);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete Change AccessPoint: ");
            }
        });

        return results;
    }
    private Observable<DeviceBaseResponse> toDeviceChangeAccessPointObservable(ChangeAccessPointRequest changeAccessPointRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .changeAccessPoint(getDeviceAddress(ip), changeAccessPointRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceChangeAccessPointObserver(MutableLiveData<String> results){
        return new DisposableObserver<DeviceBaseResponse>() {

            @Override
            public void onNext(@NonNull DeviceBaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getCmd());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e instanceof SocketTimeoutException){
                    results.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


    public LiveData<DeviceBaseResponse> toDeviceMqtt(String host, String user, String pass, String topic, String port, String ip){
        final MutableLiveData<DeviceBaseResponse> results = new MutableLiveData<>();
        toDeviceMqttObservable(new MqttTopicRequest(host,user,pass,topic,port),ip).subscribe(toDeviceMqttObserver(results));
        return results;
    }
    public Observable<DeviceBaseResponse> toDeviceMqttObservable(MqttTopicRequest mqttTopicRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .sendMqtt(getDeviceAddress(ip), mqttTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceMqttObserver(MutableLiveData<DeviceBaseResponse> results){
        return new DisposableObserver<DeviceBaseResponse>() {

            @Override
            public void onNext(@NonNull DeviceBaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e.toString().contains("Unauthorized"))
                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


    public LiveData<String> toDeviceEndSettings(String ip){
        final MutableLiveData<String> results = new MutableLiveData<>();
        toDeviceEndSettingsObservable(new BaseRequest(AppConstants.END_SETTINGS), ip).subscribe(toDeviceEndSettingsObserver(results));
        return results;
    }
    private Observable<DeviceBaseResponse> toDeviceEndSettingsObservable(BaseRequest baseRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .endSettings(getDeviceAddress(ip), baseRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceEndSettingsObserver(MutableLiveData<String> results){
        return new DisposableObserver<DeviceBaseResponse>() {

            @Override
            public void onNext(@NonNull DeviceBaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getCmd());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                results.postValue(AppConstants.ERROR);
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


    public void updateDevice(Device device){
        deviceDatabase.updateDevice(device);
    }

    public String getDeviceAddress(String ip){
//       return "http://"+ip+":"+AppConstants.HTTP_TO_DEVICE_PORT;
       return "http://192.168.137.1/test.php";
    }

    public void writeToFile(String mycode) {
        try {
            String rootPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.nodeUpdateFolder/";
           // String hi="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus condimentum sagittis lacus, laoreet luctus ligula laoreet ut. Vestibulum ullamcorper accumsan velit vel vehicula. Proin tempor lacus arcu. Nunc at elit condimentum, semper nisi et, condimentum mi. In venenatis blandit nibh at sollicitudin. Vestibulum dapibus mauris at orci maximus pellentesque. Nullam id elementum ipsum. Suspendisse cursus lobortis viverra. Proin et erat at mauris tincidunt porttitor vitae ac dui.\n" +
            //       "Fusce tincidunt dictum tempor. Mauris nec tellus posuere odio hendrerit sodales. Cras sit amet dapibus velit. Cras risus turpis, vehicula sed lobortis non, volutpat ut leo. Integer at efficitur risus, nec volutpat turpis. Phasellus in arcu sed nunc varius eleifend ut nec dui. Donec pharetra arcu eget dui consectetur, et semper elit rutrum. Integer quis ornare nisl, et scelerisque enim. In libero ligula, porttitor non enim a, scelerisque molestie nibh.\n" +
              //      "Sed tristique auctor tellus id facilisis. Quisque laoreet auctor massa ut venenatis. Sed elementum quis neque non accumsan. Suspendisse eros justo, tempus dapibus facilisis eget, vehicula eu magna. Cras sodales mauris ac tincidunt pellentesque. Vestibulum hendrerit dictum lectus, quis tempor turpis. Morbi odio risus, ullamcorper ac quam vel, mattis dictum magna. Vivamus in dui diam. Nulla non tristique lectus, quis iaculis magna. Sed vel nisi a ante aliquet accumsan. Suspendisse dapibus lacus risus, at vulputate sapien faucibus non. Sed convallis nunc vel risus luctus maximus. Praesent a nulla tempus, varius enim non, aliquam tellus. Vestibulum non massa id diam aliquam suscipit non nec purus. Vivamus cursus dictum risus id vulputate. In commodo porta tellus, sed pharetra mauris vehicula vel.";
            File root = new File(rootPath);
            if (!root.exists()) {
                root.mkdirs();
            }
            File f = new File(rootPath + ".nodeUpdateFile.txt");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();

            FileOutputStream out = new FileOutputStream(f);

            out.write(mycode.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String readFromFile() {
        String pathRoot =Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/.nodeUpdateFolder/.nodeUpdateFile.txt";
        String code = "";
        try {
            File myFile = new File(pathRoot);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "";
            while ((aDataRow = myReader.readLine()) != null) {
                code += aDataRow;
            }
            Log.e("it is code file",code);
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    public LiveData<String> toDeviceUpdate(String ip ){
        final MutableLiveData<String> results = new MutableLiveData<>();
        toDeviceUpdateObservable(new BaseRequest(AppConstants.DEVICE_IS_READY_FOR_UPDATE),ip).subscribe(toDeviceUpdateObserver(results));
        return results;
    }

    private Observable<DeviceBaseResponse> toDeviceUpdateObservable(BaseRequest baseRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deviceUpdate(getDeviceAddress(ip), baseRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<DeviceBaseResponse> toDeviceUpdateObserver(MutableLiveData<String> results){
        return new DisposableObserver<DeviceBaseResponse>() {

            @Override
            public void onNext(@NonNull DeviceBaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getCmd());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e instanceof SocketTimeoutException){
                    results.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


    @SuppressLint("CheckResult")
    public LiveData<String> toDeviceDoUpdate(String cmd, List<String> codeList, String ip){
        final MutableLiveData<String> results = new MutableLiveData<>();
        Observable.fromIterable(codeList)
                .concatMap(s -> toDeviceDoUpdateObservable(new UpdateDeviceRequest(cmd,s),ip))
                .takeWhile(deviceBaseResponse -> {
                    Log.e("////////////" ," ////////: "+ deviceBaseResponse);
                    if(deviceBaseResponse.getCmd().equals(AppConstants.DEVICE_UPDATE_CODE_WROTE)){
                        return true;
                    }
                    return false;
                }).subscribe(toDeviceDoUpdateObserver(results));
        return results;
    }

    private Observable<DeviceBaseResponse> toDeviceDoUpdateObservable(UpdateDeviceRequest updateDeviceRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService.deviceDoUpdate(getDeviceAddress(ip), updateDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<DeviceBaseResponse> toDeviceDoUpdateObserver(MutableLiveData<String> results){
        return new DisposableObserver<DeviceBaseResponse>() {

            @Override
            public void onNext(@NonNull DeviceBaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getCmd());

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e instanceof SocketTimeoutException){
                    results.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public LiveData<ArrayList<String>> toDeviceAllFilesList(String cmd,String ip){
        final MutableLiveData<ArrayList<String>> results = new MutableLiveData<>();
        toDeviceAllFilesListObservable(new BaseRequest(cmd),ip).subscribe(toDeviceAllFilesListObserver(results));
        return results;
    }
    private Observable<AllFilesListResponse> toDeviceAllFilesListObservable(BaseRequest baseRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deviceFileList("http://10.0.3.2/DeviceGetFileList.php", baseRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<AllFilesListResponse> toDeviceAllFilesListObserver(MutableLiveData<ArrayList<String>> results){
        return new DisposableObserver<AllFilesListResponse>() {

            @Override
            public void onNext(@NonNull AllFilesListResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getFiles());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e instanceof SocketTimeoutException){
                    ArrayList<String> res=new ArrayList<>();
                    res.add(AppConstants.SOCKET_TIME_OUT);
                    results.postValue(res);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


    public LiveData<String> sendFilesToDevicePermit(String request){
        final MutableLiveData<String> results = new MutableLiveData<>();
        SendFilesToDevicePermitObservable(new SendFilesToDevicePermitRequest(request)).subscribe(SendFilesToDevicePermitObserver(results));
        return results;
    }
    private Observable<SendFilesToDevicePermitResponse> SendFilesToDevicePermitObservable(SendFilesToDevicePermitRequest sendFilesToDevicePermitRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deviceSendFilePermit("http://10.0.3.2/YesNoApi.php", sendFilesToDevicePermitRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<SendFilesToDevicePermitResponse> SendFilesToDevicePermitObserver(MutableLiveData<String> results){
        return new DisposableObserver<SendFilesToDevicePermitResponse>() {

            @Override
            public void onNext(@NonNull SendFilesToDevicePermitResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getPermit());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e instanceof SocketTimeoutException){
                    results.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


}
