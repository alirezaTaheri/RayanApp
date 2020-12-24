package rayan.rayanapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;
import rayan.rayanapp.Fragments.NewRemoteFragment;
import rayan.rayanapp.Fragments.RemoteFragment;
import rayan.rayanapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RemoteActivity extends AppCompatActivity {

    FragmentManager fm;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_activity);
        ButterKnife.bind(this);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        RemoteFragment remoteFragment = RemoteFragment.newInstance(getIntent().getExtras().getParcelable("remote"));
        if (savedInstanceState == null) {
            ft.add(R.id.container, remoteFragment)
                    .commit();
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
