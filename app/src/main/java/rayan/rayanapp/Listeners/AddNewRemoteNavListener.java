package rayan.rayanapp.Listeners;

import android.support.v4.app.Fragment;

import java.util.Map;

public interface AddNewRemoteNavListener {
    void onBackClicked();
    void goToNextStep(Map<String, String> data);
    void verifyStatus();
}
