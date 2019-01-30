package rayan.rayanapp.Listeners;

public interface OnDeviceClickListenerManagement<T> extends BaseRecyclerListener {
    void onItemClick(T item);
}
