package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.support.annotation.NonNull;
import rayan.rayanapp.Services.udp.SendUDPMessage;

public class DevicesManagementActivityViewModel extends DevicesFragmentViewModel {
    private final String TAG = DevicesManagementActivityViewModel.class.getSimpleName();
    public DevicesManagementActivityViewModel(@NonNull Application application) {
        super(application);
    }

}

