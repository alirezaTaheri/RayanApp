package rayan.rayanapp.Retrofit;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HandleFakeRequest {
    public static Map<String,String> bodyMap = new HashMap<>();
    public static Response check(Request request, Interceptor.Chain chain) throws IOException {
        Log.e("MOCK",""+request.header("mock"));
        String url = bodyMap.get(request.header("mock"));
        Log.e("MOCK_TO", "TO: " + url);
        if (url != null) {
            Request newRequest = request.newBuilder().url(url).build();
            return chain.proceed(newRequest);
        }
        return null;
    }
    public static void fillBodyMap(){
        bodyMap.put("login","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/login");
        bodyMap.put("get_groups","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/get_groups");
        bodyMap.put("getRemoteHubs","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/getRemoteHubs");
        bodyMap.put("addRemoteHub","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/addRemoteHub");
        bodyMap.put("delete_remote_hub","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/delete_remote_hub");
        bodyMap.put("addRemoteHubToGroup","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/addRemoteHubToGroup");
        bodyMap.put("addRemoteData","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/addRemoteData");
        bodyMap.put("addRemote","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/addRemote");
        bodyMap.put("editRemoteData","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/editRemoteData");
        bodyMap.put("editRemote","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/editRemote");
        bodyMap.put("getRemoteDatasById","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/getRemoteDatasById");
        bodyMap.put("send_data_remoteHub_v1","https://6effcf91-1321-4fe6-8c18-e49878e78d48.mock.pstmn.io/api/v3/send_data_remoteHub_v1");
    }
}