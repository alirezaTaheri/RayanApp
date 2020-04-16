package rayan.rayanapp.Listeners;

public interface OnRemoteBrandClicked<T> extends BaseRecyclerListener {
    void onRemoteBrandClicked(T item, int position);
}
