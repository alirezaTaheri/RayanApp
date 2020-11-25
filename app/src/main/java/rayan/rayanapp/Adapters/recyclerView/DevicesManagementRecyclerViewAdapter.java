package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;
import rayan.rayanapp.ViewHolders.DeviceViewHolderManagement;

public class DevicesManagementRecyclerViewAdapter extends GenericRecyclerViewAdapter<Device,OnDeviceClickListenerManagement<Device>, DeviceViewHolderManagement>  {
    private List<String> waiting;
    Context context;
    public DevicesManagementRecyclerViewAdapter(Context context, List<Device> devices, List<String> waiting) {
        super(context);
        this.items = devices;
        this.waiting = waiting;
        this.context=context;
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

    @Override
    public DeviceViewHolderManagement onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceViewHolderManagement(inflate(R.layout.item_device_management, parent), waiting, context);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolderManagement holder, int position) {
        holder.onBind(items.get(position), getListener());
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolderManagement holder, int position, @NonNull List<Object> payloads) {
        holder.onBind(items.get(position), getListener());
    }
}
