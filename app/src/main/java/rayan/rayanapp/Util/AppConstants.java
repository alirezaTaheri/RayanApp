package rayan.rayanapp.Util;

public class AppConstants {
    public final static String MQTT = "mqtt";
    public final static String UDP = "udp";
    public final static int UDP_SEND_PORT = 2000;
    public final static int UDP_RECEIVE_PORT = 3000;
    public final static int HTTP_TO_DEVICE_PORT = 3000;
    public final static String MQTT_HOST = "api.rayansmarthome.ir";
    public final static String MQTT_PORT = "1883";
    //Messages
    public final static String TO_DEVICE_NODE = "NODE?";
    public final static String TO_DEVICE_TLMS = "TLMS";
    public final static String OPERATION_DONE = "Done";
    public final static String ERROR = "error";
    public final static String FORBIDDEN = "Forbidden";
    public final static int MOBILE_DATA = 0;
    public final static int WIFI_NETWORK = 1;
    public final static String MOBILE = "MOBILE";
    public final static String WIFI = "WIFI";
    public final static String NOT_CONNECTED = "NOT_CONNECTED";

    //Operations
    public final static String ON_1 = "turn_on_1";
    public final static String ON_2 = "turn_on_2";
    public final static String OFF_1 = "turn_off_1";
    public final static String OFF_2 = "turn_off_2";

    //Status
    public final static String ON_STATUS = "on";
    public final static String OFF_STATUS = "off";

    /**
     * Device Settings
     */
    public final static String CHANGE_NAME_FALSE = "change_hname_false";
    public final static String SOCKET_TIME_OUT = "SocketTimeoutException";
    public final static String SETTINGS = "settings";
    public final static String END_SETTINGS = "end_settings";
    public final static String SET_TOPIC_MQTT = "MQTT";
    public final static String CHANGE_WIFI = "change_wifi";
    public final static String SET_TOPIC_MQTT_Response = "MQTTR";
    public final static String DEVICE_CONNECTED_STYLE = "connected";
    public final static String DEVICE_STANDALONE_STYLE = "standalone";
    public final static String FACTORY_RESET = "factory_reset";
    public final static String FACTORY_RESET_DONE = "factory_reseted";
}
