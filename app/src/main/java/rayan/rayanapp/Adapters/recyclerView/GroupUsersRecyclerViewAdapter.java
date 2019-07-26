package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.view.ViewGroup;

import java.util.ArrayList;

import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.ViewHolders.AdminViewHolder;
import rayan.rayanapp.ViewHolders.GroupUserViewHolder;

public class GroupUsersRecyclerViewAdapter extends GenericRecyclerViewAdapter<User,OnAdminClicked<User>, GroupUserViewHolder>  {

    ArrayList<String> adminsPhoneNumber;
    String parentFragment;
    public GroupUsersRecyclerViewAdapter(Context context, ArrayList<String> adminsPhoneNumber , String parentFragment) {
        super(context);
        this.adminsPhoneNumber=adminsPhoneNumber;
        this.parentFragment=parentFragment;

    }

    @Override
    public GroupUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupUserViewHolder(inflate(R.layout.item_group_user, parent),adminsPhoneNumber, parentFragment);
    }
}
