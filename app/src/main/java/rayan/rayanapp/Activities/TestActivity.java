package rayan.rayanapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Requests.device.ToggleDevice;
import rayan.rayanapp.Retrofit.Models.Responses.device.ToggleDeviceResponse;
import rayan.rayanapp.Util.AppConstants;

public class TestActivity extends AppCompatActivity {

    Device device;
    @BindView(R.id.stword)
    EditText stword;
    @BindView(R.id.chipId)
    EditText chipId;
    @BindView(R.id.cmd)
    EditText cmd;
    @BindView(R.id.lc)
    EditText lc;
    private String TAG = this.getClass().getCanonicalName();
    DeviceDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        database = new DeviceDatabase(this);
    }

//    @OnClick(R.id.send)
//    public void send(){
//        device = database.getDevice(chipId.getText().toString());
//        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
//                AppConstants.getDeviceAddress(device.getIp()),
//                new Response.Listener<String>() {
//
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d(TAG, "Response time: "+(System.currentTimeMillis()-sendTime)+"Volley: "+response.toString());
//                        Gson gson = new Gson();
//                        Toast.makeText(TestActivity.this, response, Toast.LENGTH_SHORT).show();
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: Volley: " + error.getMessage());
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("cmd", cmd.getText().toString());
//                params.put("stword", Encryptor.encrypt(stword.getText().toString().concat("#"), device.getSecret()));
//                params.put("src", "password123");
//
//                return params;
//            }
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//
//                Gson gson = new Gson();
//                String requestBody = gson.toJson(new ToggleDevice(cmd.getText().toString(),Encryptor.encrypt(stword.getText().toString().concat("#"), device.getSecret()), lc.getText().toString()), ToggleDevice.class);
//                Log.e("Sending", "Volley: " + requestBody);
//                try {
//                    return requestBody.getBytes("utf-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                    return null;
//                }
//            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
////                headers.put("apiKey", "xxxxxxxxxxxxxxx");
//                return headers;
//            }
//
//        };
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        sendTime = System.currentTimeMillis();
//// Adding request to request queue
//        RayanApplication.addToRequestQueue(jsonObjReq, String.valueOf(System.currentTimeMillis()));
//    }
    long sendTime;
}