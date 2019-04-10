package rayan.rayanapp.Adapters.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import rayan.rayanapp.R;
import rayan.rayanapp.ViewHolders.ExpandableGroupViewHolder;
import rayan.rayanapp.ViewHolders.SingleGroupViewHolder;

public class GroupsExpandableRecyclerViewAdapter extends ExpandableRecyclerViewAdapter<ExpandableGroupViewHolder, SingleGroupViewHolder> {
    public GroupsExpandableRecyclerViewAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public ExpandableGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expandable_group, parent, false);
        return new ExpandableGroupViewHolder(view);
    }

    @Override
    public SingleGroupViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_group, parent, false);
        return new SingleGroupViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(SingleGroupViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

    }

    @Override
    public void onBindGroupViewHolder(ExpandableGroupViewHolder holder, int flatPosition, ExpandableGroup group) {

    }
}
