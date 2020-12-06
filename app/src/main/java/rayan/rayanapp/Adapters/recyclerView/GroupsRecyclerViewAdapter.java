package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.Util.diffUtil.GroupsDiffCallBack;
import rayan.rayanapp.ViewHolders.GroupViewHolder;

public class GroupsRecyclerViewAdapter extends GenericRecyclerViewAdapter<Group,OnGroupClicked<Group>, GroupViewHolder>  {

    String groupId;
    public GroupsRecyclerViewAdapter(Context context, List<Group> groups) {
        super(context);
        this.items = groups;
    }

    public void updateItems(List<Group> items){
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
//        Log.e("OldGroups:" , "oldGroups: "  + this.items);
//        Log.e("NewGroups:" , "newGroups: "  + items);
        GroupsDiffCallBack devicesDiffCallBack = new GroupsDiffCallBack(items, this.items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(devicesDiffCallBack);
        this.items.clear();
        this.items.addAll(items);
        this.groupId=groupId;
        diffResult.dispatchUpdatesTo(this);
        notifyDataSetChanged();
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupViewHolder(inflate(R.layout.item_group, parent));
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        holder.bind(items.get(position), getListener(), position == items.size()-1);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull GroupViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.bind(items.get(holder.getAdapterPosition()),getListener(),holder.getAdapterPosition() == items.size());
    }
}
