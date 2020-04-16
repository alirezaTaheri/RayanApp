package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Listeners.OnRemoteClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.diffUtil.RemotesDiffCallBack;
import rayan.rayanapp.ViewHolders.RemoteViewHolderAddNewRemote;

public class RemotesRecyclerViewAdapter extends GenericRecyclerViewAdapter<Remote,OnRemoteClicked<Remote>, RemoteViewHolderAddNewRemote>  {

    String remoteId;
    public RemotesRecyclerViewAdapter(Context context, List<Remote> remotes) {
        super(context);
        this.items = remotes;
    }

    public void updateItems(List<Remote> items){
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
//        Log.e("OldRemotes:" , "oldRemotes: "  + this.items);
//        Log.e("NewRemotes:" , "newRemotes: "  + items);
        RemotesDiffCallBack devicesDiffCallBack = new RemotesDiffCallBack(items, this.items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(devicesDiffCallBack);
        this.items.clear();
        this.items.addAll(items);
        this.remoteId=remoteId;
        diffResult.dispatchUpdatesTo(this);
        notifyDataSetChanged();
    }

    @Override
    public RemoteViewHolderAddNewRemote onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RemoteViewHolderAddNewRemote(inflate(R.layout.item_remote, parent));
    }

    @Override
    public void onBindViewHolder(RemoteViewHolderAddNewRemote holder, int position) {
        holder.bind(items.get(position), getListener(), position == items.size()-1);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RemoteViewHolderAddNewRemote holder) {
        super.onViewAttachedToWindow(holder);
        holder.bind(items.get(holder.getAdapterPosition()),getListener(),holder.getAdapterPosition() == items.size());
    }
}
