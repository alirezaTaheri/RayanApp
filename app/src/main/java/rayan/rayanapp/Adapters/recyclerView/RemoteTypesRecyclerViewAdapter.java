package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Listeners.OnRemoteTypeClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewHolders.RemoteTypeViewHolder;

public class RemoteTypesRecyclerViewAdapter extends GenericRecyclerViewAdapter<String, OnRemoteTypeClicked<String>, RemoteTypeViewHolder>  {

    public RemoteTypesRecyclerViewAdapter(Context context, List<String> remotes) {
        super(context);
        this.items = remotes;
    }


    @Override
    public RemoteTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RemoteTypeViewHolder(inflate(R.layout.item_remote_type, parent));
    }

    @Override
    public void onBindViewHolder(RemoteTypeViewHolder holder, int position) {
        holder.bind(items.get(position), getListener(), position == items.size()-1);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RemoteTypeViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.bind(items.get(holder.getAdapterPosition()),getListener(),holder.getAdapterPosition() == items.size());
    }
}
