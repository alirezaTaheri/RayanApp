package rayan.rayanapp.Adapters.arrayAdapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import androidx.annotation.NonNull;

public class SpinnerCustomAdapter extends ArrayAdapter<String> {
    public SpinnerCustomAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return super.getCount() > 0 ? super.getCount()-1 : super.getCount();
    }

}
