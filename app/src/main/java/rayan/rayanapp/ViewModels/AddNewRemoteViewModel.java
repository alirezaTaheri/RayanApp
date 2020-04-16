package rayan.rayanapp.ViewModels;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddNewRemoteViewModel extends ViewModel {
    private final String[] types = {"TV","AC","Learn"};
    private final String[] brands = {"Aux","Samsung","LG", "Toshiba"};

    public List<String> getAllTypes(){
        return new ArrayList<>(Arrays.asList(this.types));
    }
    public List<String> getAllBrands(){
        return new ArrayList<>(Arrays.asList(this.brands));
    }
}
