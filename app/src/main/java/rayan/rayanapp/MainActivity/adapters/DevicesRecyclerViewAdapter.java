package rayan.rayanapp.MainActivity.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.ItemTouchHelperAdapter;
import rayan.rayanapp.MainActivity.OnStatusIconClickListener;
import rayan.rayanapp.MainActivity.viewHolders.DeviceViewHolder1Bridge;
import rayan.rayanapp.MainActivity.viewHolders.DeviceViewHolder2Bridges;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.DevicesDiffCallBack;
import rayan.rayanapp.Util.GenericRecyclerViewAdapter;

public class DevicesRecyclerViewAdapter extends GenericRecyclerViewAdapter<Device,OnStatusIconClickListener<Device>, DeviceViewHolder1Bridge>
        implements ItemTouchHelperAdapter {

    public interface OnDragStartListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }


    private OnDragStartListener dragStartListener;
    public DevicesRecyclerViewAdapter(Context context, List<Device> devices, OnDragStartListener dragStartListener) {
        super(context);
        this.items = devices;
        this.dragStartListener = dragStartListener;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder1Bridge holder, int position) {
        Device item = items.get(position);
        holder.onBind(item, getListener(), dragStartListener);
    }

    public void updateItems(List<Device> items){
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
        DevicesDiffCallBack devicesDiffCallBack = new DevicesDiffCallBack(items, this.items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(devicesDiffCallBack);
        this.items.clear();
        this.items.addAll(items);
        diffResult.dispatchUpdatesTo(this);

    }

    @NonNull
    @Override
    public DeviceViewHolder1Bridge onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new DeviceViewHolder1Bridge(inflate(R.layout.item_device_1_bridge, parent));
        else if (viewType == 2)
            return new DeviceViewHolder2Bridges(inflate(R.layout.item_device_2_bridge, parent));
        else
            return new DeviceViewHolder1Bridge(inflate(R.layout.item_device_plug, parent));
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getType().equals("switch_1"))
            return 1;
        else if (items.get(position).getType().equals("switch_2"))
            return 2;
        else return 3;
    }

    @Override
    public void onItemDismiss(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Device prev = items.remove(fromPosition);
        items.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }


}
