package rayan.rayanapp.Listeners;

import android.view.View;

public interface OnNewDeviceClicked<T> extends BaseRecyclerListener {
    void onItemClicked(T item);
    void onTestDeviceClicked(T item);
}
