package rayan.rayanapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import rayan.rayanapp.Data.Button;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Dialogs.ProgressDialog;
import rayan.rayanapp.Dialogs.RemoteLearnButtonDialog.RemoteLearnButtonDialog;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;


public class ACRemoteFragment extends NewRemoteControlBase implements View.OnClickListener {

    RemoteHub remoteHub;
    private String currentModel = null;
    private boolean learn;
    private final String TAG = "ACRemoteFragment";
    private List<RemoteData> remoteData = new ArrayList<>();
    private Map<String, RemoteData> remoteDataMap = new HashMap<>();

    public static ACRemoteFragment newInstance(Remote remote, boolean learn, ArrayList<RemoteData> remoteData) {
        ACRemoteFragment fragment = new ACRemoteFragment();
        Bundle b = new Bundle();
        b.putParcelable("remote",remote);
        b.putBoolean("learn",learn);
        b.putParcelableArrayList("remoteData",remoteData);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.remote_ac, container, false);
        ButterKnife.bind(this, v);
        for(int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
            View child = ((ViewGroup) v).getChildAt(i);
            if (child instanceof ConstraintLayout)
                child.setOnClickListener(this);
        }
        remote = getArguments().getParcelable("remote");
        learn = getArguments().getBoolean("learn");
        remoteData = getArguments().getParcelableArrayList("remoteData");
        remoteHub = viewModel.getRemoteHubById(remote.getRemoteHubId());
        Log.e(TAG, "Received Remote is: " + remote);
        Log.e(TAG, "Received RemoteData is: " + remoteData);
        Log.e(TAG, "Learn Mode: " + learn);
        Log.e(TAG, "RemoteHub: " + remoteHub);
        if (!learn){
            Map<String, String> params = new HashMap<>();
            params.put(AppConstants.PARAM_BRAND, remote.getBrand());
            params.put(AppConstants.PARAM_TYPE, remote.getType());
            ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.show();
            viewModel.getRemoteData(params).observe(this, data -> {
                Log.e("DATA RECEIVED", "DATA: " + data);
                progressDialog.dismiss();
                if (data.first != null){
                    remoteData = data.first;
                }else{
                    switch (data.second){
                        case AppConstants.SOCKET_TIME_OUT:
                            Log.e(TAG, "SOCKET_TIME_OUT");
                            Toast.makeText(activity, "سرور در مدت مشخص پاسخ نداد", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.CONNECT_EXCEPTION:
                            Log.e(TAG, "CONNECT_EXCEPTION");
                            Toast.makeText(activity, "خطا در اتصال", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.UNKNOWN_EXCEPTION:
                            Log.e(TAG, "Unknown Exception Occurred");
                            Toast.makeText(activity, "مشکلی رخ داده است", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
        else{
            remoteHub.setState(AppConstants.REMOTE_HUB_STATE_WAITING_LEARN);
            for (RemoteData data: remoteData) {
                buttonMap.put(data.getButton(), new Button(data));
                if (Arrays.asList(AppConstants.AC_REMOTE_BUTTONS).contains(data.getButton()))
                    addLearnedBadge(v, data.getButton());
            }
            activity.setBottomMessage("تعداد دکمه‌های ایجاد شده: "+buttonMap.size());

        }
        return v;
    }

    @Override
    public void nextModel() {
        Map<String, String> params = new HashMap<>();
        params.put("model",remote.getModel());
        params.put("type", AppConstants.REMOTE_TYPE_AC);
        viewModel.getRemoteData(params);
    }

    @Override
    public void onClick(View v) {
        if (learn){
            Log.e(TAG, "RemoteHub:"+remoteHub);
            Log.e("AC_Remote_Frag", "Received RemoteHub is: " + remoteHub);
            RemoteLearnButtonDialog learnDialog = RemoteLearnButtonDialog.newInstance(remoteHub, remote, getResources().getResourceName(v.getId()).split("/")[1]);
            learnDialog.show(getChildFragmentManager(), TAG);
        } else {
            for (RemoteData data: remoteData)
                remoteDataMap.put(data.getButton().replace(" ", "").toLowerCase(), data);
            String viewId = getResources().getResourceName(v.getId()).split("/")[1].toLowerCase();
            if (remoteDataMap.keySet().contains(viewId)){
                Log.e(TAG, "Sending Signal For: " + viewId);
                viewModel.send_signal(remoteDataMap.get(viewId), remoteHub).observe(this, s -> {
                    Log.e(TAG, "Response Received: " + s);
                    Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
                });
            }
            else Toast.makeText(activity, "این دکمه تنظیم نشده است", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void create_button(Button button) {
        buttonMap.put(button.getName(), button);
        activity.setBottomMessage("تعداد دکمه‌های ایجاد شده: "+buttonMap.size());
        addLearnedBadge(getView(), button.getName());
    }

}
