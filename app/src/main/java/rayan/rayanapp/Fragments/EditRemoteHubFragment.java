package rayan.rayanapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.DeviceManagementActivity;
import rayan.rayanapp.Adapters.recyclerView.RemotesRecyclerViewAdapter;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Dialogs.YesNoDialog;
import rayan.rayanapp.Listeners.DoneWithSelectAccessPointFragment;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditDeviceFragmentViewModel;

public class EditRemoteHubFragment extends BackHandledFragment implements DoneWithSelectAccessPointFragment, OnBottomSheetSubmitClicked, YesNoDialogListener {
    private final String newSSID= "newSSID";
    private final String newName="newName";
    private final String newPass="newPass";
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.changeAccessPoint)
    ImageView changeAccessPoint;
    EditDeviceFragmentViewModel editDeviceFragmentViewModel;
    RemoteHub remoteHub;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    List<Remote> remotes = new ArrayList<>();
    DeviceManagementActivity activity;
    RemotesRecyclerViewAdapter remotesRecyclerViewAdapter;
    Bundle changedData = new Bundle();
    public EditRemoteHubFragment() {
    }

    public static EditRemoteHubFragment newInstance(RemoteHub remoteHub) {
        EditRemoteHubFragment fragment = new EditRemoteHubFragment();
        Bundle args = new Bundle();
        args.putParcelable("remoteHub",remoteHub);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DeviceManagementActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        remotesRecyclerViewAdapter = new RemotesRecyclerViewAdapter(activity, new ArrayList<>());
        remoteHub = getArguments().getParcelable("remoteHub");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_remote_hub, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(remotesRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        init();
        editDeviceFragmentViewModel = ViewModelProviders.of(this).get(EditDeviceFragmentViewModel.class);
        editDeviceFragmentViewModel.getAllRemotesLive().observe(this, new Observer<List<Remote>>() {
            @Override
            public void onChanged(@Nullable List<Remote> remotes) {
                remotesRecyclerViewAdapter.updateItems(remotes);
            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changedData.putString(newName,name.getText().toString());
//                if (name.getText().toString().equals(remoteHub.getName())){
//                    editDevice.setVisibility(View.INVISIBLE);
//                }
//                else{
//                    editDevice.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    @BindView(R.id.group)
    TextView groupName;
    @BindView(R.id.ssid)
    TextView accessPointSsid;
    @BindView(R.id.type)
    TextView type;
    public void init(){
        name.setText(remoteHub.getName());
        accessPointSsid.setText(remoteHub.getSsid());
        groupName.setText(remoteHub.getGroupId());
        type.setText(remoteHub.getDeviceType());
    }

    @Override
    public boolean onBackPressed() {
        if (changedData.size() != 0) {
            Toast.makeText(activity, "" + changedData, Toast.LENGTH_SHORT).show();
            confirmChanges();
            return true;
        }
        return false;
    }

    @Override
    public void accessPointSelected(String ssid, String pass) {
        changedData.putString(newSSID,ssid);
        changedData.putString(newPass,pass);
    }

    @Override
    public void submitClicked(String tag) {

    }

    @Override
    public void onYesClicked(YesNoDialog yesNoDialog, Bundle data) {

    }

    @Override
    public void onNoClicked(YesNoDialog yesNoDialog, Bundle data) {

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_device, menu);
        menu.getItem(0).setVisible(true);
//        super.onCreateOptionsMenu(menu,inflater);
    }

    public void confirmChanges(){
        if (changedData.containsKey(newName))
        editDeviceFragmentViewModel.remoteHubChangeName(remoteHub, remoteHub.getId(), changedData.getString(newName), remoteHub.isAccessible(), remoteHub.isVisibility(), remoteHub.getMac(), remoteHub.getVersion(), changedData.getString(newSSID)).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                assert s != null;
                switch (s){
                    case AppConstants.FORBIDDEN:
                        Log.e(this.getClass().getSimpleName(), "FORBIDDEN CHANGENAME");
                        SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content),"شما دسترسی لازم برای تغییر نام را ندارید");
                        break;
                    case AppConstants.CHANGE_NAME_FALSE:
                        Log.e(this.getClass().getSimpleName(), "CHANGE_NAME_FALSE CHANGENAME");
                        SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content),"امکان ویرایش نام وجود ندارد");
                        break;
                    case AppConstants.OPERATION_DONE:
                        Log.e(this.getClass().getSimpleName(), "DONE CHANGENAME");
                        remoteHub.setName(name.getText().toString());
                        SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content),"ویرایش نام با موفقیت انجام شد");
                        editDeviceFragmentViewModel.getGroups();
                        break;
                    case AppConstants.CHANGE_NAME_TRUE:
                        Log.e(this.getClass().getSimpleName(), "DONE CHANGENAME");
                        remoteHub.setName(name.getText().toString());
                        SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content),"ویرایش نام با موفقیت انجام شد");
                        editDeviceFragmentViewModel.getGroups();
                        break;
                    case AppConstants.SOCKET_TIME_OUT:
                        Log.e(this.getClass().getSimpleName(), "SOCKET_TIME_OUT CHANGENAME");
                        SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content),"خطای اتصال");
                        name.setText(remoteHub.getName());
                        break;
                    case AppConstants.ERROR:
                        Log.e(this.getClass().getSimpleName(), "ERROR CHANGENAME");
                        SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content),"خطایی رخ داد");
                        name.setText(remoteHub.getName());
                        break;
                }
                changedData.remove(newName);
                if (changedData.containsKey(newSSID))
                    editDeviceFragmentViewModel.toRemoteHubChangeAccessPoint(remoteHub, changedData.getString(newSSID), changedData.getString(newPass)).observe(EditRemoteHubFragment.this, s2 -> {
                        assert s2 != null;
                        switch (s2){
                            case AppConstants.CHANGING_WIFI:
                                Toast.makeText(getActivity(), "دستگاه در‌حال اعمال تغییرات می‌باشد", Toast.LENGTH_SHORT).show();
                                break;
                            case AppConstants.SOCKET_TIME_OUT:
                                Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
                                break;
                            case AppConstants.ERROR:
                                Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    });
                changedData.remove(newSSID);
                changedData.remove(newPass);
            }
        });
        activity.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.confirm)
            confirmChanges();
        Toast.makeText(activity, "item", Toast.LENGTH_SHORT).show();
        return true;
    }
    @OnClick(R.id.changeAccessPoint)
    void toDeviceChangeAccessPoint(){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
        };
        if(!editDeviceFragmentViewModel.hasPermissions(activity, PERMISSIONS)){
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
        }
        else{
            LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                editDeviceFragmentViewModel.buildAlertMessageNoGps(activity);
            }else{
                ChangeDeviceAccessPointFragment.newInstance(remoteHub.getSsid()).show(activity.getSupportFragmentManager(), "dialog");
            }
        }
    }

}
