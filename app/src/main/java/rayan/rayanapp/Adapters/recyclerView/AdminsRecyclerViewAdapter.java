package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.view.ViewGroup;

import java.util.ArrayList;

import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.ViewHolders.AdminViewHolder;

public class AdminsRecyclerViewAdapter extends GenericRecyclerViewAdapter<User,OnAdminClicked<User>, AdminViewHolder>  {

    ArrayList<String> adminsPhoneNumber;
    String parentFragment;
    String lastItemUserName;
    public AdminsRecyclerViewAdapter(Context context, ArrayList<String> adminsPhoneNumber, String lastItemUserName ,String parentFragment) {
        super(context);
        this.adminsPhoneNumber=adminsPhoneNumber;
        this.parentFragment=parentFragment;
        this.lastItemUserName=lastItemUserName;

    }

    @Override
    public AdminViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AdminViewHolder(inflate(R.layout.item_user, parent),adminsPhoneNumber, lastItemUserName, parentFragment);
    }
}
