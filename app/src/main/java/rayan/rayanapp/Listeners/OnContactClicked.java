package rayan.rayanapp.Listeners;

public interface OnContactClicked<T> extends BaseRecyclerListener {
    void onContactClicked(T item);
}
