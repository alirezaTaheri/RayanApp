package rayan.rayanapp.Helper;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import java.util.List;

import io.reactivex.subjects.PublishSubject;
import rayan.rayanapp.Fragments.ProvideInternetFragment;
import rayan.rayanapp.Util.AppConstants;

public class DialogPresenter {
    private FragmentManager fm;
    public DialogPresenter(FragmentManager fm) {
        this.fm = fm;
    }

    public void showDialog(String type, List<String> params){
        switch (type){
            case AppConstants.DIALOG_PROVIDE_INTERNET:
                ProvideInternetFragment p = ProvideInternetFragment.newInstance();
                p.show(fm, "");
                break;
        }
    }
}
