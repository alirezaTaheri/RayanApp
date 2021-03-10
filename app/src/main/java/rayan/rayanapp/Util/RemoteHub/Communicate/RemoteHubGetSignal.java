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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Helper.RayanUtils;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.device.RemoteHubBaseResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.ToggleDeviceResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SingleLiveEvent;
import retrofit2.Response;

public class RemoteHubGetSignal {
    private final String TAG = "RemoteHubGetSignal";
    public SingleLiveEvent<String> get_signal_request(RemoteHub remoteHub){
        SingleLiveEvent<String> callback = new SingleLiveEvent<>();
        send_request_observable(remoteHub, AppConstants.REMOTE_HUB_ENTER_SETTINGS, callback)
                .subscribe(new Observer<Response<String>>() {
                    @Override public void onSubscribe(Disposable d) { Log.e(TAG, "Staring To Send Request"); }

                    @Override public void onNext(Response<String> stringResponse) { Log.e(TAG, "On Next Something Received: " + stringResponse); }

                    @Override public void onError(Throwable e) { Log.e(TAG, "An Error Occurred: " + e);e.printStackTrace(); }

                    @Override public void onComplete() { Log.e(TAG, "Sending is Completed"); }
                });
        return callback;
    }

    public Observable<Response<String>> send_request_observable(RemoteHub remoteHub, String request_type, SingleLiveEvent<String> callback){
        ApiService apiService = ApiUtils.getApiServiceScalar();
        return Observable.just(remoteHub)
                .flatMap(i -> {
                    switch (request_type) {
                        case AppConstants.REMOTE_HUB_ENTER_SETTINGS:
                            return apiService.enter_settings_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_ENTER_SETTINGS), new BaseRequest(remoteHub.getEncryptedStatusWordToGo()));
                        case AppConstants.REMOTE_HUB_ENTER_LEARN:
                            return apiService.enter_learn_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_ENTER_LEARN), new BaseRequest(remoteHub.getEncryptedStatusWordToGo()));
                        default: return apiService.enter_learn_remoteHub_v1(AppConstants.getDeviceAddress(remoteHub.getIp(), AppConstants.REMOTE_HUB_ENTER_LEARN), new BaseRequest(remoteHub.getEncryptedStatusWordToGo()));
                    }
                }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .timeout(4, TimeUnit.SECONDS)
                .takeWhile(response -> {
                    if (response.headers().get(AppConstants.AUTH) != null){
                        Log.e(TAG, "Auth Is Not NUll");
                        if (sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))){
                            Log.e(TAG, "HMACs Are Equal");
                            RemoteHubBaseResponse wrappedResponse = RayanUtils.convertToObject(ToggleDeviceResponse.class, response.body());
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
                callback.setValue(AppConstants.WRONG_STWORD);
                return true;
            case AppConstants.NOT_IN_LEARN_ERROR:
                Log.e(TAG, "RemoteHub is in not in LEARN Mode");
                callback.setValue(AppConstants.NOT_IN_LEARN_ERROR);
                return false;
            case AppConstants.LEARN_TIMEOUT:
                Log.e(TAG, "LEARN timeout");
                callback.setValue(AppConstants.LEARN_TIMEOUT);
                return false;
            case AppConstants.WRONG_FORMAT_ERROR:
                Log.e(TAG, "Wrong Raw Data Format");
                callback.setValue(AppConstants.WRONG_FORMAT_ERROR);
                return false;
            case AppConstants.SUCCESS_RESULT:
                Log.e(TAG, "Successful Result.");
                callback.setValue(AppConstants.SUCCESS_RESULT);
                return false;
            default:
                Log.e(TAG, "UNKNOWN Error Occurred");
                callback.setValue(AppConstants.UNKNOWN_ERROR);
                return false;
        }
    }
    private String sha1(String s, String keyString) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException { SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");mac.init(key);byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));return new String( Base64.encodeBase64(bytes) ); }
}
