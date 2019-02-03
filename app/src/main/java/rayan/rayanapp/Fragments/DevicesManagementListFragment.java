package rayan.rayanapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.DeviceManagementListActivity;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapterManagement;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.Listeners.OnStatusIconClickListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.DevicesManagementActivityViewModel;
import rayan.rayanapp.ViewModels.DevicesManagementListFragmentViewModel;
import rayan.rayanapp.ViewModels.FavoritesFragmentViewModel;

public class DevicesManagementListFragment extends Fragment implements OnDeviceClickListenerManagement<Device> {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    DevicesRecyclerViewAdapterManagement devicesRecyclerViewAdapterManagement;
    List<Device> devices = new ArrayList<>();
    DevicesManagementListFragmentViewModel devicesManagementListFragmentViewModel;
    SendDevice sendDevice;
    public DevicesManagementListFragment() {
    }

    public static DevicesManagementListFragment newInstance() {
        return new DevicesManagementListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicesRecyclerViewAdapterManagement = new DevicesRecyclerViewAdapterManagement(getActivity(), devices);
        devicesRecyclerViewAdapterManagement.setListener(this);
        devicesManagementListFragmentViewModel = ViewModelProviders.of(this).get(DevicesManagementListFragmentViewModel.class);
        devicesManagementListFragmentViewModel.getAllDevices().observe(this, devices -> devicesRecyclerViewAdapterManagement.updateItems(devices));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices_management_list, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(devicesRecyclerViewAdapterManagement);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onItemClick(Device item) {
//        DeviceListDialogFragment deviceListDialogFragment = DeviceListDialogFragment.newInstance(3);
//        deviceListDialogFragment.show(getActivity().getSupportFragmentManager(), "Custom Button Sheet");
        ((DeviceManagementListActivity)Objects.requireNonNull(getActivity())).setActionBarTitle(item.getName1());
        sendDevice.sendDevice(item);
    }
    public interface SendDevice {
        void sendDevice(Device device);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sendDevice = (SendDevice) getActivity();
    }
}
