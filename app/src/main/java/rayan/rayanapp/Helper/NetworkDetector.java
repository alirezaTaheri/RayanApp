package rayan.rayanapp.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Listeners.NetworkConnectivityListener;
import rayan.rayanapp.Util.AppConstants;

public class NetworkDetector {
    NetworkConnectivityListener listener;
    @SuppressLint("CheckResult")
    public NetworkDetector(Context context, NetworkConnectivityListener listener) {
        this.listener = listener;
        ReactiveNetwork
                .observeNetworkConnectivity(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    Log.e("NetworkDetector", "From context: " + context + " connectivityIs: " + connectivity);
                    switch (connectivity.type()){
                        case AppConstants.WIFI_NETWORK:
                            String extraInfo = connectivity.extraInfo();
                            if (connectivity.extraInfo().charAt(connectivity.extraInfo().length()-1) == connectivity.extraInfo().charAt(0) && String.valueOf(connectivity.extraInfo().charAt(0)).equals("\""))
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
}
