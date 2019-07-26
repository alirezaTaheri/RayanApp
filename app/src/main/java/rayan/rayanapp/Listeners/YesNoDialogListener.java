package rayan.rayanapp.Listeners;

import android.os.Bundle;

import rayan.rayanapp.Dialogs.YesNoDialog;

public interface YesNoDialogListener {
    void onYesClicked(YesNoDialog yesNoDialog, Bundle data);
    void onNoClicked(YesNoDialog yesNoDialog, Bundle data);
}
