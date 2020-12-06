package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Adapters.arrayAdapter.ScenarioDevicesSpinnerCustomAdapter;
import rayan.rayanapp.Adapters.arrayAdapter.SpinnerCustomAdapter;
import rayan.rayanapp.Adapters.recyclerView.ScenarioActionsRecyclerViewAdapter;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Data.ScenarioDeviceSpinnerItem;
import rayan.rayanapp.Listeners.ScenarioOnActionClickedListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.CreateScenarioViewModel;

public class CreateScenarioFragment extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener, ScenarioOnActionClickedListener<Device> {

    private CreateScenarioViewModel mViewModel;
    @BindView(R.id.groupsSpinner)
    Spinner groupsSpinner;
    @BindView(R.id.devicesSpinner)
    Spinner devicesSpinner;
    CreateScenarioViewModel viewModel;
    List<Group> groups = new ArrayList<>();
    List<Device> actions = new ArrayList<>();
    List<Device> currentDevices = new ArrayList<>();
    private Group selectedGroup;
    private Device selectedDevice;
    @BindView(R.id.actions)
    RecyclerView recyclerView;
    @BindView(R.id.name)
    TextInputEditText nameEditText;
    @BindView(R.id.nameInputLayout)
    TextInputLayout nameInputLayout;
    ScenarioActionsRecyclerViewAdapter recyclerViewAdapter;
    Scenario editingScenario;
    private BottomSheetBehavior mBehavior;
    @BindView(R.id.root)
    CoordinatorLayout root;

    public static CreateScenarioFragment newInstance(int id) {
        CreateScenarioFragment fragment = new CreateScenarioFragment();
        if (id != -1) {
            Bundle args = new Bundle();
            args.putInt("id", id);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CreateScenarioViewModel.class);
        recyclerViewAdapter = new ScenarioActionsRecyclerViewAdapter(getContext(), this);
        if (getArguments() != null) {
            editingScenario = viewModel.getScenario(getArguments().getInt("id"));
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(),R.layout.fragment_create_scenario, null);
        ButterKnife.bind(this, view);
        dialog.setContentView(view);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        View parent = (View) view.getParent();
        parent.setFitsSystemWindows(true);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        view.measure(0, 0);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        bottomSheetBehavior.setPeekHeight(screenHeight);
        if (params.getBehavior() instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior)params.getBehavior()).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        params.height = screenHeight;
        parent.setLayoutParams(params);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        devicesSpinner.setOnItemSelectedListener(this);
        groupsSpinner.setOnItemSelectedListener(this);
        nameEditText.clearFocus();
        if (editingScenario != null){
            nameEditText.setText(editingScenario.getName());
            actions.addAll(editingScenario.getDevices());
            recyclerViewAdapter.updateItems(actions);
        }
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
//        return dialog;
    }
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

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
                        groupNames.add("لطفا گروه مورد نظر خود را انتخاب کنید");
                        ArrayAdapter arrayAdapter = new SpinnerCustomAdapter(activity, android.R.layout.simple_spinner_item, groupNames);
                        CreateScenarioFragment.this.groups.add(0,new Group());
                        Log.e("ererererer" , "erererer: " + groupNames.size() + groupNames.get(groupNames.size()-1) + arrayAdapter.getCount());
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        groupsSpinner.setAdapter(arrayAdapter);
//                        if (selectedGroup == null)
                            groupsSpinner.setSelection(arrayAdapter.getCount());
//                        else
//                            groupsSpinner.setSelection(getGroupPosition(selectedGroup));
                    }
                });
    }

    public int getGroupPosition(Group group){
        for (int a = 0;a<groups.size();a++)
            if (groups.get(a).getId().equals(group.getId()))
                return a;
        return -1;
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
                if (parent.getSelectedItemPosition() < groups.size()) {
                    selectedGroup = groups.get(parent.getSelectedItemPosition());
                    viewModel.getAllDevicesInGroupLive(selectedGroup.getId()).observe(this, new Observer<List<Device>>() {
                        @Override
                        public void onChanged(@Nullable List<Device> devices) {
                            CreateScenarioFragment.this.currentDevices = devices;
                            selectedGroup.setDevices(devices);
                            List<ScenarioDeviceSpinnerItem> devicesName = new ArrayList<>();
                            if (devices.size() == 0)
                                devicesName.add(new ScenarioDeviceSpinnerItem("0","دستگاهی در این گروه وجود ندارد"));
                            else {
                                devicesName.add(new ScenarioDeviceSpinnerItem("1", "همه دستگاه‌ها"));
                                for (Device d : devices)
                                    devicesName.add(new ScenarioDeviceSpinnerItem(d.getChipId(), d.getName1()));
                                devicesName.add(new ScenarioDeviceSpinnerItem("0","دستگاه مورد نظر خود را انتخاب کنید"));
                                ArrayAdapter arrayAdapter = new ScenarioDevicesSpinnerCustomAdapter(activity, android.R.layout.simple_spinner_item, devicesName);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                devicesSpinner.setAdapter(arrayAdapter);
                                devicesSpinner.setSelection(arrayAdapter.getCount());
                            }
                        }
                    });
                }
                break;
            case R.id.devicesSpinner:
//                if (parent.getSelectedItemPosition() < currentDevices.size()) {
                    if (((ScenarioDeviceSpinnerItem)parent.getSelectedItem()).getId().equals("1")){
                        for (Device device: selectedGroup.getDevices()) {
                            boolean exist = false;
                            for (Device d : actions)
                                if (d.getChipId().equals(device.getChipId()))
                                    exist = true;
                            if (!exist) {
                                actions.add(0, device);
                                recyclerViewAdapter.updateItems(actions);
                            }
                        }
                    }else if (!((ScenarioDeviceSpinnerItem)parent.getSelectedItem()).getId().equals("0")){
                        selectedDevice = currentDevices.get(parent.getSelectedItemPosition()-1);
                        boolean exist = false;
                            for (Device d : actions)
                                if (d.getChipId().equals(selectedDevice.getChipId()))
                                    exist = true;
                            if (!exist) {
                                actions.add(0, selectedDevice);
                                recyclerViewAdapter.updateItems(actions);
                            }
                    }
//                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch (parent.getId()){
            case R.id.groupsSpinner:
                break;
            case R.id.devicesSpinner:
//                selectedDevice = currentDevices.get(0);
//                devicesSpinner.setSelection(0);
                break;
        }
    }

    @Override
    public void pin1Clicked(Device item, int position) {
        Device d = new Device(item);
        d.setPin1(item.getPin1().equals(AppConstants.ON_STATUS)? AppConstants.OFF_STATUS:AppConstants.ON_STATUS);
        actions.set(position, d);
        recyclerViewAdapter.updateItems(actions);
    }

    @Override
    public void pin2Clicked(Device item, int position) {
        Device d = new Device(item);
        d.setPin2(item.getPin2().equals(AppConstants.ON_STATUS)? AppConstants.OFF_STATUS:AppConstants.ON_STATUS);
        actions.set(position, d);
        recyclerViewAdapter.updateItems(actions);
    }

    @Override
    public void removeClicked(Device item, int position) {
        actions.remove(position);
        recyclerViewAdapter.updateItems(actions);
    }

    public Scenario getScenario(){
        if (nameEditText.getText().toString().trim().length() > 0)
            return new Scenario(nameEditText.getText().toString(), actions);
        else {
            nameInputLayout.setError("لطفا نام سناریو را وارد کنید");
            return null;
        }

    }

    @OnClick(R.id.doneButton)
    public void doneButtonClicked(){
        Scenario scenario = getScenario();
        if (scenario != null) {
            if (editingScenario != null){
                scenario.setId(editingScenario.getId());
                viewModel.updateScenario(scenario);
                dismiss();
                Toast.makeText(activity, "سناریو با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show();
            }else {
                viewModel.addScenario(scenario);
                dismiss();
                Toast.makeText(activity, "سناریو با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.cancelButton)
    public void cancelButtonClicked(){
        dismiss();
    }
}
