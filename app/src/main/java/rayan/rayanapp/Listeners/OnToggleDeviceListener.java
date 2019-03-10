package rayan.rayanapp.Listeners;

public interface OnToggleDeviceListener<T> extends BaseRecyclerListener {
    void onPin1Clicked(T Item, int position);
    void onPin2Clicked(T Item, int position);
    int getItemWidth(int position);
}
