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

import java.util.ArrayList;
import java.util.List;

import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.diffUtil.BaseDeviceDiffCallBack;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;
import rayan.rayanapp.ViewHolders.BaseViewHolderManagement;
import rayan.rayanapp.ViewHolders.DeviceViewHolderManagement;
import rayan.rayanapp.ViewHolders.RemoteHubViewHolderManagement;
import rayan.rayanapp.ViewHolders.RemoteViewHolderManagement;

public class DevicesManagementRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolderManagement> {
    private List<String> waiting;
    Context context;
    private final String TAG = "DevicesManagementRecyclerViewAdapter";
    private OnDeviceClickListenerManagement listener;
    private List<BaseDevice> items = new ArrayList<>();
    public DevicesManagementRecyclerViewAdapter(Context context, List<String> waiting, OnDeviceClickListenerManagement listener) {
        this.waiting = waiting;
        this.context=context;
        this.listener = listener;
        layoutInflater = LayoutInflater.from(context);
    }

    public void updateItems(List<BaseDevice> items){
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
        Log.e(TAG ,items.size()+"Showing:" + items.subList(0, items.size()/3));
        Log.e(TAG ,"Showing:" + items.subList(items.size()/3,items.size()/3*2));
        Log.e(TAG ,"Showing:" + items.subList(items.size()/3*2, items.size()));
        BaseDeviceDiffCallBack devicesDiffCallBack = new BaseDeviceDiffCallBack(items, this.items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(devicesDiffCallBack,false);
        diffResult.dispatchUpdatesTo(this);
        this.items.clear();
        this.items.addAll(items);
    }

    public void setItems(List<BaseDevice> items){
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolderManagement onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("onCreateViewHolder", "lksdjflkfdlkjd"+viewType);
        if (viewType == 1 || viewType == 2 || viewType == 3 || viewType == 4)
            return new DeviceViewHolderManagement(inflate(R.layout.item_device_management, parent), waiting, context);
        else if (viewType == 5)
            return new RemoteHubViewHolderManagement(inflate(R.layout.item_device_management, parent), waiting, context);
        else if (viewType == 6)
            return new RemoteViewHolderManagement(inflate(R.layout.item_device_management, parent), waiting, context);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolderManagement holder, int position) {
        holder.onBind(items.get(position), listener);
    }

    @Override
    public int getItemViewType(int position) {
        Log.e("getItemViewType", "lksdjflkfdlkjd"+position);
        if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_SWITCH_1))
            return 1; if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_SWITCH_2))
            return 2; if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_TOUCH_2))
            return 3; if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_PLUG))
            return 4; if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_REMOTE_HUB))
            return 5; if (items.get(position).getDeviceType().equals(AppConstants.BaseDeviceType_REMOTE))
            return 6; else return -1;
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
}
