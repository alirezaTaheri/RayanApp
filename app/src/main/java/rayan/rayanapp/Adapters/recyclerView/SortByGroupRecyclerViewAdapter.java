package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.Util.diffUtil.GroupsDiffCallBack;
import rayan.rayanapp.ViewHolders.DrawerSortByGroupViewHolder;

public class SortByGroupRecyclerViewAdapter extends GenericRecyclerViewAdapter<Group,OnGroupClicked<Group>, DrawerSortByGroupViewHolder>  {


    public SortByGroupRecyclerViewAdapter(Context context, List<Group> groups) {
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
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public DrawerSortByGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DrawerSortByGroupViewHolder(inflate(R.layout.item_drawer_sort_by_group, parent));
    }
}
