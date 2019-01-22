package rayan.rayanapp.MainActivity.fragments;

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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.MainActivity.MainActivity;
import rayan.rayanapp.MainActivity.OnStatusIconClickListener;
import rayan.rayanapp.MainActivity.adapters.DevicesRecyclerViewAdapter;
import rayan.rayanapp.MainActivity.viewModels.DevicesFragmentViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class DevicesFragment extends Fragment implements OnStatusIconClickListener {
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
        });
        devicesFragmentViewModel.getGroups();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(devicesRecyclerViewAdapter);
        return view;
    }


    @Override
    public void onPin1Clicked(Object Item) {
            devicesFragmentViewModel.togglePin1((Device) Item, MainActivity.PROTOCOL.equals(AppConstants.UDP));

    }

    @Override
    public void onPin2Clicked(Object Item) {
        devicesFragmentViewModel.togglePin2((Device) Item, MainActivity.PROTOCOL.equals(AppConstants.UDP));
    }
}
