package rayan.rayanapp.ViewHolders;

import android.opengl.Visibility;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.Listeners.OnUserClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;

public class AdminViewHolder extends BaseViewHolder<User, OnAdminClicked<User>> {
    private final String TAG = AdminViewHolder.class.getSimpleName();

    @BindView(R.id.contactImage)
    ImageView contactImage;
    @BindView(R.id.contactName)
    TextView contactName;
    @BindView(R.id.deleteUser)
    ImageView delete;
    public AdminViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
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
        if (item.getUsername().equals(RayanApplication.getPref().getUsername())){
            delete.setVisibility(View.INVISIBLE);
        }
        if (RayanApplication.getPref().getIsGroupAdminKey()){
        }else{
            delete.setVisibility(View.INVISIBLE);
        }
        delete.setOnClickListener(v -> {
            listener.onRemoveAdminClicked(item);
        });
    }
}
