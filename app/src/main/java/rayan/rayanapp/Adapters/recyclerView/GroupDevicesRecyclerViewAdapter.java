package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.Listeners.OnStatusIconClickListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;
import rayan.rayanapp.ViewHolders.DeviceViewHolder1Bridge;
import rayan.rayanapp.ViewHolders.DeviceViewHolder2Bridges;
import rayan.rayanapp.ViewHolders.GroupDevicesViewHolder;

public class GroupDevicesRecyclerViewAdapter extends GenericRecyclerViewAdapter<Device,OnDeviceClickListenerManagement<Device>, GroupDevicesViewHolder> {


    public GroupDevicesRecyclerViewAdapter(Context context, List<Device> devices) {
        super(context);
        this.items = devices;
    }


    @NonNull
    @Override
    public GroupDevicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GroupDevicesViewHolder(inflate(R.layout.item_device_management, parent));
    }

}
