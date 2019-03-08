package rayan.rayanapp.App;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import org.json.JSONObject;

import java.util.List;

import rayan.rayanapp.Activities.LoginActivity;
import rayan.rayanapp.Data.NetworkConnectionLiveData;
//<<<<<<< HEAD
import rayan.rayanapp.Receivers.LanguageDetailsChecker;
import rayan.rayanapp.Receivers.NetworkStateChangeReceiver;
import rayan.rayanapp.RxBus.DevicesAccessibilityBus;
//=======
import rayan.rayanapp.R;
//>>>>>>> 61f7df95c05f5e7b5402a088a45aa1e4642821eb
import rayan.rayanapp.RxBus.NetworkConnectionBus;
import rayan.rayanapp.RxBus.UDPMessageRxBus;
import rayan.rayanapp.Persistance.PrefManager;
import rayan.rayanapp.RxBus.WifiScanResultsBus;
import rayan.rayanapp.Util.JsonMaker;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class RayanApplication extends Application {
    private static Context context;
    private UDPMessageRxBus bus;
    private WifiScanResultsBus wifiBus;
    private JsonMaker jsonMaker;
    private static PrefManager pref;
    private NetworkConnectionLiveData networkConnectionLiveData;
    private NetworkConnectionBus networkBus;
    private DevicesAccessibilityBus devicesAccessibilityBus;
    private String currentSSID;
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iransans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        wifiBus = new WifiScanResultsBus();
        devicesAccessibilityBus = new DevicesAccessibilityBus(this);
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Dosis_Regular.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );
//        networkReceiver = new NetworkStateChangeReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        registerReceiver(networkReceiver,intentFilter);
        networkConnectionLiveData = new NetworkConnectionLiveData(this);
        networkBus = new NetworkConnectionBus();
        context = this;
        bus = new UDPMessageRxBus();
        pref = new PrefManager();
        jsonMaker = new JsonMaker();
        if (!pref.isLoggedIn()){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

//        Intent detailsIntent =  new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
//        sendOrderedBroadcast(
//                detailsIntent, null, new LanguageDetailsChecker(), null, Activity.RESULT_OK, null, null);

    }

    public String getCurrentSSID() {
        return currentSSID;
    }

    public void setCurrentSSID(String currentSSID) {
        this.currentSSID = currentSSID;
    }

    public NetworkConnectionBus getNetworkBus() {
        return networkBus;
    }

    public WifiScanResultsBus getWifiBus() {
        return wifiBus;
    }

    public JSONObject getJson(String cmd, List<String> values){
        return jsonMaker.getJson(cmd, values);
    }

    public DevicesAccessibilityBus getDevicesAccessibilityBus() {
        return devicesAccessibilityBus;
    }

    public UDPMessageRxBus getBus(){
        return bus;
    }

    public NetworkConnectionLiveData getNetworkStatus(){
        return networkConnectionLiveData;
    }

    public int getVersionCode() {
        int code = 0;
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            code = pi.versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public String getGateway() {
        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        DhcpInfo d = wm.getDhcpInfo();
        return __formatString(d.gateway);
    }

    private String __formatString(int value) {
        String strValue = "";
        byte[] ary = __intToByteArray(value);
        for (int i = ary.length - 1; i >= 0; i--) {
            strValue += (ary[i] & 0xFF);
            if (i > 0) {
                strValue += ".";
            }
        }
        return strValue;
    }

    private byte[] __intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte)((value >>> offset) & 0xFF);
        }
        return b;
    }

    public static Context getContext(){
        return context;
    }
    public static PrefManager getPref(){
        return pref;
    }




}
