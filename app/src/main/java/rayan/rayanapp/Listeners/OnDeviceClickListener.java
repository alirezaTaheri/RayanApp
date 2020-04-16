package rayan.rayanapp.Listeners;

public interface OnDeviceClickListener<T> extends BaseRecyclerListener {
    void onPin1Clicked(T Item, int position);
    void onPin2Clicked(T Item, int position);
    void onAccessPointChanged(T item);
    void onClick_RemoteHub(T Item, int position);
    void onClick_Remote(T item, int position);
}
