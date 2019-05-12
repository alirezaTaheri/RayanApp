package rayan.rayanapp.ViewHolders;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
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

import static rayan.rayanapp.App.RayanApplication.getContext;

public class AdminViewHolder extends BaseViewHolder<User, OnAdminClicked<User>> {
    private final String TAG = AdminViewHolder.class.getSimpleName();

    @BindView(R.id.contactImage)
    ImageView contactImage;
    @BindView(R.id.contactName)
    TextView contactName;
    @BindView(R.id.adminTxt)
    TextView adminTxt;
    @BindView(R.id.itemLine)
    View itemLine;
    String parentFragment;
    ArrayList<String> adminsPhoneNumber;
    public AdminViewHolder(View itemView, ArrayList<String> adminsPhoneNumber, String parentFragment ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.adminsPhoneNumber=adminsPhoneNumber;
        this.parentFragment=parentFragment;
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
            adminTxt.setText("مدیر");
        }

        if (parentFragment.equals("admins_users")){
            adminTxt.setVisibility(View.VISIBLE);
            adminTxt.setText("");
            Drawable drawableTop = AppCompatResources.getDrawable(getContext(), R.drawable.ic_more);
            adminTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableTop, null);
            adminTxt.setOnClickListener(v -> listener.onRemoveAdminClicked(item));
        }
    }
}
