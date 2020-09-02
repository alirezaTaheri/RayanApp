package rayan.rayanapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.DeviceManagementActivity;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Dialogs.YesNoDialog;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditDeviceFragmentViewModel;

public class EditRemoteFragment extends BackHandledFragment implements OnBottomSheetSubmitClicked, YesNoDialogListener {
    private final String newSSID= "newSSID";
    private final String newName="newName";
    private final String newPass="newPass";
    @BindView(R.id.name)
    EditText name;
    EditDeviceFragmentViewModel editDeviceFragmentViewModel;
    Remote remote;
    List<Remote> remotes = new ArrayList<>();
    DeviceManagementActivity activity;
    private final String TAG = "EditRemoteFragment";
    Bundle changedData = new Bundle();
    public EditRemoteFragment() {
    }

    public static EditRemoteFragment newInstance(Remote remote) {
        EditRemoteFragment fragment = new EditRemoteFragment();
        Bundle args = new Bundle();
        args.putParcelable("remote",remote);
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
        remote = getArguments().getParcelable("remote");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_remote, container, false);
        ButterKnife.bind(this, view);
        editDeviceFragmentViewModel = ViewModelProviders.of(this).get(EditDeviceFragmentViewModel.class);
        init();

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changedData.putString(newName,name.getText().toString());
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
        switch (remote.getType()){
            case AppConstants.REMOTE_TYPE_TV:
                type.setText("ریموت تلویزیون");
                break;
            case AppConstants.REMOTE_TYPE_AC:
                type.setText("ریموت کولر");
                break;
        }
        name.setText(remote.getName());
        RemoteHub remoteHub = editDeviceFragmentViewModel.getRemoteHub(remote.getRemoteHubId());
        accessPointSsid.setText(remoteHub.getName());
        Group group = editDeviceFragmentViewModel.getGroup(remote.getGroupId());
        groupName.setText(group.getName());
        favoriteButton.setImageDrawable(ContextCompat.getDrawable(activity,remote.isFavorite()?R.drawable.ic_star_full:R.drawable.ic_star_empty));
        visibilityButton.setImageDrawable(ContextCompat.getDrawable(activity,remote.isVisibility()?R.drawable.ic_visibility_on:R.drawable.ic_visibility_off));
    }

    @Override
    public boolean onBackPressed() {
        Log.e("EDITREMOTEOnbackpressed","Changed Params: " + changedData);
        if (changedData.size() != 0 && !deleted) {
            Toast.makeText(activity, "" + changedData, Toast.LENGTH_SHORT).show();
            confirmChanges();
            return true;
        }
        return false;
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
        menu.getItem(1).setVisible(false);
//        super.onCreateOptionsMenu(menu,inflater);
    }

    boolean deleted = false;
    public void confirmChanges() {
        if (changedData.containsKey(newName)) {
            String lastName = remote.getName();
            remote.setName(changedData.getString(newName));
            editDeviceFragmentViewModel.editRemote(remote).observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    Log.e("confirmChanges", "Response of Viewmodel is: "+s);
                    assert s != null;
                    switch (s) {
                        case AppConstants.FORBIDDEN:
                            Log.e(this.getClass().getSimpleName(), "FORBIDDEN CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "شما دسترسی لازم برای تغییر نام را ندارید");
                            break;
                        case AppConstants.OPERATION_DONE:
                            Log.e(this.getClass().getSimpleName(), "DONE CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "ویرایش نام با موفقیت انجام شد");
                            editDeviceFragmentViewModel.getGroups();
                            break;
                        case AppConstants.SOCKET_TIME_OUT:
                            Log.e(this.getClass().getSimpleName(), "SOCKET_TIME_OUT CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "خطای اتصال");
                            name.setText(lastName);
                            break;
                        case AppConstants.NETWORK_ERROR:
                            Log.e(this.getClass().getSimpleName(), "NETWORK_ERROR CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "خطای اتصال");
                            name.setText(lastName);
                            break;
                        case AppConstants.ERROR:
                            Log.e(this.getClass().getSimpleName(), "ERROR CHANGENAME");
                            SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "خطایی رخ داد");
                            name.setText(lastName);
                            break;
                    }
                    changedData.remove(newName);
                    Log.e("JFJFJFJFJF", "jfjfjfj" + changedData);
                    activity.onBackPressed();
                }
            });
        }
    }

    @OnClick(R.id.delete)
    public void deleteRemote(){
        editDeviceFragmentViewModel.deleteRemote(remote).observe(this, s -> {
            Log.e(TAG, "Remote Deletion Response: "+ s);
            switch (s) {
                case AppConstants.FORBIDDEN:
                    SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "شما دسترسی لازم برای تغییر نام را ندارید");
                    break;
                case AppConstants.MISSING_PARAMS:
                    SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "اطفاعات کافی نیست");
                    break;
                case AppConstants.SERVER_ERROR:
                    SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "مشکل در سرور لطفا کمی بعد امتحان کنید");
                    break;
                case AppConstants.OPERATION_DONE:
                    SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "ریموت حذف شد");
                    editDeviceFragmentViewModel.getGroups();
                    deleted = true;
                    activity.onBackPressed();
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "خطای اتصال");
                    break;
                case AppConstants.NETWORK_ERROR:
                    SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "خطای اتصال");
                    break;
                case AppConstants.ERROR:
                    SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), "خطایی رخ داد");
                    break;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.confirm)
            confirmChanges();
        return true;
    }

    @OnClick(R.id.favorite)
    public void onFavoriteClicked(){
        remote.setFavorite(!remote.isFavorite());
        editDeviceFragmentViewModel.updateRemote(remote);
        favoriteButton.setImageDrawable(ContextCompat.getDrawable(activity,remote.isFavorite()?R.drawable.ic_star_full:R.drawable.ic_star_empty));
        editDeviceFragmentViewModel.editRemote(remote);
        SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), remote.getName().concat(remote.isFavorite()?" به موردعلاقه ها اضافه شد":" از موردعلاقه ها حذف شد"));
    }
    @OnClick(R.id.visibility)
    public void onVisibility(){
        remote.setVisibility(!remote.isVisibility());
        editDeviceFragmentViewModel.updateRemote(remote);
        visibilityButton.setImageDrawable(ContextCompat.getDrawable(activity,remote.isVisibility()?R.drawable.ic_visibility_on:R.drawable.ic_visibility_off));
        editDeviceFragmentViewModel.editRemote(remote);
        SnackBarSetup.snackBarSetup(Objects.requireNonNull(activity).findViewById(android.R.id.content), remote.getName().concat(remote.isVisibility()?" به موردعلاقه ها اضافه شد":" از موردعلاقه ها حذف شد"));
    }
}
