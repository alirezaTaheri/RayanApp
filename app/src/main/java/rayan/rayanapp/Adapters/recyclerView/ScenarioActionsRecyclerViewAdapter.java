package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.ScenarioOnActionClickedListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;
import rayan.rayanapp.ViewHolders.ActionViewHolder1Bridge;
import rayan.rayanapp.ViewHolders.ActionViewHolder2Bridges;
import rayan.rayanapp.ViewHolders.ActionViewHolderPlug;

public class ScenarioActionsRecyclerViewAdapter extends GenericRecyclerViewAdapter<Device,ScenarioOnActionClickedListener<Device>, ActionViewHolder1Bridge> {


    public ScenarioActionsRecyclerViewAdapter(Context context, ScenarioOnActionClickedListener listener) {
        super(context);
        setListener(listener);
    }


    public void updateItems(List<Device> items){
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
        DevicesDiffCallBack devicesDiffCallBack = new DevicesDiffCallBack(items, this.items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(devicesDiffCallBack,false);
        diffResult.dispatchUpdatesTo(this);
            this.items.clear();
            this.items.addAll(items);
    }

    @NonNull
    @Override
    public ActionViewHolder1Bridge onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new ActionViewHolder1Bridge(inflate(R.layout.item_action_scenario_1_bridge, parent));
        else if (viewType == 2)
            return new ActionViewHolder2Bridges(inflate(R.layout.item_action_scenario_2_bridge, parent));
        else
            return new ActionViewHolderPlug(inflate(R.layout.item_action_scenario_plug, parent));
    }


        @Override
    public void onBindViewHolder(ActionViewHolder1Bridge holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public void onBindViewHolder(@NonNull ActionViewHolder1Bridge holder, int position, @NonNull List<Object> payloads) {
//        if (!payloads.isEmpty()) {
//            Device device = items.get(position);
//            Bundle b = (Bundle) payloads.get(0);
//            for (String key : b.keySet()) {
//                if (key.equals("position")) {
//                }
//            }
//        }else
//            onBindViewHolder(holder, position);
        holder.onBind(items.get(position), getListener());
    }


    @Override
    public int getItemViewType(int position) {
        Device device = items.get(position);
        if (device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1))
            return 1;
        else if (device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2) || device.getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2))
            return 2;
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId().hashCode();
    }
}
