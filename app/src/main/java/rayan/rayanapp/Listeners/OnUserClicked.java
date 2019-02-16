package rayan.rayanapp.Listeners;

public interface OnUserClicked<T> extends BaseRecyclerListener {
    void onRemoveUserClicked(T item);
}
