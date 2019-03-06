package rayan.rayanapp.Fragments;

import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    public DevicesFragment() {}
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    List<Device> devices = new ArrayList<>();
    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(getContext(), devices);
        devicesRecyclerViewAdapter.setListener(this);
        devicesFragmentViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DevicesFragmentViewModel.class);
        devicesFragmentViewModel.getAllDevices().observe(this, devices -> {
         devicesRecyclerViewAdapter.updateItems(devices);
         this.devices = devices;
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(devicesRecyclerViewAdapter);
        return view;
    }


    @Override
    public void onPin1Clicked(Device device, int position) {
        ((RayanApplication)getActivity().getApplication()).getDevicesAccessibilityBus().registerForAnimation(this, recyclerView.getLayoutManager().findViewByPosition(position).getWidth());
        if (RayanApplication.getPref().getProtocol().equals(AppConstants.UDP)) {
            if (device.isLocallyAccessibility())
                devicesFragmentViewModel.togglePin1(position, ((RayanApplication) getActivity().getApplication()), device, true);
            else{
                devicesFragmentViewModel.togglePin1(position, ((RayanApplication) getActivity().getApplication()), device, true);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (device.isOnlineAccessibility()){
                devicesFragmentViewModel.togglePin1(position, ((RayanApplication) getActivity().getApplication()), device, false);
            }
            else{
                devicesFragmentViewModel.togglePin1(position, ((RayanApplication) getActivity().getApplication()), device, false);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onPin2Clicked(Device device, int position) {
        ((RayanApplication)getActivity().getApplication()).getDevicesAccessibilityBus().registerForAnimation(this, recyclerView.getLayoutManager().findViewByPosition(position).getWidth());
        if (RayanApplication.getPref().getProtocol().equals(AppConstants.UDP)) {
            if (device.isLocallyAccessibility())
                devicesFragmentViewModel.togglePin2(position, (RayanApplication) getActivity().getApplication(),device, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
            else{
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
                devicesFragmentViewModel.togglePin2(position, (RayanApplication) getActivity().getApplication(),device, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
            }
        }
        else {
            if (device.isOnlineAccessibility()){
                devicesFragmentViewModel.togglePin2(position, ((RayanApplication) getActivity().getApplication()), device, false);
            }
            else{
                devicesFragmentViewModel.togglePin2(position, ((RayanApplication) getActivity().getApplication()), device, false);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
            }
        }

        }

    @Override
    public void onResume() {
        super.onResume();
        devicesFragmentViewModel.getGroups();
        ((RayanApplication)getActivity().getApplication()).getNetworkStatus().observe(getActivity(), networkConnection -> {
            devicesRecyclerViewAdapter.updateItems(devices);
        });
    }

    @Override
    public void toggleAnimationProgressChanged(int progress, int position) {
        Bundle b = new Bundle();
        b.putInt("progress", progress);
        devicesRecyclerViewAdapter.notifyItemChanged(position, b);
    }

    @Override
    public void stopToggleAnimation(ValueAnimator valueAnimator, int position, int currentProgress, int progressWidth) {
        getActivity().runOnUiThread(() -> {
//            valueAnimator.end();
            valueAnimator.cancel();
            valueAnimator.setIntValues(currentProgress,
                    (currentProgress +(progressWidth - currentProgress)/3),
                    (currentProgress + (progressWidth - currentProgress)/3*2),
                    progressWidth
            );
            valueAnimator.setDuration(365);
            valueAnimator.start();
//            valueAnimator.setCurrentPlayTime(1500);
//            Bundle b = new Bundle();
//            b.putInt("progress", progressWidth);
//            devicesRecyclerViewAdapter.notifyItemChanged(position, b);
//            valueAnimator.setIntValues(currentProgress + (progressWidth-currentProgress)/3,
//                    currentProgress + (progressWidth-currentProgress)*2/3,
//                    currentProgress + (progressWidth-currentProgress));
//            valueAnimator.setIntValues(200, 300, 400, 500, 510);
//        Bundle b = new Bundle();
//        b.putInt("progress", 550);
//        valueAnimator.setCurrentPlayTime(7000);
//        devicesRecyclerViewAdapter.notifyItemChanged(position, b);
        });
//    @Override
//    public void stopToggleAnimation(ValueAnimator valueAnimator, int position, int currentProgress, int progressWidth) {
//        getActivity().runOnUiThread(() -> {
//            valueAnimator.cancel();
////            valueAnimator.setIntValues(currentProgress,
////                    (currentProgress +(progressWidth - currentProgress)/3),
////                    (currentProgress + (progressWidth - currentProgress)/3*2),
////                    progressWidth
////            );
//            valueAnimator.setDuration(500);
//            valueAnimator.start();
////            valueAnimator.setCurrentPlayTime(1500);
////            Bundle b = new Bundle();
////            b.putInt("progress", progressWidth);
////            devicesRecyclerViewAdapter.notifyItemChanged(position, b);
////            valueAnimator.setIntValues(currentProgress + (progressWidth-currentProgress)/3,
////                    currentProgress + (progressWidth-currentProgress)*2/3,
////                    currentProgress + (progressWidth-currentProgress));
////            valueAnimator.setIntValues(200, 300, 400, 500, 510);
////        Bundle b = new Bundle();
////        b.putInt("progress", 550);
////        valueAnimator.setCurrentPlayTime(7000);
////        devicesRecyclerViewAdapter.notifyItemChanged(position, b);
//        });
    }
}
