package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import rayan.rayanapp.Data.Device;

public class FavoritesFragmentViewModel extends DevicesFragmentViewModel {
    public FavoritesFragmentViewModel(@NonNull Application application) {
        super(application);

    }

    @Override
    public LiveData<List<Device>> getAllDevices() {
        return deviceDatabase.getFavoriteDevices();
    }
}

