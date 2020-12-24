package rayan.rayanapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Data.Button;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Dialogs.ProgressDialog;
import rayan.rayanapp.Dialogs.RemoteLearnButtonDialog.RemoteLearnButtonDialog;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;


public class TvRemoteFragment extends NewRemoteControlBase implements AddNewRemoteNavListener, View.OnClickListener {

    RemoteHub remoteHub;
    private String currentModel = null;
    private boolean learn;
    private final String TAG = "TvRemoteFragment";
    private List<RemoteData> remoteData = new ArrayList<>();
    private Map<String, RemoteData> remoteDataMap = new HashMap<>();

    public static TvRemoteFragment newInstance(Remote remote, boolean learn, ArrayList<RemoteData> remoteData) {
        TvRemoteFragment fragment = new TvRemoteFragment();
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
        View v = inflater.inflate(R.layout.remote_tv, container, false);
        ButterKnife.bind(this, v);
        List<String> tags = Arrays.asList(AppConstants.TV_REMOTE_BUTTONS);
        set_click_listeners(v, tags);
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
                if (Arrays.asList(remote.getType().equals(AppConstants.REMOTE_TYPE_AC)?AppConstants.AC_REMOTE_BUTTONS:AppConstants.TV_REMOTE_BUTTONS).contains(data.getButton()))
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
        params.put("type", AppConstants.REMOTE_TYPE_TV);
        viewModel.getRemoteData(params);
    }

    @Override
    public void onClick(View v) {
        if (learn){
            Log.e(TAG, "RemoteHub:"+remote);
            Log.e(TAG, "Received RemoteHub is: " + remoteHub);
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
    public void set_click_listeners(View v, List<String> tags){
        for(int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
            View child = ((ViewGroup) v).getChildAt(i);
            if (tags.contains(child.getTag()))
                child.setOnClickListener(this);
            else if(child instanceof ViewGroup)
                set_click_listeners(child, tags);
        }
    }
}
