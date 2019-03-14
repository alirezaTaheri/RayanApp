package rayan.rayanapp.Fragments;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.ViewModels.FavoritesFragmentViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class FavoritesFragment extends Fragment implements OnToggleDeviceListener<Device>, ToggleDeviceAnimationProgress {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    List<Device> devices = new ArrayList<>();
    FavoritesFragmentViewModel favoritesFragmentViewModel;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    Activity activity;
    public FavoritesFragment() {
    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        favoritesFragmentViewModel = ViewModelProviders.of(getActivity()).get(FavoritesFragmentViewModel.class);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(getContext(), devices);
        devicesRecyclerViewAdapter.setListener(this);
        favoritesFragmentViewModel.getAllDevices().observe(this, new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable List<Device> devices) {
                devicesRecyclerViewAdapter.updateItems(devices);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(devicesRecyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        return view;
    }


    @Override
    public void onPin1Clicked(Device device, int position) {
        if (RayanApplication.getPref().getProtocol().equals(AppConstants.UDP)) {
            if (device.isLocallyAccessibility())
                favoritesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, true);
            else{
                favoritesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, true);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (device.isOnlineAccessibility()){
                favoritesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, false);
            }
            else{
                favoritesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, false);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onPin2Clicked(Device device, int position) {
//        ((RayanApplication)getActivity().getApplication()).getDevicesAccessibilityBus().registerForAnimation(this, device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2)? recyclerView.getLayoutManager().findViewByPosition(position).getWidth()/2:recyclerView.getLayoutManager().findViewByPosition(position).getWidth());
        if (RayanApplication.getPref().getProtocol().equals(AppConstants.UDP)) {
            if (device.isLocallyAccessibility())
                favoritesFragmentViewModel.togglePin2(this,position, (RayanApplication) getActivity().getApplication(),device, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
            else{
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
                favoritesFragmentViewModel.togglePin2(this, position, (RayanApplication) getActivity().getApplication(),device, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
            }
        }
        else {
            if (device.isOnlineAccessibility()){
                favoritesFragmentViewModel.togglePin2(this,position, ((RayanApplication) getActivity().getApplication()), device, false);
            }
            else{
                favoritesFragmentViewModel.togglePin2(this, position, ((RayanApplication) getActivity().getApplication()), device, false);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
            }
        }

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

    public int findDevicePosition(String chipId){
        for (int a = 0;a<devices.size();a++)
            if (chipId.equals(devices.get(a).getChipId())) return a;
        return -1;
    }

}
