package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnStatusIconClickListener;
import rayan.rayanapp.ViewHolders.DeviceViewHolder1Bridge;
import rayan.rayanapp.ViewHolders.DeviceViewHolder2Bridges;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;

public class DevicesRecyclerViewAdapter extends GenericRecyclerViewAdapter<Device,OnStatusIconClickListener<Device>, DeviceViewHolder1Bridge> {


    public DevicesRecyclerViewAdapter(Context context, List<Device> devices) {
        super(context);
        this.items = devices;
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
}
