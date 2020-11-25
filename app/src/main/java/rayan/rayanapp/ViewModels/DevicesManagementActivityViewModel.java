package rayan.rayanapp.ViewModels;

import android.app.Application;
import androidx.annotation.NonNull;

public class DevicesManagementActivityViewModel extends DevicesFragmentViewModel {
    private final String TAG = DevicesManagementActivityViewModel.class.getSimpleName();
    public DevicesManagementActivityViewModel(@NonNull Application application) {
        super(application);
    }

}

