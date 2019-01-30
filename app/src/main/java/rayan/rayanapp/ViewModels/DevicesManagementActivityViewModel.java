package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import rayan.rayanapp.Data.Device;

public class DevicesManagementActivityViewModel extends DevicesFragmentViewModel {
    public DevicesManagementActivityViewModel(@NonNull Application application) {
        super(application);

    }

    @Override
    public LiveData<List<Device>> getAllDevices() {
        return deviceDatabase.getAllDevicesLive();
    }
}

