package rayan.rayanapp.Helper;

import androidx.fragment.app.FragmentManager;

import java.util.Map;

import rayan.rayanapp.Fragments.AlertBottomSheetFragment;
import rayan.rayanapp.Fragments.ProvideInternetFragment;
import rayan.rayanapp.Util.AppConstants;

public class DialogPresenter {
    private FragmentManager fm;
    public DialogPresenter(FragmentManager fm) {
        this.fm = fm;
    }

    public void showDialog(String type, Object o){
        switch (type){
            case AppConstants.DIALOG_PROVIDE_INTERNET:
                ProvideInternetFragment p = ProvideInternetFragment.newInstance();
                p.show(fm, "");
                break;
            case AppConstants.DIALOG_ALERT:
                AlertBottomSheetFragment h = AlertBottomSheetFragment.instance(((Map<String, String>)o).get("message"));
                h.show(fm,"");
                break;
        }
    }
}
