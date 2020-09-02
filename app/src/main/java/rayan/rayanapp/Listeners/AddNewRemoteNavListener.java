package rayan.rayanapp.Listeners;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.Map;

public interface AddNewRemoteNavListener {
    void onBackClicked();
    void goToNextStep(Bundle data);
    void verifyStatus();
}
