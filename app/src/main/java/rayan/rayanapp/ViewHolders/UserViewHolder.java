package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Listeners.OnUserClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.User;

public class UserViewHolder extends BaseViewHolder<User, OnUserClicked<User>> {
    private final String TAG = UserViewHolder.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.deleteUser)
    ImageView delete;
    public UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(User item, @Nullable OnUserClicked<User> listener) {
        name.setText(item.getUsername());
        delete.setOnClickListener(v -> {
            listener.onRemoveClicked(item);
        });
    }
}
