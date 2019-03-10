package rayan.rayanapp.Adapters.recyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewHolders.DeviceViewHolder1Bridge;
import rayan.rayanapp.ViewHolders.DeviceViewHolder2Bridges;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;

public class DevicesRecyclerViewAdapter extends GenericRecyclerViewAdapter<Device,OnToggleDeviceListener<Device>, DeviceViewHolder1Bridge> {


    private Map<String, ValueAnimator> animatorMap = new HashMap<>();
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
    public void onBindViewHolder(@NonNull DeviceViewHolder1Bridge holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            Bundle b = (Bundle) payloads.get(0);
            for (String key : b.keySet()) {
                if (key.equals("progressPin1")){
                    holder.setAnimationProgressPin1(b.getInt("progressPin1"));
                }
                if (key.equals("progressPin2")){
                    ((DeviceViewHolder2Bridges)holder).setAnimationProgressPin2(b.getInt("progressPin2"));
                }
                if (key.equals("startToggleOnAnimation")){
                    Log.e("startToggleOnAnimati" , "b.startToggleOnAnimation: " + b.getString("startToggleOnAnimation"));
                    ValueAnimator v;
                    if (b.getString("status").equals(AppConstants.ON_STATUS))
                        v = ValueAnimator.ofInt(b.getInt("progressWidth"), 0);
                    else
                        v = ValueAnimator.ofInt(0, b.getInt("progressWidth"));
                    animatorMap.put(b.getString("chipId"), v);
                    holder.startToggleAnimationPin1(v);
                }
                if (key.equals("stopToggleOnAnimation")){
                    Log.e("stopToggleOnAnimation" , "b.stopToggleOnAnimation: " + b.getString("stopToggleOnAnimation"));
                    holder.stopToggleOnAnimation(animatorMap.get(b.getString("chipId")), b.getString("status"));
//                    animatorMap.remove(b.getString("chipId"));
                }
//            if (key.equals("name"))
//                holder.setName(b.getString("name"));
//            if (key.equals("pin1"))
//                holder.setStatus(b.getString("pin1"));
//            if (key.equals("la"))
//                holder.setAccessibilityAndNullIP(items.get(position).getIp()!=null && b.getBoolean("la"));
            }
        }else super.onBindViewHolder(holder, position);
    }


    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1))
            return 1;
        else if (items.get(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2))
            return 2;
        else return 3;
    }
}
