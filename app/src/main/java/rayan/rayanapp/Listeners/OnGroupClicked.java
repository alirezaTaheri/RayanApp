package rayan.rayanapp.Listeners;

public interface OnGroupClicked<T> extends BaseRecyclerListener {
    void onGroupClicked(T item);
    void onGroupLongPress(T Item);
}
