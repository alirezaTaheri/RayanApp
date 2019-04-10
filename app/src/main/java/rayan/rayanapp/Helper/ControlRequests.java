package rayan.rayanapp.Helper;

import android.content.Context;

import java.util.List;

import rayan.rayanapp.Data.LocallyChange;
import rayan.rayanapp.Persistance.database.LocallyChangesDatabase;

public class ControlRequests {
    LocallyChangesDatabase lcd;
    public ControlRequests(Context context) {
        lcd = new LocallyChangesDatabase(context);
    }
    public List<LocallyChange> getAllRequests(){
        return lcd.getAllRequest();
    }
    public List<LocallyChange> getAllRequests(LocallyChange.Type type){
        return lcd.getAllRequest(type);
    }

    public void requestSuccess(LocallyChange locallyChange){
        lcd.requestSuccess(locallyChange);
    }

    public void addRequest(LocallyChange locallyChange){
        lcd.addRequest(locallyChange);
    }

    public void submitRequests(){
        lcd.submitRequests();
    }
}
