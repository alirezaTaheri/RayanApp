package rayan.rayanapp.Adapters.recyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.OnDeviceClickListener;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.diffUtil.BaseDeviceDiffCallBack;
import rayan.rayanapp.ViewHolders.BaseViewHolder;
import rayan.rayanapp.ViewHolders.DeviceViewHolder1Bridge;
import rayan.rayanapp.ViewHolders.DeviceViewHolder2Bridges;
import rayan.rayanapp.ViewHolders.DeviceViewHolderPlug;
import rayan.rayanapp.ViewHolders.RemoteHubViewHolder;
import rayan.rayanapp.ViewHolders.RemoteViewHolder;

public class DevicesFragmentRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final String TAG = "DevicesRecyclerViewAdapter";
    private Map<String, ValueAnimator> animatorMap = new HashMap<>();
    private static Map<String, Boolean> pin1Enabled = new HashMap<>();
    private ToggleDeviceAnimationProgress fragment;
    private OnDeviceClickListener<BaseDevice> listener;
    
    public DevicesFragmentRecyclerViewAdapter(Context context, OnDeviceClickListener<BaseDevice> listener, ToggleDeviceAnimationProgress fragment) {
        layoutInflater = LayoutInflater.from(context);
        this.fragment = fragment;
        this.listener = listener;
    }

    List<BaseDevice> items = new ArrayList<>();
    public void updateItems(List<BaseDevice> items){
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
        Log.e(TAG ,"ShowingDevices: " + items.subList(0, items.size()/3));
        Log.e(TAG ,"ShowingDevices: " + items.subList(items.size()/3,items.size()/3*2));
        Log.e(TAG ,"ShowingDevices: " + items.subList(items.size()/3*2, items.size()));
        BaseDeviceDiffCallBack devicesDiffCallBack = new BaseDeviceDiffCallBack(items, this.items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(devicesDiffCallBack,false);
        diffResult.dispatchUpdatesTo(this);
        this.items.clear();
        this.items.addAll(items);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new DeviceViewHolder1Bridge(inflate(R.layout.item_device_1_bridge, parent));
        else if (viewType == 2)
            return new DeviceViewHolder2Bridges(inflate(R.layout.item_device_2_bridge, parent));
        else if (viewType == 3)
            return new DeviceViewHolderPlug(inflate(R.layout.item_device_2_bridge, parent));
        else if (viewType == 4)
            return new DeviceViewHolderPlug(inflate(R.layout.item_device_plug, parent));
        else if (viewType == 5)
            return new RemoteHubViewHolder(inflate(R.layout.item_device_remote_hub, parent));
        else if (viewType == 6)
            return new RemoteViewHolder(inflate(R.layout.item_device_remote, parent));
        else
            return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_SWITCH_1))
            return 1; if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_SWITCH_2))
            return 2; if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_TOUCH_2))
            return 3; if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_PLUG))
            return 4; if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_REMOTE_HUB))
            return 5; if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_REMOTE))
            return 6; else return -1;
    }

    public void onItemMove(int fromPosition, int toPosition){
        if (fromPosition != -1 && toPosition != -1) {
            items.add(toPosition, items.remove(fromPosition));
            notifyItemMoved(fromPosition, toPosition);
        }else Log.e("GenericRecyclerAdapter", "Index Problem => from: " + fromPosition + " to: " +toPosition);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int i) {
        BaseDevice item = items.get(i);
        viewHolder.onBind(item, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            BaseDevice baseDevice = items.get(position);
                    Bundle b = (Bundle) payloads.get(0);
                    for (String key : b.keySet()) {
                        switch (key){
                            case "position": {
                                if (baseDevice instanceof Device) {
                                    Device device = (Device) baseDevice;
                                    holder.changePosition(device, listener);
                                }else if(baseDevice instanceof RemoteHub) {
                                    RemoteHub remoteHub = (RemoteHub) baseDevice;
                                    holder.changePosition(remoteHub, listener);
                                }else if (baseDevice instanceof Remote){
                                    Remote remote = (Remote) baseDevice;
                                    holder.changePosition(remote, listener);
                                }
                            }
                                break;
                            case "pin1": {
                                Device device = (Device) baseDevice;
                                if (device.getPin1().equals(AppConstants.ON_STATUS))
                                    fragment.turnOnDevicePin1(device.getChipId(), position, device.getType());
                                else
                                    fragment.turnOffDevicePin1(device.getChipId(), position, device.getType());
                                holder.stopToggleAnimationPin1(animatorMap.get(device.getChipId() + "1"), listener, device);
                            }
                                break;
                            case "pin2": {
                                Device device = (Device) baseDevice;
                                if (device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2) || device.getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2)) {
                                    if (device.getPin2().equals(AppConstants.ON_STATUS))
                                        fragment.turnOnDevicePin2(device.getChipId(), position, device.getType());
                                    else
                                        fragment.turnOffDevicePin2(device.getChipId(), position, device.getType());
                                    holder.stopToggleAnimationPin2(animatorMap.get(device.getChipId() + "2"), listener, device);
                                }
                            }
                                break;
                            case "name":
                                holder.changeName(b.getString("name"));
                                if (baseDevice instanceof Device) {
                                    Device device = (Device) baseDevice;
                                    holder.stopToggleAnimationPin1(animatorMap.get(device.getChipId() + "1"), listener, device);
                                }
                                break;
                            case "ip":
                                if (baseDevice instanceof Device) {
                                    Device device = (Device) baseDevice;
                                    holder.ipChanged(listener, device);
                                }else if (baseDevice instanceof RemoteHub) {
                                    RemoteHub remoteHub = (RemoteHub) baseDevice;
                                    holder.ipChanged(listener, remoteHub);
                                }
                                break;
                            case "startTogglingPin1": {
                                Device device = (Device) baseDevice;
                                setPin1Enabled(device.getChipId(), false);
                                ValueAnimator v;
                                if (b.getString("status").equals(AppConstants.ON_STATUS))
                                    v = ValueAnimator.ofInt(holder.getDeviceItemWidth(), 0);
                                else
                                    v = ValueAnimator.ofInt(0, holder.getDeviceItemWidth());
                                animatorMap.put(b.getString("chipId") + "1", v);
                                holder.startToggleAnimationPin1(v);
                            }
                                break;
                            case "startTogglingPin2": {
                                Device device = (Device) baseDevice;
                                setPin2Enabled(device.getChipId(), false);
                                ValueAnimator v;
                                if (b.getString("status").equals(AppConstants.ON_STATUS))
                                    v = ValueAnimator.ofInt(device.getDeviceType().equals(AppConstants.BaseDeviceType_SWITCH_2) || device.getDeviceType().equals(AppConstants.BaseDeviceType_TOUCH_2) ? holder.getDeviceItemWidth() / 2 : holder.getDeviceItemWidth(), 0);
                                else
                                    v = ValueAnimator.ofInt(0, device.getDeviceType().equals(AppConstants.BaseDeviceType_SWITCH_2) || device.getDeviceType().equals(AppConstants.BaseDeviceType_TOUCH_2) ? holder.getDeviceItemWidth() / 2 : holder.getDeviceItemWidth());
                                animatorMap.put(b.getString("chipId") + "2", v);
                                holder.startToggleAnimationPin2(v);
                            }
                                break;
                            case "stopToggleAnimationPin1": {
                                Device device = (Device) baseDevice;
                                setPin1Enabled(device.getChipId(), true);
                                holder.stopToggleAnimationPin1(animatorMap.get(b.getString("chipId") + "1"), listener, device);
                            }
                                break;
                            case "stopToggleAnimationPin2": {
                                Device device = (Device) baseDevice;
                                setPin2Enabled(device.getChipId(), true);
                                holder.stopToggleAnimationPin2(animatorMap.get(b.getString("chipId") + "2"), listener, device);
                            }
                                break;
                        }
                    }

        }else onBindViewHolder(holder,position);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }


    @NonNull
    protected View inflate(@LayoutRes final int layout, @Nullable final ViewGroup parent, final boolean attachToRoot) {
        return layoutInflater.inflate(layout, parent, attachToRoot);
    }
    @NonNull
    protected View inflate(@LayoutRes final int layout, final @Nullable ViewGroup parent) {
        return inflate(layout, parent, false);
    }
    private LayoutInflater layoutInflater;

    public void setPin1Enabled(String chiipId, boolean enabled){
        pin1Enabled.put(chiipId, enabled);
    }
    public void setPin2Enabled(String chiipId, boolean enabled){
        pin1Enabled.put(chiipId, enabled);
    }

}
