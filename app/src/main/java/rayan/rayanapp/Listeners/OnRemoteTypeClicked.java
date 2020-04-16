package rayan.rayanapp.Listeners;

public interface OnRemoteTypeClicked<T> extends BaseRecyclerListener {
    void onRemoteTypeClicked(T item, int position);
}
