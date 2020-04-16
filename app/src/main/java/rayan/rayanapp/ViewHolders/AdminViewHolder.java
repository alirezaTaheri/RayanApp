package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Util.AppConstants;

public class AdminViewHolder extends BasicViewHolder<User, OnAdminClicked<User>> {
    private final String TAG = AdminViewHolder.class.getSimpleName();

    @BindView(R.id.contactImage)
    CircleImageView contactImage;
    @BindView(R.id.contactName)
    TextView contactName;
    @BindView(R.id.adminTxt)
    TextView adminTxt;
    @BindView(R.id.itemLine)
    View itemLine;
    String parentFragment;
    ArrayList<String> adminsPhoneNumber;
//    String lastItemUserName;
    public AdminViewHolder(View itemView, ArrayList<String> adminsPhoneNumber, String parentFragment ) {
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
        if (item.getUserType().equals(AppConstants.ADMIN_TYPE)){
            adminTxt.setVisibility(View.VISIBLE);
        }else
            adminTxt.setVisibility(View.INVISIBLE);

//        if (parentFragment.equals("admins_users")){
//            adminTxt.setVisibility(View.VISIBLE);
//            adminTxt.setText("");
//            Drawable drawableTop = AppCompatResources.getDrawable(getContext(), R.drawable.ic_more);
//            adminTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableTop, null);
//            adminTxt.setOnClickListener(v -> listener.onRemoveAdminClicked(item));
//            if (item.getUsername().equals(RayanApplication.getPref().getUsername())){
//                adminTxt.setVisibility(View.INVISIBLE);
//            }
//        }
        if (item.getUsername().equals(RayanApplication.getPref().getUsername())){
            if (RayanApplication.getPref().getNameKey()!=null){
                contactName.setText(RayanApplication.getPref().getNameKey());
            }
        }
//        if (item.getUsername().equals(lastItemUserName)){
//            itemLine.setVisibility(View.GONE);
//        }
    }
}
