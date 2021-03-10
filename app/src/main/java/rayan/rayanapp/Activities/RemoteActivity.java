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
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Persistance.database.RemoteDataDatabase;
import rayan.rayanapp.Persistance.database.RemoteDatabase;
import rayan.rayanapp.Persistance.database.RemoteHubDatabase;
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
    private final String TAG = "RemoteActivity";
    @Override
    public void onBackPressed() {
        DeviceDatabase deviceDatabase = new DeviceDatabase(this);
        RemoteDataDatabase remoteDataDatabase = new RemoteDataDatabase(this);
        RemoteDatabase remoteDatabase = new RemoteDatabase(this);
        RemoteHubDatabase remoteHubDatabase = new RemoteHubDatabase(this);
        GroupDatabase groupDatabase = new GroupDatabase(this);
        Log.e(TAG, "G"+groupDatabase.getAllGroups());
        Log.e(TAG, "D"+deviceDatabase.getAllDevices());
        Log.e(TAG, "RH"+remoteHubDatabase.getAllRemoteHubs());
        Log.e(TAG, "R"+remoteDatabase.getAllRemotes());
        Log.e(TAG, "RD"+remoteDataDatabase.getAllRemoteDatas());
    }
}
