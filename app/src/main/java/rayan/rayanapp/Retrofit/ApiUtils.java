package rayan.rayanapp.Retrofit;

/**
 * Created by alireza321 on 21/12/2018.
 */

public class ApiUtils {
    private ApiUtils(){}
//    private static final String BASE_URL = "http://5.160.218.92:2233/";
    private static final String BASE_URL = "http://api2.rayansmarthome.ir:2233/";
    public static ApiService getApiService(){
        return RetrofitClient.getRetrofitInstance(BASE_URL).create(ApiService.class);
    }
}
