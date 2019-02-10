package rayan.rayanapp.Listeners;

public interface OnNewDeviceClicked<T> extends BaseRecyclerListener {
    void onItemClicked(T item);
}
