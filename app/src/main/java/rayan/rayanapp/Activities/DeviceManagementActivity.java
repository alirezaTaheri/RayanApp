package rayan.rayanapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.R;

public class DeviceManagementActivity extends AppCompatActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    List<Device> devices = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);
        ButterKnife.bind(this);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(this, devices);
        recyclerView.setAdapter(devicesRecyclerViewAdapter);

    }
}
