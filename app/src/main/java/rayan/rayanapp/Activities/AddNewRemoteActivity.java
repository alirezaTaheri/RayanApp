package rayan.rayanapp.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Data.Button;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Dialogs.ProgressDialog;
import rayan.rayanapp.Fragments.ACRemoteFragment;
import rayan.rayanapp.Fragments.NewRemoteControlBase;
import rayan.rayanapp.Fragments.NewRemoteSelectBrandFragment;
import rayan.rayanapp.Fragments.NewRemoteSelectLearnedFragment;
import rayan.rayanapp.Fragments.NewRemoteSelectTypeFragment;
import rayan.rayanapp.Fragments.NewRemoteSetConfigurationFragment;
import rayan.rayanapp.Fragments.TvRemoteFragment;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Topic;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.AddNewRemoteViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddNewRemoteActivity extends AppCompatActivity {

    FragmentManager fm;
    FragmentTransaction ft;
    private String selectedBrand;
    AddNewRemoteViewModel viewModel;
    private NewRemoteSelectLearnedFragment selectLearnedFragment;
    private NewRemoteSelectTypeFragment selectTypeFragment;
    private NewRemoteSelectBrandFragment selectBrandFragment;
    private NewRemoteControlBase newRemoteControlBase;
    private NewRemoteSetConfigurationFragment setConfigurationFragment;
    private AddNewRemoteNavListener currentFragment;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.nextModel)
    TextView bottomNext;
    @BindView(R.id.remotePanel)
    ConstraintLayout remotePanel;
    @BindView(R.id.next)
    TextView nextButton;
    @BindView(R.id.bottom_message)
    TextView bottomMessage;
    RemoteHub remoteHub;
    Remote remote;
    private boolean settings;
    private Bundle newRemoteData = new Bundle();
    private List<Button> learnedButtons = new ArrayList<>();
    private ArrayList<RemoteData> remoteData = new ArrayList<>();
    private final String TAG = "AddNewRemoteActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_remote_activity);
        if (getIntent().getExtras() != null) {
            remoteHub = getIntent().getExtras().getParcelable("remoteHub");
            settings = getIntent().getExtras().getBoolean("settings");
            newRemoteData.putBoolean("learn", getIntent().getExtras().getBoolean("learn"));
            remote = getIntent().getExtras().getParcelable("remote");
            remoteData = getIntent().getExtras().getParcelableArrayList("remoteData");
        }
        Log.e(TAG, "RemoteHub"+remoteHub);
        Log.e(TAG, "Remote"+remote);
        Log.e(TAG, "RemoteData"+remoteData);
        Log.e(TAG, "Settings"+settings);
        Log.e(TAG, "Learn"+newRemoteData.getBoolean("learn"));
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(AddNewRemoteViewModel.class);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        progressDialog = new ProgressDialog(this);
        newRemoteData.putString("remoteHubId", remoteHub!=null?remoteHub.getId():"");
        onFragmentChanged();
        if (!settings) {
            selectLearnedFragment = NewRemoteSelectLearnedFragment.newInstance();
            currentFragment = (NewRemoteSelectLearnedFragment) selectLearnedFragment;
            if (savedInstanceState == null) {
                ft.add(R.id.container, selectLearnedFragment)
                        .commit();
            }
        }else{
            newRemoteControlBase = remote.getType().equals(AppConstants.REMOTE_TYPE_TV)? TvRemoteFragment.newInstance(remote, newRemoteData.getBoolean("learn"), remoteData):
                    ACRemoteFragment.newInstance(remote, newRemoteData.getBoolean("learn"), remoteData);
            currentFragment = newRemoteControlBase;
            fm.beginTransaction().add(R.id.container, newRemoteControlBase)
                    .commit();
            remotePanel.setVisibility(View.VISIBLE);
            nextButton.setText("ذخیره");
            findViewById(R.id.nextModel).setOnClickListener(v -> newRemoteControlBase.showButtons());
            message.setText("دکمه مورد نظر خود را لمس کنید");
            bottomNext.setText("مشاهده همه");
            bottomMessage.setText("تعداد دکمه‌های ایجاد شده: 0");

        }
    }

    public AddNewRemoteViewModel getViewModel(){
        return viewModel;
    }
    public void setSelectedBrand(String selectedBrand) {
        this.selectedBrand = selectedBrand;
    }

    @OnClick(R.id.next)
    public void onNextClicked() {
        currentFragment.verifyStatus();
    }
    ProgressDialog progressDialog;

    public void doOnNext(Bundle data) {
        newRemoteData.putAll(data);
        if (currentFragment instanceof NewRemoteSelectLearnedFragment) {
            selectTypeFragment = NewRemoteSelectTypeFragment.newInstance();
            currentFragment = (NewRemoteSelectTypeFragment) selectTypeFragment;
            fm.beginTransaction().add(R.id.container, selectTypeFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (currentFragment instanceof NewRemoteSelectTypeFragment) {
            selectBrandFragment = NewRemoteSelectBrandFragment.newInstance(newRemoteData.getString("type"));
            currentFragment = (AddNewRemoteNavListener) selectBrandFragment;
            fm.beginTransaction().add(R.id.container, selectBrandFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (currentFragment instanceof NewRemoteSelectBrandFragment) {
            modifyRemote(newRemoteData);
            newRemoteControlBase = newRemoteData.getString("type").equals(AppConstants.REMOTE_TYPE_TV)? TvRemoteFragment.newInstance(remote, newRemoteData.getBoolean("learn"), new ArrayList<>()):
                    ACRemoteFragment.newInstance(remote, newRemoteData.getBoolean("learn"), new ArrayList<>());
            currentFragment = newRemoteControlBase;
            fm.beginTransaction().add(R.id.container, newRemoteControlBase)
                    .addToBackStack(null)
                    .commit();
        } else if (currentFragment instanceof NewRemoteControlBase) {
            Log.e(TAG, "AFTER: "+remote);
            learnedButtons = newRemoteData.getParcelableArrayList("buttons");
            if (!settings) {
                modifyRemote(newRemoteData);
                setConfigurationFragment = NewRemoteSetConfigurationFragment.newInstance(remote, newRemoteData.getBoolean("learn"));
                currentFragment = setConfigurationFragment;
                fm.beginTransaction().add(R.id.container, setConfigurationFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                List<String> buttonNames = new ArrayList<>();
                for (Button b: learnedButtons)
                    buttonNames.add(b.getName());
                progressDialog.show();
                viewModel.addEditRemoteDataEditRemote(learnedButtons,remote, remoteData).observe(this, s -> {
                    List<String> data_ids = new ArrayList<>();
                    progressDialog.dismiss();
                    switch (s.first){
                        case AppConstants.SUCCESSFUL:
                            Toast.makeText(this, "ریموت با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show();
                            viewModel.getGroupsv3();
                            onBackPressed();
                            break;
                        case AppConstants.FORBIDDEN:
                            Toast.makeText(this, "شما دسترسی لازم برای این کار را ندارید", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.SOCKET_TIME_OUT:
                            AddNewDeviceActivity.getNewDevice().setFailed(true);
                            Toast.makeText(this, "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.CONNECT_EXCEPTION:
                            Toast.makeText(this, "ارسال پیام موفق نبود", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.UNKNOWN_HOST_EXCEPTION:
                            Toast.makeText(this, "ارتباط با اینترنت را بررسی کنید", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.UNKNOWN_EXCEPTION:
                            Toast.makeText(this, "یک مشکل ناشناخته رخ داده است", Toast.LENGTH_SHORT).show();
                            break;
                            default:
                                if (buttonNames.contains(s.first))
                                    data_ids.add(s.second);

                    }
                });
            }
        }else if (currentFragment instanceof NewRemoteSetConfigurationFragment) {
            modifyRemote(newRemoteData);
            progressDialog.show();
            if (!newRemoteData.getBoolean("learn")) {
                progressDialog.cancel.setOnClickListener(v -> {
                    progressDialog.dismiss();
                    viewModel.getAddRemoteDisposable().dispose();
                });
                viewModel.addRemote(remote).observe(this,s -> {
                    Log.e(TAG, "Remote Successfully Added");
                    progressDialog.dismiss();
                    switch (s) {
                        case AppConstants.FORBIDDEN:
                            Toast.makeText(this, "شما دسترسی لازم برای این کار را ندارید", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.OPERATION_DONE:
                            Log.e(this.getClass().getSimpleName(), "DONE CHANGENAME");
                            Toast.makeText(this, "ریموت با موفقیت اضافه شد", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                        case AppConstants.SOCKET_TIME_OUT:
                            AddNewDeviceActivity.getNewDevice().setFailed(true);
                            Toast.makeText(this, "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.CONNECT_EXCEPTION:
                            Toast.makeText(this, "ارسال پیام موفق نبود", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.UNKNOWN_HOST_EXCEPTION:
                            Toast.makeText(this, "ارتباط با اینترنت را بررسی کنید", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.UNKNOWN_EXCEPTION:
                            Toast.makeText(this, "یک مشکل ناشناخته رخ داده است", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
            }
            else {
                viewModel.addRemoteDataCreateRemote(learnedButtons, remote).observe(this, s -> {
                    Log.e(TAG, "AddRemoteDataResponse: "+s);
                    switch(s){
                        case AppConstants.SUCCESSFUL:
                            Toast.makeText(this, "ریموت با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                        case AppConstants.FORBIDDEN:
                            Toast.makeText(this, "شما دسترسی لازم برای این کار را ندارید", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.SOCKET_TIME_OUT:
                            AddNewDeviceActivity.getNewDevice().setFailed(true);
                            Toast.makeText(this, "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.CONNECT_EXCEPTION:
                            Toast.makeText(this, "ارسال پیام موفق نبود", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.UNKNOWN_HOST_EXCEPTION:
                            Toast.makeText(this, "ارتباط با اینترنت را بررسی کنید", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.UNKNOWN_EXCEPTION:
                            Toast.makeText(this, "یک مشکل ناشناخته رخ داده است", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
        }
        else {
            Log.e("::::::", "DATA: " + newRemoteData);
        }
    }




    @OnClick(R.id.back)
    public void onBackCLicked() {
        currentFragment.onBackClicked();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fm.getFragments().size() > 0)
            currentFragment = (AddNewRemoteNavListener) fm.getFragments().get(fm.getFragments().size() - 1);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }
    public void setBottomMessage(String message) {
        this.bottomMessage.setText(message);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onFragmentChanged() {
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                handleFragmentChange();
            }
        });
    }
    public void handleFragmentChange(){
        String topFragment = fm.getFragments().get(fm.getFragments().size() - 1).getClass().getName();
        if (topFragment.equals(selectLearnedFragment.getClass().getName())){
            setMessage("قصد انجام چه کاری را دارید؟");
        } else if (topFragment.equals(selectTypeFragment.getClass().getName())){
            setMessage("لطفا نوع دستگاه خود را انتخاب کنید");
        }else if (topFragment.equals(selectBrandFragment.getClass().getName())){
            remotePanel.setVisibility(View.GONE);
            nextButton.setText("بعدی");
            setMessage("لطفا برند دستگاه مورد نظر را وارد کنید");
        }else if (topFragment.equals(newRemoteControlBase.getClass().getName())){
            if (getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            remotePanel.setVisibility(View.VISIBLE);
            nextButton.setText("بعدی");
            if (!newRemoteData.getBoolean("learn")){
                findViewById(R.id.nextModel).setOnClickListener(v -> newRemoteControlBase.nextModel());
                message.setText("لطفا ریموت متناسب با دستگاه را انتخاب کنید");
            }else{
                findViewById(R.id.nextModel).setOnClickListener(v -> newRemoteControlBase.showButtons());
                message.setText("دکمه مورد نظر خود را لمس کنید");
                bottomNext.setText("مشاهده همه");
                bottomMessage.setText("تعداد دکمه‌های ایجاد شده: 0");
            }
        }else if (topFragment.equals(setConfigurationFragment.getClass().getName())){
            remotePanel.setVisibility(View.GONE);
            nextButton.setText("تمام");
            setMessage("لطفا نام و مشخصات ریموت را وارد کنید");
        }
    }
    public void modifyRemote(Bundle data){
        remote = new Remote();
        if (remoteHub != null){
            remote.setGroupId(remoteHub.getGroupId());
            remote.setTopic(new Topic(remoteHub.getGroupId()+"/"+remoteHub.getId()+"/"));
        }
        remote.setName(data.getString("name"));
        remote.setType(data.getString(AppConstants.PARAM_TYPE));
        remote.setBrand(data.getString(AppConstants.PARAM_BRAND));
        remote.setModel(data.getString(AppConstants.PARAM_MODEL));
        remote.setRemoteHubId(data.getString("remoteHubId"));
        remote.setVisibility(data.getBoolean("visibility"));
        remote.setFavorite(data.getBoolean("favorite"));
    }

    public NewRemoteSetConfigurationFragment getSetConfigurationFragment() {
        return setConfigurationFragment;
    }
}
