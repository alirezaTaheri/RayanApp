package rayan.rayanapp.Activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Fragments.NewRemoteSelectBrandFragment;
import rayan.rayanapp.Fragments.NewRemoteSelectTypeFragment;
import rayan.rayanapp.Fragments.NewRemoteFragment;
import rayan.rayanapp.Fragments.TvRemoteFragment;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddNewRemoteActivity extends AppCompatActivity {

    FragmentManager fm;
    FragmentTransaction ft;
    private String selectedBrand;
    private NewRemoteSelectTypeFragment selectTypeFragment;
    private NewRemoteSelectBrandFragment selectBrandFragment;
    private AddNewRemoteNavListener currentFragment;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.remotePanel)
    ConstraintLayout remotePanel;
    @BindView(R.id.next)
    TextView nextButton;
    private Map<String, String> newRemoteData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_remote_activity);
        ButterKnife.bind(this);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
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


    public void doOnNext(Map<String, String> map) {
        newRemoteData.putAll(map);
        if (currentFragment instanceof NewRemoteSelectTypeFragment) {
            selectBrandFragment = NewRemoteSelectBrandFragment.newInstance(newRemoteData.get("type"));
            currentFragment = (AddNewRemoteNavListener) selectBrandFragment;
            fm.beginTransaction().add(R.id.container, selectBrandFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (currentFragment instanceof NewRemoteSelectBrandFragment) {
            NewRemoteFragment newRemoteFragment = NewRemoteFragment.newInstance(newRemoteData.get("type"));
            currentFragment = (AddNewRemoteNavListener) newRemoteFragment;
            fm.beginTransaction().add(R.id.container, newRemoteFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (currentFragment instanceof TvRemoteFragment) {
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
}
