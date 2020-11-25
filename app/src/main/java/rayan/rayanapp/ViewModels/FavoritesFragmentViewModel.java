package rayan.rayanapp.ViewModels;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import rayan.rayanapp.Data.Device;

public class FavoritesFragmentViewModel extends DevicesFragmentViewModel {
    public FavoritesFragmentViewModel(@NonNull Application application) {
        super(application);

    }

    @Override
    public LiveData<List<Device>> getAllDevicesLive() {
        return deviceDatabase.getFavoriteDevices();
    }
}

