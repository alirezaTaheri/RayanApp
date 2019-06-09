package rayan.rayanapp.Helper;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Listeners.NetworkConnectivityListener;
import rayan.rayanapp.Util.AppConstants;

public class NetworkDetector {
//    NetworkConnectivityListener listener;
//    Context context;
    Disposable d;
    @SuppressLint("CheckResult")
    public NetworkDetector(Context context, NetworkConnectivityListener listener) {
//        this.listener = listener;
//        this.context = context;
        ReactiveNetwork
                .observeNetworkConnectivity(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    Log.e("NetworkDetector", "From context: " + context + " connectivityIs: " + connectivity);
                    switch (connectivity.type()){
                        case AppConstants.WIFI_NETWORK:
                            String extraInfo = connectivity.extraInfo();
                            if (extraInfo != null && connectivity.extraInfo().charAt(connectivity.extraInfo().length()-1) == connectivity.extraInfo().charAt(0) && String.valueOf(connectivity.extraInfo().charAt(0)).equals("\""))
                                extraInfo = connectivity.extraInfo().substring(1,extraInfo.length()-1);
                            listener.wifiNetwork(connectivity.state().equals(NetworkInfo.State.CONNECTED), extraInfo);
                            break;
                        case AppConstants.MOBILE_DATA:
                            listener.mobileNetwork(connectivity.state().equals(NetworkInfo.State.CONNECTED));
                            break;
                        case AppConstants.NOT_CONNECTED_NETWORK:
                            listener.notConnected();
                            break;
                    }
                });
    }
    public Disposable getNetworkDetectorDisposable(){
        return d;
    }
//    @Override
//    protected void onActive() {
//        super.onActive();
//        Log.e(this.getClass().getCanonicalName(), "Enabling Network Detector...");
////        try {
////            Thread.sleep(100);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//        d = ReactiveNetwork
//                .observeNetworkConnectivity(context)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(connectivity -> {
//                    Log.e("NetworkDetector", "From context: " + context + " connectivityIs: " + connectivity);
//                    switch (connectivity.type()){
//                        case AppConstants.WIFI_NETWORK:
//                            String extraInfo = connectivity.extraInfo();
//                            if (extraInfo != null && connectivity.extraInfo().charAt(connectivity.extraInfo().length()-1) == connectivity.extraInfo().charAt(0) && String.valueOf(connectivity.extraInfo().charAt(0)).equals("\""))
//                                extraInfo = connectivity.extraInfo().substring(1,extraInfo.length()-1);
//                                listener.wifiNetwork(connectivity.state().equals(NetworkInfo.State.CONNECTED), extraInfo);
//                            break;
//                        case AppConstants.MOBILE_DATA:
//                            listener.mobileNetwork(connectivity.state().equals(NetworkInfo.State.CONNECTED));
//                            break;
//                        case AppConstants.NOT_CONNECTED_NETWORK:
//                            listener.notConnected();
//                            break;
//                    }
//                });
//    }
//    @Override
//    protected void onInactive() {
//        super.onInactive();
//        Log.e(this.getClass().getCanonicalName(), "onInactive Network Detector...");
//        if (d != null) {
//            Log.e(this.getClass().getCanonicalName(), "Disabling Network Detector...");
//            d.dispose();
//        }
//    }

}
