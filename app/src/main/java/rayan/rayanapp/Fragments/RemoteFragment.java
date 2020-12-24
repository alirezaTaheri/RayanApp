package rayan.rayanapp.Fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.RemoteViewModel;


public class RemoteFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    List<RemoteData> remoteData = new ArrayList<>();
    Remote remote;
    RemoteHub remoteHub;
    Map<String, RemoteData> remoteDataMap = new HashMap<>();
    private final String TAG = "RemoteFragment";
    private RemoteViewModel viewModel;
    public static RemoteFragment newInstance(Remote remote) {
        RemoteFragment newRemoteFragment = new RemoteFragment();
        Bundle b = new Bundle();
        b.putParcelable("remote", remote);
        newRemoteFragment.setArguments(b);
        return newRemoteFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v;
        if (remote.getType().equals(AppConstants.REMOTE_TYPE_AC))
            v = inflater.inflate(R.layout.remote_ac, container, false);
        else if (remote.getType().equals(AppConstants.REMOTE_TYPE_TV))
            v = inflater.inflate(R.layout.remote_tv, container, false);
        else v = null;
        ButterKnife.bind(this, v);
        for(int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
            View child = ((ViewGroup) v).getChildAt(i);
            if (child instanceof ConstraintLayout)
                child.setOnClickListener(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ms_black));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(RemoteViewModel.class);
        remote = getArguments().getParcelable("remote");
        remoteHub = viewModel.getRemoteHubById(remote.getRemoteHubId());
        viewModel.getDataOfRemote(remote.getId()).observe(this, data -> {
            Log.e(TAG, "RemoteData: "+data);
            remoteData = data;
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onClick(View v) {
        for (RemoteData data: remoteData)
            remoteDataMap.put(data.getButton().replace(" ", "").toLowerCase(), data);
        String viewId = getResources().getResourceName(v.getId()).split("/")[1].toLowerCase();
        if (remoteDataMap.keySet().contains(viewId)){
            viewModel.addProgressBar(getView(), v.getTag().toString());
            Log.e(TAG, "Sending Signal For: " + viewId);
            viewModel.send_signal(remoteDataMap.get(viewId), remoteHub).observe(this, s -> {
                Log.e(TAG, "Response Received: " + s);
                viewModel.removeProgressbar(v.getTag().toString(), (ViewGroup) getView());
                switch (s){
                    case AppConstants.SUCCESS_RESULT:

                        break;
                    case AppConstants.LEARN_ERROR:
                        Toast.makeText(activity, "دستگاه در حالت یادگیری است", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.SOCKET_TIME_OUT:
                        AddNewDeviceActivity.getNewDevice().setFailed(true);
                        Toast.makeText(activity, "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.CONNECT_EXCEPTION:
                        Toast.makeText(activity, "ارسال پیام موفق نبود", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.UNKNOWN_HOST_EXCEPTION:
                        Toast.makeText(activity, "ارتباط با دستگاه را بررسی کنید", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.UNKNOWN_EXCEPTION:
                        Toast.makeText(activity, "یک مشکل ناشناخته رخ داده است", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.WRONG_FORMAT_ERROR:
                        Toast.makeText(activity, "پیام ارسالی مورد قبول نیست", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(activity, "پاسخ نامرتبط دریافت شد", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else Toast.makeText(activity, "این دکمه تنظیم نشده است", Toast.LENGTH_SHORT).show();
    }
}
