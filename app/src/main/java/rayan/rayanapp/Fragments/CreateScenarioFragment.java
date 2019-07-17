package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;

public class CreateScenarioFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private CreateScenarioViewModel mViewModel;
    @BindView(R.id.groupsSpinner)
    Spinner groupsSpinner;
    @BindView(R.id.devicesSpinner)
    Spinner devicesSpinner;
    CreateScenarioViewModel viewModel;
    List<Group> groups = new ArrayList<>();
    List<Device> currentDevices = new ArrayList<>();
    private Group selectedGroup;
    private Device selectedDevice;

    public static CreateScenarioFragment newInstance() {
        return new CreateScenarioFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CreateScenarioViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_scenario, container, false);
        ButterKnife.bind(this, view);
        devicesSpinner.setOnItemSelectedListener(this);
        groupsSpinner.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CreateScenarioViewModel.class);
        // TODO: Use the ViewModel
    }


    @SuppressLint("CheckResult")
    @Override
    public void onResume() {
        super.onResume();
        viewModel.getAllGroupsFlowable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Group>>() {
                    @Override
                    public void accept(List<Group> groups) throws Exception {
                        CreateScenarioFragment.this.groups = groups;
                        List<String> groupNames = new ArrayList<>();
                        groupNames.add("همه گروه‌ها");
                        for (Group g : groups)
                            groupNames.add(g.getName());
                        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, groupNames);
                        CreateScenarioFragment.this.groups.add(0,new Group());
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        groupsSpinner.setAdapter(arrayAdapter);
                    }
                });
    }

    Activity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.groupsSpinner:
                selectedGroup = groups.get(parent.getSelectedItemPosition());
                viewModel.getAllDevicesInGroupLive(selectedGroup.getId()).observe(this, new Observer<List<Device>>() {
                    @Override
                    public void onChanged(@Nullable List<Device> devices) {
                        CreateScenarioFragment.this.currentDevices = devices;
                        List<String> devicesName = new ArrayList<>();
                        for (Device d : devices)
                            devicesName.add(d.getName1());
                        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, devicesName);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        devicesSpinner.setAdapter(arrayAdapter);
                    }
                });
                break;
            case R.id.devicesSpinner:
                selectedDevice = currentDevices.get(parent.getSelectedItemPosition());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
