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
import rayan.rayanapp.Helper.RayanUtils;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.SendDataRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.device.RemoteHubBaseResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SingleLiveEvent;
import retrofit2.Response;

public class RemoteHubSendData {
    private final String TAG = "RemoteHubSendData";
    public SingleLiveEvent<String> send_data(RemoteHub remoteHub, String msg){
        SingleLiveEvent<String> callback = new SingleLiveEvent<>();
        send_data_observable(remoteHub, callback, new SendDataRemoteHubRequest(remoteHub.getEncryptedStatusWordToGo(), msg)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Response<String>>() {
            @Override public void onSubscribe(Disposable d) { Log.e(TAG, "Staring To Send Data"); }

            @Override public void onNext(Response<String> stringResponse) { Log.e(TAG, "On Next Something Received: " + stringResponse); }

            @Override public void onError(Throwable e) { Log.e(TAG, "An Error Occurred: " + e);e.printStackTrace();callback.setValue(handle_error(e));}

            @Override public void onComplete() { Log.e(TAG, "Sending is Completed"); }
        });
        return callback;
    }

    private Observable<Response<String>> send_data_observable(RemoteHub remoteHub, SingleLiveEvent<String> callback, SendDataRemoteHubRequest request){
        ApiService apiService = ApiUtils.getApiServiceScalar();
        return Observable.just(remoteHub)
                .flatMap(a -> apiService.send_data_remoteHub_v1(AppConstants.getDeviceAddress(a.getIp(), AppConstants.REMOTE_HUB_SEND_DATA), request))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .timeout(4, TimeUnit.SECONDS)
                .takeWhile(response -> {
                    if (response.headers().get(AppConstants.AUTH) != null){
                        Log.e(TAG, "Auth Is Not NUll");
                        if (!sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))){
                            Log.e(TAG, "HMACs Are Equal");
                            RemoteHubBaseResponse wrappedResponse = RayanUtils.convertToObject(RemoteHubBaseResponse.class, response.body());
//                            if (mqttBackup.get(device.getChipId()+"_1") != null && !mqttBackup.get(device.getChipId()+"_1").isDisposed())
//                                mqttBackup.get(device.getChipId()+"_1").dispose();
                            remoteHub.decrypt_setStatusWord(wrappedResponse.getSTWORD());
                            String res = wrappedResponse.getResult()==null?"":wrappedResponse.getResult();
                            String error = wrappedResponse.getError()==null?"":wrappedResponse.getError();
                            String total = res+error;
                            Log.e(TAG, "What Should I do?: " + total);
                            if (total.equals(AppConstants.WRONG_STWORD)) {
                                Log.e(TAG, "Wrong STWORD, Retrying");
                                callback.setValue(AppConstants.WRONG_STWORD);
                                return true;
                            } else if (total.equals(AppConstants.LEARN_ERROR)) {
                                Log.e(TAG, "RemoteHub is in LEARN Mode");
                                callback.setValue(AppConstants.LEARN_ERROR);
                                return false;
                            } else if (total.equals(AppConstants.WRONG_FORMAT_ERROR)) {
                                Log.e(TAG, "Wrong Raw Data Format");
                                callback.setValue(AppConstants.WRONG_FORMAT_ERROR);
                                return false;
                            } else if (total.equals(AppConstants.SUCCESS_RESULT)) {
                                Log.e(TAG, "Successful Result.");
                                callback.setValue(AppConstants.SUCCESS_RESULT);
                                return false;
                            }
                        }else Log.e(TAG, "HMACs Are NOT Equal");
                    }
                    else Log.e(TAG, "Auth IS NULL");
                    return false;
                });
    }


    private String handle_error(Throwable e){
        switch (e.getClass().getSimpleName()){
            case AppConstants.TIME_OUT_EXCEPTION:
                return AppConstants.TIME_OUT_EXCEPTION;
            case AppConstants.UNKNOWN_HOST_EXCEPTION:
                return AppConstants.UNKNOWN_HOST_EXCEPTION;
            default: return AppConstants.UNKNOWN_EXCEPTION;
        }
    }
    private String sha1(String s, String keyString) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException { SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");Mac mac = Mac.getInstance("HmacSHA1");mac.init(key);byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));return new String( Base64.encodeBase64(bytes) ); }
}
