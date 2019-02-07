package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Listeners.OnContactClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.User;

public class ContactViewHolder  extends BaseViewHolder<User, OnContactClicked<User>> {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.checkbox)
    AppCompatCheckBox checkBox;
    public ContactViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBind(User item, @Nullable OnContactClicked<User> listener) {
        name.setText(item.getUsername());
        phone.setText(item.getUsername());
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
