package rayan.rayanapp.App;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import rayan.rayanapp.Helper.DeviceAnimator;
import rayan.rayanapp.Helper.MessageTransmissionDecider;
import rayan.rayanapp.Helper.MqttMessagesController;
import rayan.rayanapp.Helper.MqttSubscriptionController;
import rayan.rayanapp.Helper.RequestManager;
import rayan.rayanapp.Helper.ScenariosMqttMessagesController;
import rayan.rayanapp.Helper.SendMessageToDevice;
import rayan.rayanapp.Persistance.PrefManager;
import rayan.rayanapp.R;
import rayan.rayanapp.RxBus.DevicesAccessibilityBus;
import rayan.rayanapp.RxBus.NetworkConnectionBus;
import rayan.rayanapp.RxBus.UDPMessageRxBus;
import rayan.rayanapp.RxBus.WifiScanResultsBus;
import rayan.rayanapp.Util.JsonMaker;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class RayanApplication extends Application {
    private static final String TAG = "RayanApplication";
    private static Context context;
    private UDPMessageRxBus bus;
    private WifiScanResultsBus wifiBus;
    private JsonMaker jsonMaker;
    private static PrefManager pref;
    private NetworkConnectionBus networkBus;
    private DevicesAccessibilityBus devicesAccessibilityBus;
    private String currentSSID;
    private MessageTransmissionDecider mtd;
    private SendMessageToDevice sendMessageToDevice;
    private RequestManager requestManager;
    private Locale locale = null;
    private MqttMessagesController mqttMessagesController;
    private ScenariosMqttMessagesController scenariosMqttMessagesController;
    private MqttSubscriptionController msc;
    @Override
    public void onCreate() {
        super.onCreate();
        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = "en";
        if (! config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
//        Fabric.with(this, new Crashlytics());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSans1.ttf")
//                .setDefaultFontPath("fonts/aseman.ttf")
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
        Log.e("seekbarthis","in the rayan application creating networkconnectionlivedata");
        networkBus = new NetworkConnectionBus();
        context = this;
        bus = new UDPMessageRxBus();
        pref = new PrefManager();
        jsonMaker = new JsonMaker();
        mtd = new MessageTransmissionDecider(this);
        sendMessageToDevice = new SendMessageToDevice(this);
        requestManager = new RequestManager();
        msc = new MqttSubscriptionController(this);
        mqttMessagesController = new MqttMessagesController();
        scenariosMqttMessagesController = new ScenariosMqttMessagesController();
//        Intent detailsIntent =  new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
//        sendOrderedBroadcast(
//                detailsIntent, null, new LanguageDetailsChecker(), null, Activity.RESULT_OK, null, null);
    }


    public MqttSubscriptionController getMsc() {
        return msc;
    }

    public MessageTransmissionDecider getMtd() {
        return mtd;
    }

    public SendMessageToDevice getSendMessageToDevice() {
        return sendMessageToDevice;
    }

    public RequestManager getRequestManager() {
        return requestManager;
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

    public JSONObject getJSON(String cmd, List<String> values){
        return jsonMaker.getJSON(cmd, values);
    }

    public JsonObject getJson(String cmd, List<String> values){
        return jsonMaker.getJson(cmd, values);
    }

    public DevicesAccessibilityBus getDevicesAccessibilityBus() {
        return devicesAccessibilityBus;
    }

    public UDPMessageRxBus getBus(){
        return bus;
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public MqttMessagesController getMqttMessagesController() {
        return mqttMessagesController;
    }
    public ScenariosMqttMessagesController getScenariosMqttMessagesController() {
        return scenariosMqttMessagesController;
    }


    private static RequestQueue mRequestQueue;
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public static void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
