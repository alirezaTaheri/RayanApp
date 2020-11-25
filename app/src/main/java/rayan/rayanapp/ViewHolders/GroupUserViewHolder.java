package rayan.rayanapp.ViewHolders;

import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;

public class GroupUserViewHolder extends BaseViewHolder<User, OnAdminClicked<User>> {
    private final String TAG = GroupUserViewHolder.class.getSimpleName();

    @BindView(R.id.contactImage)
    CircleImageView contactImage;
    @BindView(R.id.contactName)
    TextView contactName;
    @BindView(R.id.deleteIcon)
    ImageView deleteIcon;
    @BindView(R.id.itemLine)
    View itemLine;
    String parentFragment;
    ArrayList<String> adminsPhoneNumber;
//    String lastItemUserName;
    public GroupUserViewHolder(View itemView, ArrayList<String> adminsPhoneNumber, String parentFragment ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.adminsPhoneNumber=adminsPhoneNumber;
        this.parentFragment=parentFragment;
//        this.lastItemUserName=lastItemUserName;
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
       deleteIcon.setOnClickListener(v->listener.onRemoveAdminClicked(item));
        if (item.getUsername().equals(RayanApplication.getPref().getUsername())){
            if (RayanApplication.getPref().getNameKey()!=null){
                contactName.setText(RayanApplication.getPref().getNameKey());
            }
        }

    }
}
