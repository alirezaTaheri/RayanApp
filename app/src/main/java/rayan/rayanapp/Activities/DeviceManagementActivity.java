package rayan.rayanapp.Activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Fragments.BackHandledFragment;
import rayan.rayanapp.Fragments.DevicesManagementListFragment;
import rayan.rayanapp.Fragments.EditDeviceFragment;
import rayan.rayanapp.Fragments.EditRemoteFragment;
import rayan.rayanapp.Fragments.EditRemoteHubFragment;
import rayan.rayanapp.Fragments.ProvideInternetFragment;
import rayan.rayanapp.Fragments.YesNoButtomSheetFragment;
import rayan.rayanapp.Helper.DialogPresenter;
import rayan.rayanapp.Listeners.DoneWithSelectAccessPointFragment;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.DevicesManagementActivityViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DeviceManagementActivity extends AppCompatActivity implements DevicesManagementListFragment.ClickOnDevice, BackHandledFragment.BackHandlerInterface, DoneWithSelectAccessPointFragment, OnBottomSheetSubmitClicked {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    public EditDeviceFragment editDeviceFragment;
    public EditRemoteHubFragment editRemoteHubFragment;
    public EditRemoteFragment editRemoteFragment;
    YesNoButtomSheetFragment yesNoButtomSheetFragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    DevicesManagementActivityViewModel viewModel;
    BackHandledFragment currentFragment;
    Device device;
    DialogPresenter dp;
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);
        viewModel = ViewModelProviders.of(this).get(DevicesManagementActivityViewModel.class);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        yesNoButtomSheetFragment=new YesNoButtomSheetFragment();
        yesNoButtomSheetFragment.setOnBottomSheetSubmitClicked(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        fragmentManager = getSupportFragmentManager();
         transaction = fragmentManager.beginTransaction();
        DevicesManagementListFragment devicesManagementListFragment = DevicesManagementListFragment.newInstance();
//        EditDeviceFragment devicesManagementListFragment = EditDeviceFragment.newInstance(viewModel.getDevice("222222"));
        transaction.replace(R.id.frameLayout, devicesManagementListFragment);
        transaction.commit();
        dp = new DialogPresenter(getSupportFragmentManager());
    }

    @SuppressLint("CheckResult")
    @Override
    public void clickOnDevice(Device device) {}

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        this.currentFragment = backHandledFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home && currentFragment == null || !currentFragment.onBackPressed()){
//                return super.onOptionsItemSelected(item);
//        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment == null || !currentFragment.onBackPressed())
            super.onBackPressed();
    }

    public void setActionBarTitle(){
//        getSupportActionBar().setTitle(R.string.title_deviceManagementActivity);
        getSupportActionBar().setTitle("");
    }

    @Override
    public void accessPointSelected(String ssid, String pass) {
        ((DoneWithSelectAccessPointFragment)getSupportFragmentManager().getFragments().get(0)).accessPointSelected(ssid, pass);
    }

    public void submitClicked(String tag) {
        Log.e("tag of fragment",tag);
        switch (tag){
            case "EditDeviceFragment":
                Toast.makeText(this, ".......", Toast.LENGTH_SHORT).show();
//                editDeviceFragment.clickOnDeviceUpdateSubmit();
                break;
            case "resetDevice":
                viewModel.internetProvided().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean)
                            editDeviceFragment.resetDevice();
                        else dp.showDialog(AppConstants.DIALOG_PROVIDE_INTERNET, null);
                    }
                });
                break;
            case "resetRemoteHub":
                viewModel.internetProvided().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean)
                            editRemoteHubFragment.resetRemoteHub();
                        else dp.showDialog(AppConstants.DIALOG_PROVIDE_INTERNET, null);
                    }
                });
                break;
            default:
                break;
                }
        }
}
