package rayan.rayanapp.Util;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import rayan.rayanapp.MainActivity.adapters.DevicesRecyclerViewAdapter;

public abstract class BaseViewHolder<T, L extends BaseRecyclerListener> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Bind data to the item and set listener if needed.
     *
     * @param item     object, associated with the item.
     * @param listener listener a listener {@link BaseRecyclerListener} which has to b set at the item (if not `null`).
     */
    public abstract void onBind(T item, @Nullable L listener, DevicesRecyclerViewAdapter.OnDragStartListener mDragStartListener);
}
