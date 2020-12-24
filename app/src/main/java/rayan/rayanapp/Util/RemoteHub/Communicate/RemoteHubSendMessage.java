package rayan.rayanapp.Util.RemoteHub.Communicate;

import android.util.Log;

import org.apache.commons.net.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Exceptions.EnterLearnException;
import rayan.rayanapp.Exceptions.GetSignalException;
import rayan.rayanapp.Exceptions.RayanException;
import rayan.rayanapp.Helper.RayanUtils;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.device.RemoteHubBaseResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.LiveResponse;
import rayan.rayanapp.Util.SingleLiveEvent;
import retrofit2.Response;

public class RemoteHubSendMessage {

    private final String TAG = "RemoteHubSendMessage";
    public SingleLiveEvent<String> send_request(RemoteHub remoteHub, String request_type){
        SingleLiveEvent<String> callback = new SingleLiveEvent<>();
        send_request_observable(remoteHub, request_type, callback)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override public void onSubscribe(Disposable d) { Log.e(TAG, "Staring To Send Request"); }

                    @Override public void onNext(Response<String> stringResponse) { Log.e(TAG, "On Next Something Received: " + stringResponse); }

                    @Override public void onError(Throwable e) { Log.e(TAG, "An Error Occurred: " + e);e.printStackTrace();callback.setValue("BBBB"); }

                    @Override public void onComplete() { Log.e(TAG, "Sending is Completed"); }
                });
        return callback;
    }
    String signal;
    public SingleLiveEvent<LiveResponse> try_enter_learn_get_IR(RemoteHub remoteHub){
        SingleLiveEvent<LiveResponse> callback = new SingleLiveEvent<>();
        Observable.concat(enter_learn_observable(remoteHub, callback), get_IR_signal_observable(remoteHub, callback)
        ,exit_learn_observable(remoteHub, callback))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override public void onSubscribe(Disposable d) { Log.e(TAG, "Staring To Send Request"); }

                    @Override public void onNext(Response<String> stringResponse) { Log.e(TAG, "On Next Something Received: " + stringResponse); }

                    @Override public void onError(Throwable e) { Log.e(TAG, "An Error Occurred: " + e);e.printStackTrace();callback.setValue(try_enter_learn_get_IR_handle_error(e)); }

                    @Override public void onComplete() { Log.e(TAG, "Sending is Completed");callback.setValue(new LiveResponse(true, signal,null)); }
                });
        return callback;
    }
    private Observable<Response<String>> enter_learn_observable(RemoteHub remoteHub, SingleLiveEvent<LiveResponse> callback){
        ApiService apiService = ApiUtils.getApiServiceScalar();
        Log.e(TAG, "RemoteHub:"+remoteHub);
        if (remoteHub.getState().equals(AppConstants.REMOTE_HUB_STATE_LEARN))
            return Observable.empty();
        return Observable.just(remoteHub)
                .flatMap(re -> apiService.enter_learn_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_ENTER_LEARN), new BaseRequest(remoteHub.getEncryptedStatusWordToGo())))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .timeout(4, TimeUnit.SECONDS)
                .takeWhile(response -> {
                    Log.e(TAG, "Auth: "+response.headers().get(AppConstants.AUTH));
                    if (response.headers().get(AppConstants.AUTH) != null && sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))){
                        Log.e(TAG, "HMACs Are Equal");
                        RemoteHubBaseResponse wrappedResponse = RayanUtils.convertToObject(RemoteHubBaseResponse.class, response.body());
                        remoteHub.decrypt_setStatusWord(wrappedResponse.getSTWORD());
                        String res = wrappedResponse.getResult()==null?"":wrappedResponse.getResult();
                        String error = wrappedResponse.getError()==null?"":wrappedResponse.getError();
                        String total = res+error;
                        Log.e(TAG, "What Should I do?: " + total);
                        return enter_learn_handle_response(total, remoteHub);
                    }
                    else Log.e(TAG, "Auth IS NULL");
                    return false;
                });
    }
    private Observable<Response<String>> get_IR_signal_observable(RemoteHub remoteHub, SingleLiveEvent<LiveResponse> callback){
        ApiService apiService = ApiUtils.getApiServiceScalar();
        return Observable.just(remoteHub)
                .flatMap(re -> apiService.get_IR_signal_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_GET_IR_SIGNAL), new BaseRequest(remoteHub.getEncryptedStatusWordToGo())))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .timeout(4, TimeUnit.SECONDS)
                .takeWhile(response -> {
                    Log.e(TAG, "Auth: "+response.headers().get(AppConstants.AUTH));
                    if (response.headers().get(AppConstants.AUTH) != null && sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))){
                        Log.e(TAG, "HMACs Are Equal");
                        RemoteHubBaseResponse wrappedResponse = RayanUtils.convertToObject(RemoteHubBaseResponse.class, response.body());
                        remoteHub.decrypt_setStatusWord(wrappedResponse.getSTWORD());
                        String res = wrappedResponse.getResult()==null?"":wrappedResponse.getResult();
                        String error = wrappedResponse.getError()==null?"":wrappedResponse.getError();
                        String total = res+error;
                        Log.e(TAG, "What Should I do?: " + total);
                        return get_IR_signal_handle_response(total, callback, remoteHub);
                    }
                    else Log.e(TAG, "Auth IS NULL");
                    return false;
                });
    }
    private Observable<Response<String>> exit_learn_observable(RemoteHub remoteHub, SingleLiveEvent<LiveResponse> callback){
        ApiService apiService = ApiUtils.getApiServiceScalar();
        return Observable.just(remoteHub)
                .flatMap(re -> apiService.exit_learn_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_EXIT_LEARN), new BaseRequest(remoteHub.getEncryptedStatusWordToGo())))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .timeout(4, TimeUnit.SECONDS)
                .takeWhile(response -> {
                    Log.e(TAG, "Auth: "+response.headers().get(AppConstants.AUTH));
                    if (response.headers().get(AppConstants.AUTH) != null && sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))){
                        Log.e(TAG, "HMACs Are Equal");
                        RemoteHubBaseResponse wrappedResponse = RayanUtils.convertToObject(RemoteHubBaseResponse.class, response.body());
                        remoteHub.decrypt_setStatusWord(wrappedResponse.getSTWORD());
                        String res = wrappedResponse.getResult()==null?"":wrappedResponse.getResult();
                        String error = wrappedResponse.getError()==null?"":wrappedResponse.getError();
                        String total = res+error;
                        Log.e(TAG, "What Should I do?: " + total);
                        return exit_learn_handle_response(total, callback, remoteHub);
                    }
                    else Log.e(TAG, "Auth IS NULL");
                    return false;
                });
    }

    private boolean enter_learn_handle_response(String total, RemoteHub remoteHub){
        Log.e(TAG, "enter_learn_handle_response() called with: total = [" + total + "]");
        switch (total) {
            case AppConstants.SUCCESS_RESULT:
                Log.e(TAG, "Successful Result.");
                return false;
            case AppConstants.WRONG_STWORD:
                Log.e(TAG, "Wrong STWORD, Retrying");
                return true;
            case AppConstants.ALREADY_LEARN_ERROR:
                Log.e(TAG, "ALREADY_LEARN_ERROR");
                remoteHub.setState(AppConstants.REMOTE_HUB_STATE_LEARN);
                return false;
            case AppConstants.FULL_LEARN_ERROR:
                Log.e(TAG, "FULL_LEARN_ERROR");
                remoteHub.setState("");
                RuntimeException e = new EnterLearnException(AppConstants.FULL_LEARN_ERROR);
                throw e;
            case AppConstants.SETTINGS_MODE_ERROR:
                Log.e(TAG, "SETTINGS_MODE_ERROR");
                remoteHub.setState("");
                throw new EnterLearnException(AppConstants.SETTINGS_MODE_ERROR);
            default: Log.e(TAG, "Unexpected Error: " + total);
                remoteHub.setState("");
                throw new RayanException(AppConstants.UNKNOWN_EXCEPTION);
        }
    }
    private boolean get_IR_signal_handle_response(String total, SingleLiveEvent<LiveResponse> callback, RemoteHub remoteHub){
        Log.e(TAG, "enter_learn_handle_response() called with: total = [" + total + "]");
        switch (total) {
            case AppConstants.WRONG_STWORD:
                Log.e(TAG, "Wrong STWORD, Retrying");
                return true;
            case AppConstants.NOT_IN_LEARN_ERROR:
                Log.e(TAG, "NOT_IN_LEARN_ERROR");
                remoteHub.setState("");
                throw new GetSignalException(AppConstants.NOT_IN_LEARN_ERROR);
            case AppConstants.LEARN_TIMEOUT:
                remoteHub.setState("");
                Log.e(TAG, "RayanException");
                throw new GetSignalException(AppConstants.LEARN_TIMEOUT);
            default:
                if (total.matches(AppConstants.LEARN_IR_SIGNAL_REGEX)){
                    Log.e(TAG, "Successful Result." + total);
                    remoteHub.setState(AppConstants.REMOTE_HUB_STATE_LEARN);
                    signal = total;
                } else{
                    Log.e(TAG, "UNKNOWN Error Occurred");
                    throw new RayanException(AppConstants.UNKNOWN_EXCEPTION);
                }
                return false;
        }
    }
    private boolean exit_learn_handle_response(String total, SingleLiveEvent<LiveResponse> callback, RemoteHub remoteHub){
        Log.e(TAG, "enter_learn_handle_response() called with: total = [" + total + "]");
        switch (total) {
            case AppConstants.SUCCESS_RESULT:
                Log.e(TAG, "Wrong STWORD, Retrying");
                return false;
            case AppConstants.WRONG_STWORD:
                Log.e(TAG, "Wrong STWORD, Retrying");
                return true;
            case AppConstants.NOT_IN_LEARN_ERROR:
                Log.e(TAG, "NOT_IN_LEARN_ERROR");
                remoteHub.setState("");
                return false;
            default:
                throw new RayanException(AppConstants.UNKNOWN_EXCEPTION);
        }
    }

    private Observable<Response<String>> send_request_observable(RemoteHub remoteHub, String request_type, SingleLiveEvent<String> callback){
        ApiService apiService = ApiUtils.getApiServiceScalar();
        return Observable.just(remoteHub)
                .flatMap(i -> {
                    switch (request_type) {
                        case AppConstants.REMOTE_HUB_ENTER_SETTINGS:
                            return apiService.enter_settings_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_ENTER_SETTINGS), new BaseRequest(remoteHub.getEncryptedStatusWordToGo()));
                        case AppConstants.REMOTE_HUB_EXIT_LEARN:
                            return apiService.exit_learn_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_EXIT_LEARN), new BaseRequest(remoteHub.getEncryptedStatusWordToGo()));
                        case AppConstants.REMOTE_HUB_ENTER_LEARN:
                            return apiService.enter_learn_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_ENTER_LEARN), new BaseRequest(remoteHub.getEncryptedStatusWordToGo()));
                        case AppConstants.REMOTE_HUB_EXIT_SETTINGS:
                            return apiService.exit_settings_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_EXIT_SETTINGS), new BaseRequest(remoteHub.getEncryptedStatusWordToGo()));
                        case AppConstants.REMOTE_HUB_GET_IR_SIGNAL:
                            return apiService.get_IR_signal_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_GET_IR_SIGNAL), new BaseRequest(remoteHub.getEncryptedStatusWordToGo()));
                        default: return apiService.enter_learn_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_ENTER_LEARN), new BaseRequest(remoteHub.getEncryptedStatusWordToGo()));
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .timeout(4, TimeUnit.SECONDS)
                .takeWhile(response -> {
                    if (response.headers().get(AppConstants.AUTH) != null){
                        Log.e(TAG, "Auth Is Not NUll");
                        if (sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))){
                            Log.e(TAG, "HMACs Are Equal");
                            RemoteHubBaseResponse wrappedResponse = RayanUtils.convertToObject(RemoteHubBaseResponse.class, response.body());
//                            if (mqttBackup.get(device.getChipId()+"_1") != null && !mqttBackup.get(device.getChipId()+"_1").isDisposed())
//                                mqttBackup.get(device.getChipId()+"_1").dispose();
                            remoteHub.decrypt_setStatusWord(wrappedResponse.getSTWORD());
                            String res = wrappedResponse.getResult()==null?"":wrappedResponse.getResult();
                            String error = wrappedResponse.getError()==null?"":wrappedResponse.getError();
                            String total = res+error;
                            Log.e(TAG, "What Should I do?: " + total);
                            return handle_response(total, callback);
                        }else Log.e(TAG, "HMACs Are NOT Equal");
                    }
                    else Log.e(TAG, "Auth IS NULL");
                    return false;
                });
    }

    private boolean handle_response(String total, SingleLiveEvent<String> callback){
        switch (total) {
            case AppConstants.WRONG_STWORD:
                Log.e(TAG, "Wrong STWORD, Retrying");
                if(callback!=null)callback.setValue(AppConstants.WRONG_STWORD);
                return true;
            case AppConstants.LEARN_ERROR:
                Log.e(TAG, "RemoteHub is in LEARN Mode");
                if(callback!=null)callback.setValue(AppConstants.LEARN_ERROR);
                return false;
            case AppConstants.ALREADY_LEARN_ERROR:
                Log.e(TAG, "RemoteHub is Already in LEARN Mode");
                if(callback!=null)callback.setValue(AppConstants.ALREADY_LEARN_ERROR);
                return false;
            case AppConstants.FULL_LEARN_ERROR:
                Log.e(TAG, "Full LEARN Mode");
                if(callback!=null)callback.setValue(AppConstants.FULL_LEARN_ERROR);
                return false;
            case AppConstants.SETTINGS_MODE_ERROR:
                Log.e(TAG, "RemoteHub is in Settings MODE");
                if(callback!=null)callback.setValue(AppConstants.SETTINGS_MODE_ERROR);
                return false;
            case AppConstants.ALREADY_SETTINGS_MODE_ERROR:
                Log.e(TAG, "RemoteHub is already in Settings MODE");
                if(callback!=null)callback.setValue(AppConstants.ALREADY_SETTINGS_MODE_ERROR);
                return false;
            case AppConstants.FULL_SETTINGS_ERROR:
                Log.e(TAG, "RemoteHub Full Settings MODE");
                if(callback!=null)callback.setValue(AppConstants.FULL_SETTINGS_ERROR);
                return false;
            case AppConstants.NOT_IN_LEARN_ERROR:
                Log.e(TAG, "NOT_IN_LEARN_ERROR");
                if(callback!=null)callback.setValue(AppConstants.NOT_IN_LEARN_ERROR);
                return false;
            case AppConstants.WRONG_FORMAT_ERROR:
                Log.e(TAG, "Wrong Raw Data Format");
                if(callback!=null)callback.setValue(AppConstants.WRONG_FORMAT_ERROR);
                return false;
            case AppConstants.SUCCESS_RESULT:
                Log.e(TAG, "Successful Result.");
                if(callback!=null)callback.setValue(AppConstants.SUCCESS_RESULT);
                return false;
            default:
                if (total.matches(AppConstants.LEARN_IR_SIGNAL_REGEX)){
                    Log.e(TAG, "Successful Result." + total);
                    if(callback!=null)callback.setValue(total);
                } else{
                    Log.e(TAG, "UNKNOWN Error Occurred");
                    if(callback!=null)callback.setValue(AppConstants.UNKNOWN_ERROR);
                }
                return false;
        }
    }
    private LiveResponse try_enter_learn_get_IR_handle_error(Throwable e){
        Log.e(TAG, ""+e.getMessage());
        Log.e(TAG, ""+e.getClass().getName());
        if (e instanceof RayanException) {
            return new LiveResponse(false,null, (RayanException) e);
        }
        switch (e.getClass().getSimpleName()){
            case AppConstants.TIME_OUT_EXCEPTION:
                return new LiveResponse(false,null, new RayanException(AppConstants.TIME_OUT_EXCEPTION));
            case AppConstants.UNKNOWN_HOST_EXCEPTION:
                return new LiveResponse(false,null, new RayanException(AppConstants.UNKNOWN_HOST_EXCEPTION));
            default: return new LiveResponse(false,null, new RayanException(AppConstants.UNKNOWN_EXCEPTION));
        }
    }
    private String sha1(String s, String keyString) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException { SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");Mac mac = Mac.getInstance("HmacSHA1");mac.init(key);byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));return new String( Base64.encodeBase64(bytes) ); }
}
