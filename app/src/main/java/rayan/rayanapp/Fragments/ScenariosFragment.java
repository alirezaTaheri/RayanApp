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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Adapters.recyclerView.ScenariosRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Dialogs.ScenarioPopupMenuDialog;
import rayan.rayanapp.Dialogs.YesNoDialog;
import rayan.rayanapp.Listeners.OnScenarioClicked;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.ScenariosFragmentViewModel;

public class ScenariosFragment extends Fragment implements
        AdapterView.OnItemSelectedListener, OnScenarioClicked<Scenario>,ScenarioPopupMenuDialog.ScenarioMenuListener, YesNoDialogListener {
    @BindView(R.id.allOff)
    Button allOffButton;
    @BindView(R.id.allOn)
    Button allOnButton;
    @BindView(R.id.allOnSpinner)
    Spinner allOnSpinner;
    @BindView(R.id.allOffSpinner)
    Spinner allOffSpinner;
    ScenariosFragmentViewModel viewModel;
    Activity activity;
    List<Group> groups = new ArrayList<>();
    Group allOnSelectedGroup = new Group();
    Group allOffSelectedGroup = new Group();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    List<Scenario> scenarios = new ArrayList<>();
    ScenariosRecyclerViewAdapter recyclerViewAdapter;
    ScenarioPopupMenuDialog menuDialog;

    public ScenariosFragment() {
    }

    public static ScenariosFragment newInstance() {
        ScenariosFragment fragment = new ScenariosFragment();
        return fragment;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ScenariosFragmentViewModel.class);
        recyclerViewAdapter = new ScenariosRecyclerViewAdapter(getContext(), new ArrayList<>());
        recyclerViewAdapter.setListener(this);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onResume() {
        super.onResume();
        viewModel.getAllGroupsFlowable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Group>>() {
                    @Override
                    public void accept(List<Group> groups) throws Exception {
                        ScenariosFragment.this.groups = groups;
                        List<String> groupNames = new ArrayList<>();
                        groupNames.add("همه گروه‌ها");
                        for (Group g : groups)
                            groupNames.add(g.getName());
                        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, groupNames);
                        ScenariosFragment.this.groups.add(0,new Group());
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        allOnSpinner.setAdapter(arrayAdapter);
                        allOffSpinner.setAdapter(arrayAdapter);
                        String allOffSelectedGroupId = RayanApplication.getPref().getSelectedGroupAllOffScenario();
                        String allOnSelectedGroupId = RayanApplication.getPref().getSelectedGroupAllOnScenario();
                        if (allOffSelectedGroup != null){
                            int allOffSelectedGroupPosition = getGroupPosition(allOffSelectedGroupId);
                            if (allOffSelectedGroupPosition != -1){
                                allOffSpinner.setSelection(allOffSelectedGroupPosition);
                                allOffSelectedGroup = groups.get(allOffSelectedGroupPosition);
                            }
                        }
                        if (allOnSelectedGroup != null){
                            int allOnSelectedGroupPosition = getGroupPosition(allOnSelectedGroupId);
                            if (allOnSelectedGroupPosition != -1){
                                allOnSelectedGroup = groups.get(allOnSelectedGroupPosition);
                                allOnSpinner.setSelection(allOnSelectedGroupPosition);
                            }
                        }

                    }
                });
        viewModel.getAllScenariosLive().observe(this, new Observer<List<Scenario>>() {
            @Override
            public void onChanged(@Nullable List<Scenario> scenarios) {
                recyclerViewAdapter.setItems(scenarios);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scenarios, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allOnSpinner.setOnItemSelectedListener(this);
        allOffSpinner.setOnItemSelectedListener(this);
        return view;
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.allOn)
    public void allOn(){
        if (allOnSelectedGroup.getId() != null)
            viewModel.getAllDevicesInGroupSingle(allOnSelectedGroup.getId()).observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Device>>() {
                        @Override
                        public void accept(List<Device> devices) throws Exception {
                            for (Device device : devices){
                                if (device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1) || device.getType().equals(AppConstants.DEVICE_TYPE_PLUG)){
                                    viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, true);
                                }else {
                                    viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, true);
                                    viewModel.sendMqttPin2((RayanApplication)activity.getApplication(), device, true);
                                }
                            }
                        }
                    });
        else
        viewModel.getAllDevicesSingle().subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .subscribe(devices -> {
                    for (Device device : devices){
                        if (device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1) || device.getType().equals(AppConstants.DEVICE_TYPE_PLUG)){
                            viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, true);
                        }else {
                            viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, true);
                            viewModel.sendMqttPin2((RayanApplication)activity.getApplication(), device, true);
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.allOff)
    public void allOff(){
        if (allOffSelectedGroup.getId() != null)
        viewModel.getAllDevicesInGroupSingle(allOffSelectedGroup.getId()).observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Device>>() {
                    @Override
                    public void accept(List<Device> devices) throws Exception {
                        for (Device device : devices){
                            if (device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1) || device.getType().equals(AppConstants.DEVICE_TYPE_PLUG)){
                                viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, false);
                            }else {
                                viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, false);
                                viewModel.sendMqttPin2((RayanApplication)activity.getApplication(), device, false);
                            }
                        }
                    }
                });
        else
        viewModel.getAllDevicesSingle().subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .subscribe(devices -> {
                    for (Device device : devices){
                        if (device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1) || device.getType().equals(AppConstants.DEVICE_TYPE_PLUG)){
                            viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, false);
                        }else {
                            viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, false);
                            viewModel.sendMqttPin2((RayanApplication)activity.getApplication(), device, false);
                        }
                    }
                });
    }

    @OnClick(R.id.createScenarioButton)
    public void createScenarioButtonClicked(){
        CreateScenarioFragment.newInstance(-1).show(getChildFragmentManager(), "TAG");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.allOnSpinner:
                allOnSelectedGroup = groups.get(parent.getSelectedItemPosition());
                if (groups.get(parent.getSelectedItemPosition()).getId() != null){
                    RayanApplication.getPref().setSelectedGroupAllOnScenario(groups.get(parent.getSelectedItemPosition()).getId());
                }
                break;
            case R.id.allOffSpinner:
                allOffSelectedGroup = groups.get(parent.getSelectedItemPosition());
                if (groups.get(parent.getSelectedItemPosition()).getId() != null){
                    RayanApplication.getPref().setSelectedGroupAllOffScenario(groups.get(parent.getSelectedItemPosition()).getId());
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onScenarioClicked(Scenario item, int position) {
        menuDialog = new ScenarioPopupMenuDialog(getContext(), this, item.getId());
        menuDialog.show();
    }

    @Override
    public void onExecuteClicked(Scenario item, int position) {
        for (Device device: item.getDevices()){
            if (device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1) || device.getType().equals(AppConstants.DEVICE_TYPE_PLUG)){
                if (device.getPin1().equals(AppConstants.ON_STATUS))
                    viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, true);
                else
                    viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, false);
            }else {
                if (device.getPin1().equals(AppConstants.ON_STATUS))
                    viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, true);
                else viewModel.sendMqttPin1((RayanApplication)activity.getApplication(), device, false);
                if (device.getPin2().equals(AppConstants.ON_STATUS))
                    viewModel.sendMqttPin2((RayanApplication)activity.getApplication(), device, true);
                else viewModel.sendMqttPin2((RayanApplication)activity.getApplication(), device, false);
            }
        }
    }

    @Override
    public void onDeleteClicked(int id) {
        menuDialog.dismiss();
        Bundle data = new Bundle();
        data.putInt("id", id);
        YesNoDialog yesNoDialog = new YesNoDialog(getContext(),this, "آیا مایل به حذف کردن این سناریو هستید؟", data);
        yesNoDialog.show();
    }

    @Override
    public void onEditClicked(int id) {
        menuDialog.dismiss();
        CreateScenarioFragment.newInstance(id).show(getChildFragmentManager(), "TAG");
    }

    @Override
    public void onYesClicked(YesNoDialog yesNoDialog, Bundle data) {
        viewModel.deleteScenarioWithId(data.getInt("id"));
        yesNoDialog.dismiss();
    }

    @Override
    public void onNoClicked(YesNoDialog yesNoDialog, Bundle data) {
        yesNoDialog.dismiss();
    }

    public int getGroupPosition(String id){
        for (int a = 0;a<groups.size();a++)
            if (groups.get(a).getId() != null &&groups.get(a).getId().equals(id))
                return a;
        return -1;

    }
}
