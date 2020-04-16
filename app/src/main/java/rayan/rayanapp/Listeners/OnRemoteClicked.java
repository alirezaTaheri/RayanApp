package rayan.rayanapp.Listeners;

public interface OnRemoteClicked<T> extends BaseRecyclerListener {
    void onRemoteClicked(T item);
}
