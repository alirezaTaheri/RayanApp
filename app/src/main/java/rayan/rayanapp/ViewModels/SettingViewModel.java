package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import rayan.rayanapp.App.RayanApplication;

public class SettingViewModel extends DevicesFragmentViewModel {
    private final String TAG = SettingViewModel.class.getSimpleName();

    public SettingViewModel(@NonNull Application application) {
        super(application);
    }

    public void setNotificationState(String notificationKey){
        RayanApplication.getPref().showNotification(notificationKey);
    }
    public String getNotificationState(){
        return RayanApplication.getPref().getShowNotification();
    }
    public void setThemeKey(String key){
        RayanApplication.getPref().setThemeKey(key);
    }
    public String getThemeKey(){
        return RayanApplication.getPref().getThemeKey();
    }
}
