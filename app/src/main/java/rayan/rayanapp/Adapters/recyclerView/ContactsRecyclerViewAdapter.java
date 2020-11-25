package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.view.ViewGroup;

import rayan.rayanapp.Listeners.OnContactClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;
import rayan.rayanapp.ViewHolders.ContactViewHolder;

public class ContactsRecyclerViewAdapter extends GenericRecyclerViewAdapter<User,OnContactClicked<User>, ContactViewHolder> {

    public ContactsRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(inflate(R.layout.item_contact, parent));
    }
}

