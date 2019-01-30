package rayan.rayanapp.App;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import rayan.rayanapp.Activities.LoginActivity;
import rayan.rayanapp.Persistance.PrefManager;

public class RayanApplication extends Application {
    private static Context context;
    private static PrefManager pref;
    @Override
    public void onCreate() {
        super.onCreate();
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Dosis_Regular.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );
        context = this;
        pref = new PrefManager();
        if (!pref.isLoggedIn()){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public static Context getContext(){
        return context;
    }
    public static PrefManager getPref(){
        return pref;
    }
}
