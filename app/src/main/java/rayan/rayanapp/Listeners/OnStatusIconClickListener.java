package rayan.rayanapp.Listeners;

public interface OnStatusIconClickListener<T> extends BaseRecyclerListener {
    void onPin1Clicked(T Item);
    void onPin2Clicked(T Item);

}
