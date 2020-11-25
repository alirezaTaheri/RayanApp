package rayan.rayanapp.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;

public class SettingssActivity extends BaseActivity {
    @BindView(R.id.soundswitch)
    SwitchCompat soundswitch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        if (RayanApplication.getPref().getIsNodeSoundOn()){
            soundswitch.setChecked(true);
        }else { soundswitch.setChecked(false); }
        soundswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                RayanApplication.getPref().setIsNodeSoundOn(true);
                Toast.makeText(this, "صدای دستگاه‌ها فعال شد", Toast.LENGTH_SHORT).show();
            } else {
                RayanApplication.getPref().setIsNodeSoundOn(false);
                Toast.makeText(this,"صدای دستگاه‌ها غیر‌فعال شد", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
