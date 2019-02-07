package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.view.ViewGroup;

import rayan.rayanapp.Listeners.OnUserClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.User;
import rayan.rayanapp.ViewHolders.UserViewHolder;

public class UsersRecyclerViewAdapter extends GenericRecyclerViewAdapter<User,OnUserClicked<User>, UserViewHolder>  {

    public UsersRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(inflate(R.layout.item_user, parent));
    }
}
