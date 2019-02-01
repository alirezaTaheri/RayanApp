package rayan.rayanapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.DeviceManagementListActivity;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.R;

public class EditDeviceFragment extends Fragment{

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.onlineAccessTextView)
    TextView onlineAccess;
    Device device;
    public EditDeviceFragment() {
    }

    public static EditDeviceFragment newInstance() {
        EditDeviceFragment fragment = new EditDeviceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_device, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    public void init(Device device){
        this.device = device;
        name.setText(device.getName1());
        onlineAccess.setVisibility(device.isReady4Mqtt()? View.VISIBLE : View.INVISIBLE);
    }

}
