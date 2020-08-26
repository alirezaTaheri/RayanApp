package rayan.rayanapp.Util;

import android.view.View;
import android.view.ViewGroup;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import rayan.rayanapp.Helper.Encryptor;

public class AppConstants {
    public final static String MQTT = "mqtt";
    public final static String UDP = "udp";
    public final static int UDP_SEND_PORT = 2000;
    public final static int UDP_RECEIVE_PORT = 3000;
    public final static int FTP_PORT = 21;
    public final static int HTTP_TO_DEVICE_PORT = 3000;
    public final static int UDP_MESSAGING_TIMEOUT = 4000;
    public final static int MQTT_MESSAGING_TIMEOUT = 4000;
    public final static int HTTP_MESSAGING_TIMEOUT = 4000;
    public final static int TOGGLE_ANIMATION_TIMEOUT = 4000;
    public final static int ATTACH_MQTT_REQ_TO_TOPIC_TIMEOUT = 4;

    public final static String MESSAGE_ROUTE_UDP = "UDP";
    public final static String MESSAGE_ROUTE_TCP = "TCP";
    public final static String MESSAGE_ROUTE_HTTP = "HTTP";
    public final static String MESSAGE_ROUTE_MQTT = "MQTT";
//    public final static String MQTT_HOST = "api.rayansmarthome.ir";
//    public final static int MQTT_PORT = 1883;
//    public final static String MQTT_HOST = "api2.mahdiesrafili.ir";
    public final static String MQTT_HOST = "api2.rayansmarthome.ir";
//    public final static String MQTT_HOST = "api3.mahdiesrafili.ir";
//    public final static String MQTT_HOST = "157.119.190.160";
    public final static int MQTT_PORT_SSL = 8883;
    public final static int MQTT_PORT = 1883;
    //New Devices
    public final static String NEW_DEVICE_TYPE_SWITCH = "switch";
    public final static String NEW_DEVICE_TYPE_PLUG = "plug";
    public final static String NEW_DEVICE_TOGGLE_CMD = "toggle";
    public final static String NEW_DEVICE_ITET = "ITET";
    public final static String NEW_DEVICE_ON_STATUS = "2";
    public final static String NEW_DEVICE_OFF_STATUS = "1";
    public final static String NEW_DEVICE_PHV_TRUE = "phv_true";
    public final static String NEW_DEVICE_PHV_FALSE = "phv_false";
    public final static String NEW_DEVICE_PHV_VERIFIED = "phv_verified";
    public final static String NEW_DEVICE_PHV_START = "phv_start";
    public final static String NEW_DEVICE_PHV_TIMEOUT = "phv_timeout";
    public final static String NEW_DEVICE_config = "config";
    public final static String PRIMARY_CONFIG_TRUE = "primary_config_true";
    public final static String PRIMARY_CONFIG_FALSE = "primary_config_false";
    public final static String EXPIRED = "EXPIRED";
    public final static String NEW_DEVICE_IP = "192.168.4.1";
//    public final static String NEW_DEVICE_IP = "192.168.137.1";
    public final static String GET_VERSION = "version";
    public final static String NAMING_PREFIX_PIN1 = "_1";
    public final static String NAMING_PREFIX_PIN2 = "_2";
    public final static String NAMING_PREFIX_PIN1_PIN2 = "_1_2";

    //Messages
    public final static String TO_DEVICE_NODE = "NODE?";
    public final static String TO_DEVICE_TLMS = "TLMS";
    public final static String TO_DEVICE_VERIFY = "verify";
    public final static String TO_DEVICE_NOT_VERIFY = "not_verify";
    public final static String FROM_DEVICE_VERIFY_DONE = "verify_done";
    public final static String OPERATION_DONE = "Done";
    public final static String DEVICE_TOGGLE = "tgl";
    public final static String ERROR = "error";
    public final static String FORBIDDEN = "Forbidden";
    public final static int MOBILE_DATA = 0;
    public final static int WIFI_NETWORK = 1;
    public final static int VPN_NETWORK = 17;
    public final static String MOBILE = "MOBILE";
    public final static String WIFI = "WIFI";
    public final static String NOT_CONNECTED = "NOT_CONNECTED";
    public final static int NOT_CONNECTED_NETWORK = -1;
    public final static String NO_HTTP = "no_http";


    public final static String STWORD = "stword";
    public final static String STWORD_HEADER = "header#stword";
    public final static String UNKNOWN_HEADER = "Unknown_header";
    //Operations
    public final static String ON_1 = "turn_on_1";
    public final static String ON_2 = "turn_on_2";
    public final static String OFF_1 = "turn_off_1";
    public final static String OFF_2 = "turn_off_2";
    public final static String ON_1_ON_2 = "ON_1_ON_2";
    public final static String OFF_1_ON_2 = "OFF_1_ON_2";
    public final static String OFF_1_OFF_2 = "OFF_1_OFF_2";
    public final static String ON_1_OFF_2 = "ON_1_OFF_2";

    //ConnectionStatus
    public final static String ON_STATUS = "on";
    public final static String OFF_STATUS = "off";

    /**
     * Device Settings
     */
    public final static String CHANGE_NAME_FALSE = "change_hname_false";
    public final static String CHANGE_NAME_TRUE = "change_hname_true";
    public final static String SOCKET_TIME_OUT = "SocketTimeoutException";
    public final static String CONNECT_EXCEPTION = "ConnectException";
    public final static String UNKNOWN_EXCEPTION = "UnknownException";
    public final static String UNKNOWN_HOST_EXCEPTION = "UnknownHostException";
    public final static String SETTINGS = "settings";
    public final static String END_SETTINGS = "end_settings";
    public final static String SET_TOPIC_MQTT = "MQTT";
    public final static String CHANGE_WIFI = "change_wifi";
    public final static String CHANGING_WIFI = "changing_wifi";
    public final static String SET_TOPIC_MQTT_Response = "MQTTR";
    public final static String DEVICE_CONNECTED_STYLE = "connected";
    public final static String DEVICE_STANDALONE_STYLE = "standalone";
    public final static String FACTORY_RESET = "factory_reset";
    public final static String FACTORY_RESET_DONE = "factory_reseted";
    public final static String DEVICE_PRIMARY_PASSWORD = "12345678";
    public final static String DEVICE_TYPE_SWITCH_1 = "switch_1";
    public final static String DEVICE_TYPE_SWITCH_2 = "switch_2";
    public final static String DEVICE_TYPE_TOUCH_2 = "touch_2";
    public final static String DEVICE_TYPE_PLUG = "plug";
    public final static String DEVICE_TYPE_DEVICE = "device";
    public final static String DEVICE_TYPE_RemoteHub = "remote_hub";
    public final static String DEVICE_TYPE_Remote = "remote";
    public final static String REMOTE_TYPE_AC = "AC";
    public final static String REMOTE_TYPE_TV = "TV";
    public final static String BaseDeviceType_SWITCH_1 = "switch_1";
    public final static String BaseDeviceType_SWITCH_2 = "switch_2";
    public final static String BaseDeviceType_TOUCH_2 = "touch_2";
    public final static String BaseDeviceType_PLUG = "plug";
    public final static String BaseDeviceType_REMOTE_HUB = "remoteHub";
    public final static String BaseDeviceType_REMOTE = "remote";

    /**
     * API
     */
    public final static String ADMIN_TYPE = "admin";
    public final static String USER_TYPE = "user";
    public final static String DUPLICATE_USER = "duplicate user";
    public final static String UNKNOWN_SSID = "Unknown SSID";
    public final static String NULL_SSID = "Null SSID";
    public final static String UNKNOWN_IP = "Unknown IP";
    public final static String SUCCESS_DESCRIPTION = "success";
    public final static String ERROR_DESCRIPTION = "error";
    public final static String USER_NOT_FOUND_RESPONSE = "User not found";
    public final static String WRONG_PASSWORD_RESPONSE = "Wrong password";
    public final static String DEVICE_IS_READY_FOR_UPDATE = "update";
    public final static String DEVICE_READY_FOR_UPDATE = "update";
    public final static String DEVICE_UPDATE_CODE_WROTE = "codes_wrote";
    public final static String DEVICE_UPDATE_END = "update_end";
    public final static String DEVICE_UPDATE_DONE = "updating_done";
    public final static String DEVICE_DO_UPDATE = "update";
    public final static String DEVICE_ALL_FILES_LIST = "all_files_list";
    public final static String DEVICE_SEND_FILES_PERMIT = "send_files_to_device?";

    /**
     * Dialog Types
     */
    public final static String DIALOG_PROVIDE_INTERNET = "dialog_provide_internet";
    public final static String DIALOG_ALERT = "dialog_alert";

    /**
     * URL Params
     */




    public final static String ERROR_OCCURRED = "error_occurred";

    public static String getDeviceAddress(String ip){
        return "http://"+ip+":"+AppConstants.HTTP_TO_DEVICE_PORT;
//        return "http://192.168.137.1/test.php";
    }

    public static void disableEnableControls(boolean enable, ViewGroup vg){
        vg.setEnabled(enable);
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup){
                disableEnableControls(enable, (ViewGroup)child);
            }
        }
    }

    public static String sha1(String s, String keyString) throws
            UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);

        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));

        return new String( org.apache.commons.net.util.Base64.encodeBase64(bytes) );
    }

    public static Map<String, String> separateStword(String stword, String secret, String header){
        Map<String, String> data = new HashMap<>();
        data.put(STWORD, Encryptor.decrypt(stword, secret));
        data.put(STWORD_HEADER, header.concat("#").concat(Encryptor.decrypt(stword, secret)));
        return data;
    }

}