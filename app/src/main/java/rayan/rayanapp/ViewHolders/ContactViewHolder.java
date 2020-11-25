package rayan.rayanapp.ViewHolders;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rayan.rayanapp.Listeners.OnContactClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;

public class ContactViewHolder  extends BaseViewHolder<User, OnContactClicked<User>> {

    @BindView(R.id.contactName)
    TextView name;
    @BindView(R.id.checkbox)
    AppCompatCheckBox checkBox;
    @BindView(R.id.contactImage)
    CircleImageView contactImage;
    public ContactViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBind(User item, @Nullable OnContactClicked<User> listener) {
        name.setText(item.getUsername());
        contactImage.setImageBitmap(item.getContactImageOnPhone());
        if (item.isSelected())
            checkBox.setChecked(true);
        else checkBox.setChecked(false);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                item.setSelected(true);
                listener.onContactClicked(item);
            }
            else{
                item.setSelected(false);
                listener.onContactClicked(item);
            }
        });
    }
}
