package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;

public class AdminViewHolder extends BaseViewHolder<User, OnAdminClicked<User>> {
    private final String TAG = AdminViewHolder.class.getSimpleName();

    @BindView(R.id.contactImage)
    ImageView contactImage;
    @BindView(R.id.contactName)
    TextView contactName;
    @BindView(R.id.adminTxt)
    TextView adminTxt;
    ArrayList<String> adminsPhoneNumber=new ArrayList<>();
    public AdminViewHolder(View itemView, ArrayList<String> adminsPhoneNumber) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.adminsPhoneNumber=adminsPhoneNumber;
    }

    @Override
    public void onBind(User item, @Nullable OnAdminClicked<User> listener) {
        contactImage.setImageBitmap(item.getContactImageOnPhone());
        contactName.setText(item.getUsername());
        if(item.getContactNameOnPhone()!=null){
            if (item.getContactNameOnPhone().length()>1) {
                contactName.setText(item.getContactNameOnPhone());
            }
       }
        if (adminsPhoneNumber.contains(item.getUsername())){
            adminTxt.setVisibility(View.VISIBLE);
        }
//        delete.setOnClickListener(v -> {
//            listener.onRemoveAdminClicked(item);
//        });
    }
}
