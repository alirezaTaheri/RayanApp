package rayan.rayanapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Activities.DeviceManagementActivity;
import rayan.rayanapp.Adapters.recyclerView.RemotesRecyclerViewAdapter;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Dialogs.ProgressDialog;
import rayan.rayanapp.Dialogs.YesNoDialog;
import rayan.rayanapp.Listeners.DoneWithSelectAccessPointFragment;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.Listeners.OnRemoteClicked;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditDeviceFragmentViewModel;

public class EditRemoteHubFragment extends BackHandledFragment implements DoneWithSelectAccessPointFragment, OnBottomSheetSubmitClicked, YesNoDialogListener, OnRemoteClicked<Remote> {
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
    @BindView(R.id.factoryReset)
    LinearLayout resetButton;
    List<Remote> remotes = new ArrayList<>();
    DeviceManagementActivity activity;
    RemotesRecyclerViewAdapter remotesRecyclerViewAdapter;
    Bundle changedData = new Bundle();
    @BindView(R.id.emptyRecyclerView)
    RelativeLayout emptyRecyclerView;
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
        remotesRecyclerViewAdapter.setListener(this);
        remoteHub = getArguments().getParcelable("remoteHub");
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_remote_hub, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(remotesRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        editDeviceFragmentViewModel = ViewModelProviders.of(this).get(EditDeviceFragmentViewModel.class);
        init();
        editDeviceFragmentViewModel.getAllRemotesLive().observe(this, new Observer<List<Remote>>() {
            @Override
            public void onChanged(@Nullable List<Remote> remotes) {
                remotesRecyclerViewAdapter.updateItems(remotes);
                if (remotes.size()==0)
                    emptyRecyclerView.setVisibility(View.VISIBLE);
                else
                    emptyRecyclerView.setVisibility(View.GONE);
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
//                    editDeviceTopic.setVisibility(View.INVISIBLE);
//                }
//                else{
//                    editDeviceTopic.setVisibility(View.VISIBLE);
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
    @BindView(R.id.favorite)
    ImageView favoriteButton;
    @BindView(R.id.visibility)
    ImageView visibilityButton;
    public void init(){
        name.setFocusable(false);
        name.setClickable(true);
        changeAccessPoint.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);
        name.setText(remoteHub.getName());
        accessPointSsid.setText(remoteHub.getSsid());
        Group group = editDeviceFragmentViewModel.getGroup(remoteHub.getGroupId());
        groupName.setText(group.getName());
        type.setText("ریموت هاب");
        favoriteButton.setImageDrawable(ContextCompat.getDrawable(activity,remoteHub.isFavorite()?R.drawable.ic_star_full:R.drawable.ic_star_empty));
        visibilityButton.setImageDrawable(ContextCompat.getDrawable(activity,remoteHub.isVisibility()?R.drawable.ic_visibility_on:R.drawable.ic_visibility_off));
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


    private Menu menu;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_device, menu);
        this.menu = menu;
        menu.getItem(0).setVisible(false);
//        super.onCreateOptionsMenu(menu,inflater);
    }

    public void confirmChanges(){
        if (changedData.containsKey(newName)) {
            String lastSSID = remoteHub.getSsid();
            String lastName = remoteHub.getName();
            remoteHub.setName(changedData.getString(newName));
            remoteHub.setSsid(changedData.getString(newSSID));
            editDeviceFragmentViewModel.editRemoteHub(remoteHub).observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    assert s != null;
                    switch (s) {
                        case AppConstants.FORBIDDEN:
                            Log.e(this.getClass().getSimpleName(), "FORBIDDEN CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "شما دسترسی لازم برای تغییر نام را ندارید");
                            name.setText(lastName);
                            remoteHub.setName(lastName);
                            remoteHub.setSsid(lastSSID);
                            break;
                        case AppConstants.CHANGE_NAME_FALSE:
                            Log.e(this.getClass().getSimpleName(), "CHANGE_NAME_FALSE CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "امکان ویرایش نام وجود ندارد");
                            name.setText(lastName);
                            remoteHub.setName(lastName);
                            remoteHub.setSsid(lastSSID);
                            break;
                        case AppConstants.OPERATION_DONE:
                            Log.e(this.getClass().getSimpleName(), "DONE CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "ویرایش نام با موفقیت انجام شد");
                            editDeviceFragmentViewModel.getGroups();
                            break;
                        case AppConstants.CHANGE_NAME_TRUE:
                            Log.e(this.getClass().getSimpleName(), "DONE CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "ویرایش نام با موفقیت انجام شد");
                            editDeviceFragmentViewModel.getGroups();
                            break;
                        case AppConstants.SOCKET_TIME_OUT:
                            Log.e(this.getClass().getSimpleName(), "SOCKET_TIME_OUT CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "خطای اتصال");
                            name.setText(lastName);
                            remoteHub.setName(lastName);
                            remoteHub.setSsid(lastSSID);
                            break;
                        case AppConstants.ERROR:
                            Log.e(this.getClass().getSimpleName(), "ERROR CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "خطایی رخ داد");
                            name.setText(lastName);
                            remoteHub.setName(lastName);
                            remoteHub.setSsid(lastSSID);
                            break;
                    }
                    changedData.remove(newName);
                    if (changedData.containsKey(newSSID))
                        editDeviceFragmentViewModel.toRemoteHubChangeAccessPoint(remoteHub, changedData.getString(newSSID), changedData.getString(newPass)).observe(EditRemoteHubFragment.this, s2 -> {
                            assert s2 != null;
                            switch (s2) {
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
        }
        activity.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.confirm)
            confirmChanges();
        else if (item.getItemId() == R.id.edit)
            readyForEdit();
        return true;
    }

    ProgressDialog progressDialog;
    @OnClick(R.id.factoryReset)
    public void onResetClicked(){
        YesNoButtomSheetFragment bottomSheetFragment = YesNoButtomSheetFragment.instance("resetRemoteHub","تایید", "لغو", "آیا مایل به ریست کردن دستگاه هستید؟");
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void resetRemoteHub(){
        progressDialog.show();
        progressDialog.cancel.setOnClickListener(v -> {
            progressDialog.dismiss();
            editDeviceFragmentViewModel.getResetRemoteHubDisposable().dispose();
        });
        editDeviceFragmentViewModel.toRemoteHubFactoryReset(remoteHub).observe(EditRemoteHubFragment.this, s -> {
            assert s != null;
            switch (s){
                case AppConstants.FACTORY_RESET_DONE:
                    Toast.makeText(getActivity(), "دستگاه با موفقیت ریست شد", Toast.LENGTH_SHORT).show();
                    editDeviceFragmentViewModel.getGroups();
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.ERROR:
                    Toast.makeText(getActivity(), "خطایی رخ داد", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.NOT_FOUND:
                    Toast.makeText(getActivity(), "دستگاهی با این مشخصات وجود ندارد", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressDialog.dismiss();
        });
    }

    @OnClick(R.id.changeAccessPoint)
    public void changeAccessPoint(){
        Toast.makeText(activity, "پیاده سازی نشده است", Toast.LENGTH_SHORT).show();
    }

    public void readyForEdit(){
        progressDialog.show();
        progressDialog.cancel.setOnClickListener(v -> {
            progressDialog.dismiss();
            editDeviceFragmentViewModel.getResetRemoteHubDisposable().dispose();
        });
        editDeviceFragmentViewModel.remoteHubReadyForSettings(remoteHub).observe(this, s -> {
            Log.e("...........", "Ready For Settings Response" + s);
            if (s.equals(AppConstants.SETTINGS)) {
                Toast.makeText(activity, "دستگاه آماده ویرایش است", Toast.LENGTH_SHORT).show();
                name.setFocusableInTouchMode(true);
                changeAccessPoint.setVisibility(View.VISIBLE);
                resetButton.setVisibility(View.VISIBLE);
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
            } else if (s.equals(AppConstants.SOCKET_TIME_OUT)) {
                Toast.makeText(activity, "اتصال به دستگاه ناموفق بود", Toast.LENGTH_SHORT).show();
            } else if (s.equals(AppConstants.MISSING_PARAMS)) {
                Toast.makeText(activity, "دستگاه آمادگی ویرایش را ندارد", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(activity, "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        });
    }

    @OnClick(R.id.favorite)
    public void onFavoriteClicked(){
        remoteHub.setFavorite(!remoteHub.isFavorite());
        editDeviceFragmentViewModel.updateRemoteHub(remoteHub);
        favoriteButton.setImageDrawable(ContextCompat.getDrawable(activity,remoteHub.isFavorite()?R.drawable.ic_star_full:R.drawable.ic_star_empty));
        editDeviceFragmentViewModel.editRemoteHub(remoteHub);
        SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), remoteHub.getName().concat(remoteHub.isFavorite()?" به موردعلاقه ها اضافه شد":" از موردعلاقه ها حذف شد"));
    }
    @OnClick(R.id.visibility)
    public void onVisibility(){
        remoteHub.setVisibility(!remoteHub.isVisibility());
        editDeviceFragmentViewModel.updateRemoteHub(remoteHub);
        visibilityButton.setImageDrawable(ContextCompat.getDrawable(activity,remoteHub.isVisibility()?R.drawable.ic_visibility_on:R.drawable.ic_visibility_off));
        editDeviceFragmentViewModel.editRemoteHub(remoteHub);
        SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), remoteHub.getName().concat(remoteHub.isVisibility()?" به موردعلاقه ها اضافه شد":" از موردعلاقه ها حذف شد"));
    }

    @Override
    public void onRemoteClicked(Remote item) {
//        Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.emptyRecyclerView)
    public void addNewRemoteClicked(){
        startActivity(new Intent(activity, AddNewRemoteActivity.class));
    }
}
