package rayan.rayanapp.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Fragments.NewRemoteFragment;
import rayan.rayanapp.Fragments.NewRemoteSelectBrandFragment;
import rayan.rayanapp.Fragments.NewRemoteSelectTypeFragment;
import rayan.rayanapp.Fragments.NewRemoteSetConfigurationFragment;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.AddNewRemoteViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddNewRemoteActivity extends AppCompatActivity {

    FragmentManager fm;
    FragmentTransaction ft;
    private String selectedBrand;
    AddNewRemoteViewModel viewModel;
    private NewRemoteSelectTypeFragment selectTypeFragment;
    private NewRemoteSelectBrandFragment selectBrandFragment;
    private NewRemoteFragment newRemoteFragment;
    private NewRemoteSetConfigurationFragment setConfigurationFragment;
    private AddNewRemoteNavListener currentFragment;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.remotePanel)
    ConstraintLayout remotePanel;
    @BindView(R.id.next)
    TextView nextButton;
    RemoteHub remoteHub;
    Remote remote;
    private Bundle newRemoteData = new Bundle();
    private final String TAG = "AddNewRemoteActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_remote_activity);
        remoteHub = getIntent().getExtras().getParcelable("remoteHub");
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(AddNewRemoteViewModel.class);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        newRemoteData.putString("remoteHubId", remoteHub.getId());
        onFragmentChanged();
        selectTypeFragment = NewRemoteSelectTypeFragment.newInstance();
        currentFragment = (AddNewRemoteNavListener) selectTypeFragment;
        if (savedInstanceState == null) {
            ft.add(R.id.container, selectTypeFragment)
                    .commit();
        }
    }


    public void setSelectedBrand(String selectedBrand) {
        this.selectedBrand = selectedBrand;
    }

    @OnClick(R.id.next)
    public void onNextClicked() {
        currentFragment.verifyStatus();
    }


    public void doOnNext(Bundle data) {
        newRemoteData.putAll(data);
        if (currentFragment instanceof NewRemoteSelectTypeFragment) {
            selectBrandFragment = NewRemoteSelectBrandFragment.newInstance(newRemoteData.getString("type"));
            currentFragment = (AddNewRemoteNavListener) selectBrandFragment;
            fm.beginTransaction().add(R.id.container, selectBrandFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (currentFragment instanceof NewRemoteSelectBrandFragment) {
            newRemoteFragment = NewRemoteFragment.newInstance(newRemoteData.getString("type"));
            currentFragment = (AddNewRemoteNavListener) newRemoteFragment;
            fm.beginTransaction().add(R.id.container, newRemoteFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (currentFragment instanceof NewRemoteFragment) {
            modifyRemote(newRemoteData);
            Log.e(TAG, "AFTER: "+remote);
            setConfigurationFragment = NewRemoteSetConfigurationFragment.newInstance(remote);
            currentFragment = setConfigurationFragment;
            fm.beginTransaction().add(R.id.container, setConfigurationFragment)
                    .addToBackStack(null)
                    .commit();
        }else if (currentFragment instanceof NewRemoteSetConfigurationFragment) {
            modifyRemote(newRemoteData);
            viewModel.addRemote(remote).observe(this,s -> {
                Log.e(TAG, "Remote Successfully Added");
                switch (s){
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
                        Log.e(this.getClass().getSimpleName(), "SOCKET_TIME_OUT CHANGENAME");
                        Toast.makeText(this, "خطای اتصال", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.NETWORK_ERROR:
                        Log.e(this.getClass().getSimpleName(), "NETWORK_ERROR CHANGENAME");
                        Toast.makeText(this, "خطای اتصال", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.ERROR:
                        Log.e(this.getClass().getSimpleName(), "ERROR CHANGENAME");
                        Toast.makeText(this, "خطایی رخ داد", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onFragmentChanged() {
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                String topFragment = fm.getFragments().get(fm.getFragments().size() - 1).getClass().getName();
                if (topFragment.equals(selectTypeFragment.getClass().getName())){
                    setMessage("لطفا نوع دستگاه خود را انتخاب کنید");
                }else if (topFragment.equals(selectBrandFragment.getClass().getName())){
                    remotePanel.setVisibility(View.GONE);
                    nextButton.setText("بعدی");
                    setMessage("لطفا برند دستگاه مورد نظر را وارد کنید");
                }else if (topFragment.equals(newRemoteFragment.getClass().getName())){
                    remotePanel.setVisibility(View.GONE);
                    nextButton.setText("بعدی");
                    setMessage("لطفا برند دستگاه مورد نظر را وارد کنید");
                }else if (topFragment.equals(setConfigurationFragment.getClass().getName())){
                    remotePanel.setVisibility(View.GONE);
                    nextButton.setText("تمام");
                    setMessage("لطفا نام و مشخصات ریموت را وارد کنید");
                }else {
                    if (getCurrentFocus() != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    remotePanel.setVisibility(View.VISIBLE);
                    nextButton.setText("تایید");
                    message.setText("لطفا ریموت متناسب با دستگاه را انتخاب کنید");
                }
            }
        });
    }
    public void modifyRemote(Bundle data){
        remote = new Remote();
        remote.setGroupId(remoteHub.getGroupId());
        remote.setName(data.getString("name"));
        remote.setType(data.getString("type"));
        remote.setTopic(new Topic(remoteHub.getGroupId()+"/"+remoteHub.getId()+"/"));
        remote.setBrand(data.getString("brand"));
        remote.setModel(data.getString("model"));
        remote.setRemoteHubId(data.getString("remoteHubId"));
        remote.setVisibility(data.getBoolean("visibility"));
        remote.setFavorite(data.getBoolean("favorite"));
    }
}
