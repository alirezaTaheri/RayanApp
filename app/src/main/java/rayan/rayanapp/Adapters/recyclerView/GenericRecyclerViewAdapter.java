package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rayan.rayanapp.Listeners.BaseRecyclerListener;
import rayan.rayanapp.ViewHolders.BaseViewHolder;

public abstract class GenericRecyclerViewAdapter<T, L extends BaseRecyclerListener, VH extends BaseViewHolder<T,L>>
        extends RecyclerView.Adapter<VH> {

    protected List<T> items;
    private L listener;
    private LayoutInflater layoutInflater;
    public GenericRecyclerViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        items = new ArrayList<>();
    }
    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        T item = items.get(position);
        holder.onBind(item, listener);
    }
    public void setItems(List<T> items) {
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }
    public void addItemToFirst(T item){
        if (item != null)
            this.items.add(0,item);
    }
    public List<T> getItems() {
        return items;
    }

    public T getItem(int position) {
        return items.get(position);
    }
    public void remove(T item) {
        int position = items.indexOf(item);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void onItemMove(int fromPosition, int toPosition){
        if (fromPosition != -1 && toPosition != -1) {
            Log.e("!@#", "old: " + items);
            items.add(toPosition, items.remove(fromPosition));
            notifyItemMoved(fromPosition, toPosition);
            Log.e("!@#", "new: " + items);
        }else Log.e("GenericRecyclerAdapter", "Index Problem => from: " + fromPosition + " to: " +toPosition);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
    public void setListener(L listener) {
        this.listener = listener;
    }

    public L getListener() {
        return listener;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }
    @NonNull
    protected View inflate(@LayoutRes final int layout, @Nullable final ViewGroup parent, final boolean attachToRoot) {
        return layoutInflater.inflate(layout, parent, attachToRoot);
    }
    @NonNull
    protected View inflate(@LayoutRes final int layout, final @Nullable ViewGroup parent) {
        return inflate(layout, parent, false);
    }
    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}
