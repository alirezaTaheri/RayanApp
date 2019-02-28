package rayan.rayanapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Adapters.recyclerView.UsersRecyclerViewAdapter;
import rayan.rayanapp.Data.Contact;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.CreateGroupViewModel;
import rayan.rayanapp.ViewModels.TestDeviceFragmentViewModel;

public class TestDeviceFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    @BindView(R.id.pin1)
    SparkButton pin1;
    NewDevice newDevice;
    TestDeviceFragmentViewModel viewModel;
    public static TestDeviceFragment newInstance() {
        return new TestDeviceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_device, container, false);
        newDevice = ((AddNewDeviceActivity)getActivity()).getNewDevice();
        ButterKnife.bind(this, view);
        pin1.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(TestDeviceFragmentViewModel.class);

    }

    private void toggleOn(int pin){
        viewModel.toDeviceToggle(pin == 1? AppConstants.ON_1:AppConstants.ON_2).observe(this, toggleDeviceResponse -> {
            pin1.setChecked(toggleDeviceResponse.getPin1().equals(AppConstants.ON_STATUS));
            newDevice.setPin1(toggleDeviceResponse.getPin1());
        });
    }

    private void toggleOff(int pin){
        viewModel.toDeviceToggle(pin == 1? AppConstants.OFF_1:AppConstants.OFF_2).observe(this, toggleDeviceResponse -> {
            pin1.setChecked(toggleDeviceResponse.getPin1().equals(AppConstants.ON_STATUS));
            newDevice.setPin1(toggleDeviceResponse.getPin1());
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pin1:
                if (newDevice.getPin1()!= null){
                    if (newDevice.getPin1().equals(AppConstants.ON_STATUS))
                        toggleOff(1);
                    else
                        toggleOn(1);
                }
                break;
            case R.id.pin2:
                if (newDevice.getPin2()!= null){
                    if (newDevice.getPin2().equals(AppConstants.ON_STATUS))
                        toggleOff(1);
                    else
                        toggleOn(1);
                }
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Toast.makeText(getContext(), "پایان تست", Toast.LENGTH_SHORT).show();
    }
}