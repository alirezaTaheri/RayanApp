package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.view.ViewGroup;

import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.Listeners.OnUserClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.ViewHolders.AdminViewHolder;

public class AdminsRecyclerViewAdapter extends GenericRecyclerViewAdapter<User,OnAdminClicked<User>, AdminViewHolder>  {

    public AdminsRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public AdminViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AdminViewHolder(inflate(R.layout.item_user, parent));
    }
}
