package rayan.rayanapp.Activities;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

import rayan.rayanapp.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preferences);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getListView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
}
