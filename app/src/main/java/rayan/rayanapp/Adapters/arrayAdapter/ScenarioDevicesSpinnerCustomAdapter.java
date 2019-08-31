package rayan.rayanapp.Adapters.arrayAdapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import rayan.rayanapp.Data.ScenarioDeviceSpinnerItem;

public class ScenarioDevicesSpinnerCustomAdapter extends ArrayAdapter<ScenarioDeviceSpinnerItem> {
    public ScenarioDevicesSpinnerCustomAdapter(@NonNull Context context, int resource, @NonNull List<ScenarioDeviceSpinnerItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return super.getCount() > 0 ? super.getCount()-1 : super.getCount();
    }

}
