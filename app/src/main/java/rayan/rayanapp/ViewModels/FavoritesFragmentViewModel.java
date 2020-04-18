package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;

public class FavoritesFragmentViewModel extends DevicesFragmentViewModel {
    public FavoritesFragmentViewModel(@NonNull Application application) {
        super(application);

    }

    @Override
    public LiveData<List<Device>> getAllDevicesLive() {
        return deviceDatabase.getFavoriteDevices();
    }

    @Override
    public LiveData<List<RemoteHub>> getAllRemoteHubsLive() {
        return remoteHubDatabase.getFavoriteRemoteHubs();
    }

    @Override
    public LiveData<List<Remote>> getAllRemotesLive() {
        return remoteDatabase.getFavoriteRemotes();
    }
}

