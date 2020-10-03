package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.diffUtil.BaseDeviceDiffCallBack;
import rayan.rayanapp.ViewHolders.BaseViewHolder;
import rayan.rayanapp.ViewHolders.GroupBaseDevicesViewHolder;

public class GroupDevicesRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    Context context;
    List<BaseDevice> items;
    public GroupDevicesRecyclerViewAdapter(Context context, List<BaseDevice> items) {
        layoutInflater = LayoutInflater.from(context);
        this.context=context;
        this.items =  items;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GroupBaseDevicesViewHolder(inflate(R.layout.item_device_edit_group, parent),context);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.onBind(items.get(i), null);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<BaseDevice> items){
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }
    @NonNull
    protected View inflate(@LayoutRes final int layout, final @Nullable ViewGroup parent) {
        return inflate(layout, parent, false);
    }
    private LayoutInflater layoutInflater;
    @NonNull
    protected View inflate(@LayoutRes final int layout, @Nullable final ViewGroup parent, final boolean attachToRoot) {
        return layoutInflater.inflate(layout, parent, attachToRoot);
    }
}
