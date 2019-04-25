package rayan.rayanapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingssActivity extends AppCompatActivity {
    @BindView(R.id.soundswitch)
    SwitchCompat soundswitch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

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
                Toast.makeText(SettingssActivity.this, RayanApplication.getPref().getIsNodeSoundOn() + "", Toast.LENGTH_SHORT).show();
            } else {
                RayanApplication.getPref().setIsNodeSoundOn(false);
                Toast.makeText(SettingssActivity.this, RayanApplication.getPref().getIsNodeSoundOn() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
