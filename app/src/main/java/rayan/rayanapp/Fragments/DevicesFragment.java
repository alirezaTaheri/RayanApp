package rayan.rayanapp.Fragments;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
//<<<<<<< HEAD
import android.util.Log;
//=======
import android.util.DisplayMetrics;
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.ViewModels.DevicesFragmentViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class DevicesFragment extends Fragment implements OnToggleDeviceListener<Device>,ToggleDeviceAnimationProgress {
    DevicesFragmentViewModel devicesFragmentViewModel;
    Activity activity;

    public DevicesFragment() {}
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    List<Device> devices = new ArrayList<>();
    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(getContext(), devices);
        devicesRecyclerViewAdapter.setListener(this);
        devicesFragmentViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DevicesFragmentViewModel.class);
        devicesFragmentViewModel.getAllDevices().observe(this, devices -> {
         devicesRecyclerViewAdapter.updateItems(devices);
            ((RayanApplication)getActivity().getApplication()).getMtd().updateDevices(devices);
            this.devices = devices;
        });
        activity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, view);
        if (isTablet(getActivity())) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180)));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180)));
        }
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(devicesRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onPin1Clicked(Device device, int position) {
        Log.e("Pin1 Is Touching: " , "Device: " +device);
//        if (RayanApplication.getPref().getProtocol().equals(AppConstants.UDP)) {
//            if (device.getIp() != null)
                devicesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, true);
//            else{
////                devicesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, true);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else {
//            if (device.getTopic() != null){
//                devicesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, false);
//            }
//            else{
////                devicesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, false);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
//            }
//        }

    }

    @Override
    public void onPin2Clicked(Device device, int position) {
//        Log.e("Pin2 Is Touching: " , "Device: " +device);
////        ((RayanApplication)getActivity().getApplication()).getDevicesAccessibilityBus().registerForAnimation(this, device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2)? recyclerView.getLayoutManager().findViewByPosition(position).getWidth()/2:recyclerView.getLayoutManager().findViewByPosition(position).getWidth());
//        if (RayanApplication.getPref().getProtocol().equals(AppConstants.UDP)) {
//            if (device.getIp()!= null)
                devicesFragmentViewModel.togglePin2(this,position, (RayanApplication) getActivity().getApplication(),device, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
//            else{
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
////                devicesFragmentViewModel.togglePin2(this, position, (RayanApplication) getActivity().getApplication(),device, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
//            }
//        }
//        else {
//            if (device.getTopic() != null){
//                devicesFragmentViewModel.togglePin2(this,position, ((RayanApplication) getActivity().getApplication()), device, false);
//            }
//            else{
////                devicesFragmentViewModel.togglePin2(this, position, ((RayanApplication) getActivity().getApplication()), device, false);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
//            }
//        }

        }


    @Override
    public void startToggleAnimationPin1(String chipId, int position) {
        Bundle b = new Bundle();
        b.putString("startTogglingPin1", "startTogglingPin1");
        b.putString("chipId", chipId);
        b.putString("status", devicesRecyclerViewAdapter.getItem(position).getPin1().equals(AppConstants.ON_STATUS)?AppConstants.ON_STATUS:AppConstants.OFF_STATUS);
        devicesRecyclerViewAdapter.notifyItemChanged(position,b);
    }

    @Override
    public void startToggleAnimationPin2(String chipId, int position) {
        Bundle b = new Bundle();
        b.putString("startTogglingPin2", "startTogglingPin2");
        b.putString("chipId", chipId);
        b.putString("status", devicesRecyclerViewAdapter.getItem(position).getPin2().equals(AppConstants.ON_STATUS)?AppConstants.ON_STATUS:AppConstants.OFF_STATUS);
        devicesRecyclerViewAdapter.notifyItemChanged(position,b);
    }

    @Override
    public void stopToggleAnimationPin1(String chipId) {
            Bundle b = new Bundle();
            int position = findDevicePosition(chipId);
            b.putString("stopToggleAnimationPin1", "stopToggleAnimationPin1");
            b.putString("chipId", devicesRecyclerViewAdapter.getItem(position).getChipId());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    devicesRecyclerViewAdapter.notifyItemChanged(position,b);
                    Log.e(this.getClass().getSimpleName(), "InFragment stopping animation pin 111");
                }
            });
    }

    @Override
    public void stopToggleAnimationPin2(String chipId) {
        Bundle b = new Bundle();
        int position = findDevicePosition(chipId);
        b.putString("stopToggleAnimationPin2", "stopToggleAnimationPin2");
        b.putString("chipId", devicesRecyclerViewAdapter.getItem(position).getChipId());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                devicesRecyclerViewAdapter.notifyItemChanged(position,b);
                Log.e(this.getClass().getSimpleName(), "InFragment stopping animation pin 222");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        devicesFragmentViewModel.getGroups();
        ((RayanApplication)getActivity().getApplication()).getNetworkStatus().observe(getActivity(), networkConnection -> {
            devicesRecyclerViewAdapter.updateItems(devices);
        });
    }

    public static int calculateNoOfColumns(Context context, float columnWidthDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    public static boolean isTablet(Context ctx){
        return (ctx.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public int findDevicePosition(String chipId){
        for (int a = 0;a<devices.size();a++)
            if (chipId.equals(devices.get(a).getChipId())) return a;
        return -1;
    }
}
