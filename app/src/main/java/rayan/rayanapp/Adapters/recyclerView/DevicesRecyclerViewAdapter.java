package rayan.rayanapp.Adapters.recyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Fragments.DevicesFragment;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewHolders.DeviceViewHolder1Bridge;
import rayan.rayanapp.ViewHolders.DeviceViewHolder2Bridges;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;
import rayan.rayanapp.ViewHolders.DeviceViewHolderPlug;

public class DevicesRecyclerViewAdapter extends GenericRecyclerViewAdapter<Device,OnToggleDeviceListener<Device>, DeviceViewHolder1Bridge> {


    private Map<String, ValueAnimator> animatorMap = new HashMap<>();
    private static Map<String, Boolean> pin1Enabled = new HashMap<>();
    private static Map<String, Boolean> pin2Enabled = new HashMap<>();
    private Fragment fragment;
    public DevicesRecyclerViewAdapter(Context context, List<Device> devices, Fragment fragment) {
        super(context);
        this.items = devices;
        this.fragment = fragment;
    }


    public void updateItems(List<Device> items){
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
//        Log.e("&&&&" , "old: " + this.items);
//        Log.e("&&&&" , "new: " + items);
        DevicesDiffCallBack devicesDiffCallBack = new DevicesDiffCallBack(items, this.items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(devicesDiffCallBack,false);
        diffResult.dispatchUpdatesTo(this);
            this.items.clear();
            this.items.addAll(items);
    }

    @NonNull
    @Override
    public DeviceViewHolder1Bridge onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new DeviceViewHolder1Bridge(inflate(R.layout.item_device_1_bridge, parent));
        else if (viewType == 2)
            return new DeviceViewHolder2Bridges(inflate(R.layout.item_device_2_bridge, parent));
        else
            return new DeviceViewHolderPlug(inflate(R.layout.item_device_plug, parent));
    }


        @Override
    public void onBindViewHolder(DeviceViewHolder1Bridge holder, int position) {
        super.onBindViewHolder(holder, position);
//        if (animatorMap != null)
//        if (items.get(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2) || items.get(position).getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2)){
//            ((DeviceViewHolder2Bridges)holder).
//                    holder.onViewRecycled(animatorMap.get(items.get(position)), pin1Enabled.get(items.get(position)));
//        }else if (items.get(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1) || items.get(position).getType().equals(AppConstants.DEVICE_TYPE_PLUG)){
//            holder
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder1Bridge holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            Bundle b = (Bundle) payloads.get(0);
            for (String key : b.keySet()) {
                if (key.equals("position")){
                    Log.e("<<<<<<<<<<<<<<<<","Adapter<<Change in Position detected: " + items.get(position));
//                    holder.onBind(items.get(position),getListener());
                    holder.changePosition(items.get(position), getListener());
                }
                if (key.equals("pin1")){
                    Log.e("<<<<<<<<<<<<<<<<","Adapter<<Stopping bridge1" + items.get(position));
                    if (items.get(position).getPin1().equals(AppConstants.ON_STATUS))
                        ((DevicesFragment)fragment).turnOnDeviceAnimationPin1(items.get(position).getChipId(), position);
                    else ((DevicesFragment)fragment).turnOffDeviceAnimationPin1(items.get(position).getChipId(), position);
                    holder.stopToggleAnimationPin1(animatorMap.get(items.get(position).getChipId()+"1"),getListener(), items.get(position));
//                    holder.pin1Toggled(items.get(position).getPin1().endsWith(AppConstants.ON_STATUS));
                }
                if (key.equals("pin2")){
                    Log.e("<<<<<<<<<<<<<<<<","Adapter<<Stopping bridge2" + items.get(position));
                    if (items.get(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2) || items.get(position).getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2)){
                        if (items.get(position).getPin1().equals(AppConstants.ON_STATUS))
                            ((DevicesFragment)fragment).turnOnDeviceAnimationPin1(items.get(position).getChipId(), position);
                        else ((DevicesFragment)fragment).turnOffDeviceAnimationPin1(items.get(position).getChipId(), position);
                        holder.stopToggleAnimationPin1(animatorMap.get(items.get(position).getChipId()+"1"),getListener(), items.get(position));
//                        ((DeviceViewHolder2Bridges)holder).stopToggleAnimationPin2(animatorMap.get(items.get(position).getChipId()+"2"),getListener(), items.get(position));
//                        ((DeviceViewHolder2Bridges)holder).pin2Toggled(items.get(position).getPin2().endsWith(AppConstants.ON_STATUS));
                    }
                }
                if (key.equals("name")){
                    holder.changeName(b.getString("name"));
                    holder.stopToggleAnimationPin1(animatorMap.get(items.get(position).getChipId()+"1"),getListener(), items.get(position));
                    if (items.get(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2)|| items.get(position).getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2))
                        ((DeviceViewHolder2Bridges)holder).stopToggleAnimationPin2(animatorMap.get(items.get(position).getChipId()+"2"),getListener(), items.get(position));
                }
//                if (key.equals("ssid")){
//                    holder.accessPointChanged(getItem(position), getListener());
//                }
//                if (key.equals("ip")){
//                    holder.accessPointChanged(getItem(position), getListener());
//                }
                if (key.equals("startTogglingPin1")){
                    setPin1Enabled(items.get(position).getChipId(), false);
                    ValueAnimator v;
                    if (b.getString("status").equals(AppConstants.ON_STATUS))
                        v = ValueAnimator.ofInt(getItem(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2) || getItem(position).getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2) ? holder.getDeviceItemWidth()/2:holder.getDeviceItemWidth(), 0);
                    else
                        v = ValueAnimator.ofInt(0, getItem(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2) || getItem(position).getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2) ? holder.getDeviceItemWidth()/2:holder.getDeviceItemWidth());
                    animatorMap.put(b.getString("chipId")+"1", v);
                    holder.startToggleAnimationPin1(v);
                }
                if (key.equals("startTogglingPin2")){
                    setPin2Enabled(items.get(position).getChipId(), false);
                    ValueAnimator v;
                    if (b.getString("status").equals(AppConstants.ON_STATUS))
                        v = ValueAnimator.ofInt(getItem(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2) || getItem(position).getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2)? holder.getDeviceItemWidth()/2:holder.getDeviceItemWidth(), 0);
                    else
                        v = ValueAnimator.ofInt(0, getItem(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2) || getItem(position).getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2) ? holder.getDeviceItemWidth()/2:holder.getDeviceItemWidth());
                    animatorMap.put(b.getString("chipId")+"2", v);
                    ((DeviceViewHolder2Bridges)holder).startToggleAnimationPin2(v);
                }
                if (key.equals("stopToggleAnimationPin1")){
                    setPin1Enabled(items.get(position).getChipId(), true);
                    holder.stopToggleAnimationPin1(animatorMap.get(b.getString("chipId")+"1"),getListener(), items.get(position));
                }
                if (key.equals("stopToggleAnimationPin2")){
                    setPin2Enabled(items.get(position).getChipId(), true);
                    ((DeviceViewHolder2Bridges)holder).stopToggleAnimationPin2(animatorMap.get(b.getString("chipId")+"2"),getListener(), items.get(position));
                }
            }
        }else {
            onBindViewHolder(holder, position);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1))
            return 1;
        else if (items.get(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2) || items.get(position).getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2))
            return 2;
        return 3;
    }
    public void setPin1Enabled(String chiipId, boolean enabled){
        pin1Enabled.put(chiipId, enabled);
    }
    public void setPin2Enabled(String chiipId, boolean enabled){
        pin1Enabled.put(chiipId, enabled);
    }
}
