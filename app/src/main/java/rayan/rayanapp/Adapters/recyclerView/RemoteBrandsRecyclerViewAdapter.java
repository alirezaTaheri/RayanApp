package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Listeners.OnRemoteBrandClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewHolders.RemoteBrandViewHolder;

public class RemoteBrandsRecyclerViewAdapter extends GenericRecyclerViewAdapter<String, OnRemoteBrandClicked<String>, RemoteBrandViewHolder>  {

    public RemoteBrandsRecyclerViewAdapter(Context context, List<String> remotes) {
        super(context);
        this.items = remotes;
    }

    public void filter(String text, List<String> allItems) {
        items.clear();
        if(text.isEmpty()){
            items.addAll(allItems);
        } else{
            text = text.toLowerCase();
            for(String item: allItems){
                if(item.toLowerCase().contains(text)){
                    items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public RemoteBrandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RemoteBrandViewHolder(inflate(R.layout.item_remote_brand, parent));
    }

    @Override
    public void onBindViewHolder(RemoteBrandViewHolder holder, int position) {
        holder.bind(items.get(position), getListener(), position == items.size()-1);
    }

}
