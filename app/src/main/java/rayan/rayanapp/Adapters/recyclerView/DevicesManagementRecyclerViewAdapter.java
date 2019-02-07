package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;
import rayan.rayanapp.ViewHolders.DeviceViewHolderManagement;

public class DevicesManagementRecyclerViewAdapter extends GenericRecyclerViewAdapter<Device,OnDeviceClickListenerManagement<Device>, DeviceViewHolderManagement>  {

    public DevicesManagementRecyclerViewAdapter(Context context, List<Device> devices) {
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

    @Override
    public DeviceViewHolderManagement onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceViewHolderManagement(inflate(R.layout.item_device_management, parent));
    }
}
