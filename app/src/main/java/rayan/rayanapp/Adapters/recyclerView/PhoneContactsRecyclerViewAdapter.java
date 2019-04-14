package rayan.rayanapp.Adapters.recyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.PhoneContact;
import rayan.rayanapp.R;

public class PhoneContactsRecyclerViewAdapter extends RecyclerView.Adapter<PhoneContactsRecyclerViewAdapter.ContactViewHolder> {

    private ArrayList<PhoneContact> phoneContactsList;

    static class ContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemContactCheckBox)
        AppCompatCheckBox itemContactCheckBox;
        @BindView(R.id.itemContactName)
        TextView itemContactName;
        @BindView(R.id.itemContactNumber)
        TextView itemContactNumber;
        @BindView(R.id.ItemContactImage)
        ImageView ItemContactImage;
        ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public PhoneContactsRecyclerViewAdapter(ArrayList<PhoneContact> phoneContacts) {
        this.phoneContactsList = phoneContacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactViewHolder holder, int listPosition) {
        holder.itemContactCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (phoneContactsList.get(holder.getAdapterPosition()).isSelected()) {
                phoneContactsList.get(holder.getAdapterPosition()).setSelected(false);
            } else {
                phoneContactsList.get(holder.getAdapterPosition()).setSelected(true);
            }
        });
        holder.itemContactName.setText(phoneContactsList.get(holder.getAdapterPosition()).getName());
        holder.itemContactNumber.setText(phoneContactsList.get(holder.getAdapterPosition()).getNumbers());
        holder.ItemContactImage.setImageBitmap(phoneContactsList.get(holder.getAdapterPosition()).getImage());
        Log.e("phone contact list22 = ", phoneContactsList.toString());
    }

    @Override
    public int getItemCount() {
        return phoneContactsList.size();
    }

    public void updateListBySearch(ArrayList<PhoneContact> list){
        phoneContactsList = list;
        notifyDataSetChanged();
    }
}