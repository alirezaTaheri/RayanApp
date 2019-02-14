package rayan.rayanapp.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.EditUserViewModel;
import rayan.rayanapp.ViewModels.LoginViewModel;
import rayan.rayanapp.ViewModels.SettingViewModel;

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private final String TAG = SettingActivity.class.getSimpleName();
    SettingViewModel settingViewModel;
    String switchOn = "notification is ON";
    String switchOff = "notification is OFF";
    @BindView(R.id.notification_setting_switch)
    Switch notificationSwitch;
    @BindView(R.id.notificationHint_setting_switch)
    TextView notificationSwitchHint;
    @BindView(R.id.theme_select_layout)
    CoordinatorLayout themeBottomSheet;
    @BindView(R.id.theme_setting_radiogroup)
    RadioGroup theme_setting_radiogroup;
    @BindView(R.id.theme1_setting_radiobutton)
    RadioButton theme1_setting_radiobutton;
    @BindView(R.id.theme2_setting_radiobutton)
    RadioButton theme2_setting_radiobutton;
    BottomSheetBehavior sheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        settingViewModel= ViewModelProviders.of(this).get(SettingViewModel.class);
        Toast.makeText(this,"notification= "+settingViewModel.getNotificationState() , Toast.LENGTH_SHORT).show();

        notificationSwitch.setChecked(true);
        notificationSwitchHint.setText(switchOn);
        notificationSwitch.setOnCheckedChangeListener(this);

        Toast.makeText(this,"theme= "+settingViewModel.getThemeKey() , Toast.LENGTH_SHORT).show();

        sheetBehavior = BottomSheetBehavior.from(themeBottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int state) {
                switch (state) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                       // btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                       // btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            notificationSwitchHint.setText(switchOn);
            settingViewModel.setNotificationState("ON");
            Toast.makeText(this,"notification= "+settingViewModel.getNotificationState() , Toast.LENGTH_SHORT).show();

        } else {
            notificationSwitchHint.setText(switchOff);
            settingViewModel.setNotificationState("OFF");
            Toast.makeText(this,"notification= "+settingViewModel.getNotificationState() , Toast.LENGTH_SHORT).show();

        }
    }
    @OnClick(R.id.choseThemebutton)
    void clickOnSelectTheme(){
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
           // btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
           // btnBottomSheet.setText("Expand sheet");
        }
    }

    public void radioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // This check which radio button was clicked
        switch (view.getId()) {
            case R.id.theme1_setting_radiobutton:
                if (checked)
                    settingViewModel.setThemeKey("1");
                    Toast.makeText(this,"theme= "+settingViewModel.getThemeKey() , Toast.LENGTH_SHORT).show();
                break;

            case R.id.theme2_setting_radiobutton:
                settingViewModel.setThemeKey("2");
                Toast.makeText(this,"theme= "+settingViewModel.getThemeKey() , Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
