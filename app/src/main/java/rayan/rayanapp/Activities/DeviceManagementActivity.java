package rayan.rayanapp.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapterManagement;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.DevicesManagementActivityViewModel;

public class DeviceManagementActivity extends AppCompatActivity implements OnDeviceClickListenerManagement<Device> {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    DevicesRecyclerViewAdapterManagement devicesRecyclerViewAdapterManagement;
    List<Device> devices = new ArrayList<>();
    DevicesManagementActivityViewModel devicesManagementActivityViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);
        ButterKnife.bind(this);
        devicesRecyclerViewAdapterManagement = new DevicesRecyclerViewAdapterManagement(this, devices);
        devicesRecyclerViewAdapterManagement.setListener(this);
        recyclerView.setAdapter(devicesRecyclerViewAdapterManagement);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        devicesManagementActivityViewModel = ViewModelProviders.of(this).get(DevicesManagementActivityViewModel.class);
        devicesManagementActivityViewModel.getAllDevices().observe(this, devices -> devicesRecyclerViewAdapterManagement.updateItems(devices));
    }

    @Override
    public void onItemClick(Device item) {
        Toast.makeText(this, "onItemClick: " + item, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//        Toast.makeText(this, "OnPointerCaptureChanged: " + hasCapture, Toast.LENGTH_SHORT).show();
//    }
}
