package rayan.rayanapp.Util;

import android.util.Log;
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
    public final static String CURRENT_VERSION = "v2";
    public final static String[] SUPPORTED_TYPES = {AppConstants.BaseDeviceType_REMOTE_HUB, AppConstants.BaseDeviceType_SWITCH_1, AppConstants.BaseDeviceType_SWITCH_2, AppConstants.BaseDeviceType_TOUCH_2, AppConstants.BaseDeviceType_PLUG};
    public final static String[] REMOTE_HUB_VALID_VERSIONS = {"1.0"};
    public final static String[] SWITCH_VALID_VERSIONS = {"1.0", "2.0"};
    public final static String[] PLUG_VALID_VERSIONS = {"1.0", "2.0"};
    public final static String[] TOUCH_2_VALID_VERSIONS = {"1.0", "2.0"};
    public final static String[] AC_REMOTE_BUTTONS = {"Power","Temp Down","Temp Up","More","Mode","Air Volume"};
    public final static String[] TV_REMOTE_BUTTONS = {"Power","Home","Menu","Channel Up","Channel Down","Volume Up","Volume Down","Mute","Back","OK","Up","Down","Right","Left","More","Button 123"};
    public final static String LEARN_IR_SIGNAL_REGEX = "<IR\\+RECVRAW(\\+\\d+){2}(,\\d+)+>";
    public final static String CONNECT_TIMEOUT = "CONNECT_TIMEOUT";
    public final static String READ_TIMEOUT = "READ_TIMEOUT";
    public final static String WRITE_TIMEOUT = "WRITE_TIMEOUT";
    public final static String REMOTE_HUB_STATE_LEARN = "learn";
    public final static String REMOTE_HUB_STATE_WAITING_LEARN = "learn waiting";
    public final static String MQTT = "mqtt";
    public final static String UDP = "udp";
    public final static int UDP_SEND_PORT = 2000;
    public final static int UDP_RECEIVE_PORT = 3000;
    public final static int FTP_PORT = 21;
    public final static int HTTP_TO_DEVICE_PORT = 80;
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
    public final static int MQTT_PORT_TCP = 1883;
    public final static int MQTT_PORT = 1883;
    //New Devices
    public final static String NEW_DEVICE_TYPE_SWITCH = "switch";
    public final static String NEW_DEVICE_TYPE_PLUG = "plug";
    public final static String NEW_DEVICE_TOGGLE_CMD = "toggle";
    public final static String NEW_DEVICE_ITET = "ITET";
    public final static String NEW_DEVICE_PHYSICAL_VERIFICATION = "physical_verification";
    public final static String NEW_DEVICE_ON_STATUS = "2";
    public final static String NEW_DEVICE_OFF_STATUS = "1";
    public final static String NEW_DEVICE_PHV_TRUE = "phv_true";
    public final static String NEW_DEVICE_PHV_FALSE = "phv_false";
    public final static String NEW_DEVICE_PHV_VERIFIED = "phv_verified";
    public final static String NEW_DEVICE_PHV_START = "phv_start";
    public final static String NEW_DEVICE_PHV_TIMEOUT = "phv_timeout";
    public final static String NEW_DEVICE_config = "config";
    public final static String PRIMARY_CONFIG = "config init";
    public final static String REMOTE_HUB_SEND_DATA = "send data";
    public final static String REMOTE_HUB_ENTER_SETTINGS = "enter settings mode";
    public final static String REMOTE_HUB_EXIT_SETTINGS = "exit settings mode";
    public final static String REMOTE_HUB_ENTER_LEARN = "enter learn mode";
    public final static String REMOTE_HUB_EXIT_LEARN = "exit learn mode";
    public final static String REMOTE_HUB_GET_IR_SIGNAL = "get IR signal";
    public final static String PRIMARY_CONFIG_TRUE = "primary_config_true";
    public final static String PRIMARY_CONFIG_FALSE = "primary_config_false";
    public final static String EXPIRED = "EXPIRED";
    public final static String NEW_DEVICE_IP = "192.168.4.1";
//    public final static String NEW_DEVICE_IP = "192.168.137.1";
    public final static String GET_VERSION = "version";
    public final static String VERSION_1_0 = "1.0";
    public final static String VERSION_2_0 = "2.0";
    public final static String NODE_INFO = "info";
    public final static String NAMING_PREFIX_PIN1 = "_1";
    public final static String NAMING_PREFIX_PIN2 = "_2";
    public final static String NAMING_PREFIX_PIN1_PIN2 = "_1_2";

    //Messages
    public final static String TO_DEVICE_NODE = "NODE?";
    public final static String TO_DEVICE_TLMS = "TLMS";
    public final static String TO_DEVICE_VERIFY = "verify";
    public final static String NODE_DISCOVER = "node_discover";
    public final static String AUTH = "auth";
    public final static String AUTH_DISCOVER = "auth";
    public final static String TO_DEVICE_NOT_VERIFY = "not_verify";
    public final static String FROM_DEVICE_VERIFY_SUCCESSFUL = "successful";
    public final static String OPERATION_DONE = "Done";
    public final static String MISSING_PARAMS = "Missing_Parameters";
    public final static String NOT_FOUND = "Not_found";
    public final static String DEVICE_TOGGLE = "tgl";
    public final static String CMD = "cmd";
    public final static String DEVICE_TOGGLE_CMD = "toggle";
    public final static String ERROR = "error";
    public final static String ERROR_RESULT = "error";
    public final static String SERVER_ERROR= "SERVER_ERROR";
    public final static String FORBIDDEN = "Forbidden";
    public final static String AUTHENTICATION_ERROR= "Forbidden";
    public final static String UNAUTHORIZED= "unauthorized";
    public final static String HEADER_AUTHORIZATION= "Authorization";
    public final static int MOBILE_DATA = 0;
    public final static int WIFI_NETWORK = 1;
    public final static int VPN_NETWORK = 17;
    public final static String MOBILE = "MOBILE";
    public final static String WIFI = "WIFI";
    public final static String NOT_CONNECTED = "NOT_CONNECTED";
    public final static int NOT_CONNECTED_NETWORK = -1;
    public final static String NETWORK_ERROR = "NETWORK_ERROR";
    public final static String NO_HTTP = "no_http";


    public final static String STWORD = "stword";
    public final static String STWORD_HEADER = "header#stword";
    public final static String UNKNOWN_HEADER = "Unknown_header";
    public final static String UNDEFINED = "undefined";
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
    public final static String EVENT_GPIO_CHANGED = "gpio_changed";
    public final static String EVENT_SUBSCRIBED = "subscribed";
    public final static String EVENT_NODE_DISCOVER = "node_discover";
    public final static String EVENT_NODE_STATUS = "node_status";
    public final static int OFF_GPIO = 0;
    public final static int ON_GPIO = 1;

    /**
     * Device Settings
     */
    public final static String CHANGE_NAME_FALSE = "change_hname_false";
    public final static String CHANGE_AP = "change_ap";
    public final static String CHANGE_NAME = "change_name";
    public final static String CHANGE_NAME_TRUE = "change_hname_true";
    public final static String SOCKET_TIME_OUT = "SocketTimeoutException";
    public final static String TIME_OUT_EXCEPTION = "TimeoutException";
    public final static String CONNECT_EXCEPTION = "ConnectException";
    public final static String UNKNOWN_EXCEPTION = "UnknownException";
    public final static String UNKNOWN_HOST_EXCEPTION = "UnknownHostException";
    public final static String SETTINGS = "settings";
    public final static String SETTINGS_ENTER = "settings_enter";
    public final static String SETTINGS_EXIT = "settings_exit";
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
    public final static String PARAM_BRAND= "brand";
    public final static String PARAM_MODEL = "model";
    public final static String PARAM_TYPE = "type";

    public final static String BaseDeviceType_SWITCH_1 = "switch_1";
    public final static String BaseDeviceType_SWITCH_2 = "switch_2";
    public final static String BaseDeviceType_TOUCH_2 = "touch_2";
    public final static String BaseDeviceType_PLUG = "plug";
    public final static String BaseDeviceType_REMOTE_HUB = "remote_hub";
    public final static String BaseDeviceType_REMOTE = "remote";

    /**
     * API
     */
    public final static String ADMIN_TYPE = "admin";
    public final static String USER_TYPE = "user";
    public final static String DUPLICATE_USER = "duplicate user";
    public final static String DUPLICATE_REMOTE_HUB = "duplicate remoteHub";
    public final static String UNKNOWN_SSID = "Unknown SSID";
    public final static String UNKNOWN_NAME = "Unknown NAME";
    public final static String UNKNOWN_REMOTE_TYPE = "Unknown Remote Type";
    public final static String SSID = "SSID";
    public final static String NULL_SSID = "Null SSID";
    public final static String UNKNOWN_IP = "Unknown IP";
    public final static String SUCCESS_DESCRIPTION = "success";
    public final static String SUCCESS_RESULT = "successful";
    public final static String LEARN_ERROR = "in_ir_learn_mode";
    public final static String ALREADY_LEARN_ERROR = "already_in_ir_learn_mode";
    public final static String NOT_IN_LEARN_ERROR = "not_in_ir_learn_mode";
    public final static String LEARN_TIMEOUT = "learning_timed_out";
    public final static String FULL_LEARN_ERROR = "full_ir_learn_mode";
    public final static String SETTINGS_MODE_ERROR = "in_settings_mode";
    public final static String ALREADY_SETTINGS_MODE_ERROR = "already_in_settings_mode";
    public final static String FULL_SETTINGS_ERROR = "full_settings";
    public final static String WRONG_FORMAT_ERROR = "raw_data_wrong_format";
    public final static String UNKNOWN_ERROR = "unknown error";
    public final static String SUCCESSFUL = "successful";
    public final static String CONTINUE = "continue";
    public final static String ERROR_DESCRIPTION = "error";
    public final static String USER_NOT_FOUND_RESPONSE = "User not found";
    public final static String WRONG_PASSWORD_RESPONSE = "Wrong password";
    public final static String WRONG_STWORD = "Wrong STWORD.";
    public final static String DEVICE_IS_READY_FOR_UPDATE = "update";
    public final static String DEVICE_READY_FOR_UPDATE = "update";
    public final static String DEVICE_UPDATE_CODE_WROTE = "codes_wrote";
    public final static String DEVICE_UPDATE_END = "update_end";
    public final static String DEVICE_UPDATE_DONE = "updating_done";
    public final static String DEVICE_DO_UPDATE = "update";
    public final static String DEVICE_UPDATE = "update";
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
    public static String getDeviceAddress(String ip, String cmd){
        String address = "http://"+ip+":"+AppConstants.HTTP_TO_DEVICE_PORT+"/";
//        String address = "http://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io:"+AppConstants.HTTP_TO_DEVICE_PORT+"/";
        switch (cmd){
            case AppConstants.PRIMARY_CONFIG:
                address = address.concat("api/v1/config/init");
                break;
            case AppConstants.TO_DEVICE_VERIFY:
                address = address.concat("api/v1/node/verification");
                break;
            case AppConstants.DEVICE_TOGGLE_CMD:
                address = address.concat("api/v1/gpio/change_status");
                break;
            case AppConstants.NEW_DEVICE_PHYSICAL_VERIFICATION:
                address = address.concat("api/v1/config/physical_verification");
                break;
            case AppConstants.SETTINGS_ENTER:
                address = address.concat("api/v1/settings/enter");
                break;
            case AppConstants.CHANGE_NAME:
                address = address.concat("api/v1/settings/change_name");
                break;
            case AppConstants.CHANGE_AP:
                address = address.concat("api/v1/settings/change_ap");
                break;
            case AppConstants.FACTORY_RESET:
                address = address.concat("api/v1/settings/reset_factory");
                break;
            case AppConstants.END_SETTINGS:
                address = address.concat("api/v1/settings/exit");
                break;
            case AppConstants.DEVICE_UPDATE:
                address = address.concat("api/v1/ota/start");
                break;
            case AppConstants.NODE_INFO:
                address = address.concat("api/v1/system/info");
                break;
            case AppConstants.REMOTE_HUB_SEND_DATA:
                address = address.concat("api/v1/ir_remote/send_signal");
                break;
            case AppConstants.REMOTE_HUB_ENTER_SETTINGS:
                address = address.concat("api/v1/settings/enter");
                break;
            case AppConstants.REMOTE_HUB_EXIT_SETTINGS:
                address = address.concat("api/v1/settings/exit");
                break;
            case AppConstants.REMOTE_HUB_ENTER_LEARN:
                address = address.concat("api/v1/ir_remote/learn/enter");
                break;
            case AppConstants.REMOTE_HUB_EXIT_LEARN:
                address = address.concat("api/v1/ir_remote/learn/exit");
                break;
            case AppConstants.REMOTE_HUB_GET_IR_SIGNAL:
                address = address.concat("api/v1/ir_remote/learn/get_key_ir");
                break;
                default: throw new RuntimeException("No Suitable Found");
        }
        return address;
    }

    public static String resolveStword(String statusWord, String secret){
        Log.e("Appconstants", "Resolving Status Word");
        String decrypted = String.valueOf(Integer.parseInt(Encryptor.decrypt(statusWord, secret)));
        Log.e("Appconstants", "Decrypted word is: " + decrypted);
        int head = Integer.parseInt(String.valueOf(decrypted.charAt(0)));
        int tail = Integer.parseInt(String.valueOf(decrypted.charAt(decrypted.length()-1)));
        Log.e("Appconstants","Head is: "+ head +" Tail is: " + tail);
        String stword = decrypted.substring(head,decrypted.length()-tail-1);
        Log.e("Appconstants","STWORD is: "+ stword);
        return stword;
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