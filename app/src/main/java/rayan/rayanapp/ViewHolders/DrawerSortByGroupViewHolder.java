package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;

public class DrawerSortByGroupViewHolder extends BaseViewHolder<Group, OnGroupClicked<Group>> {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.number)
    TextView number;

    public DrawerSortByGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBind(Group item, @Nullable OnGroupClicked<Group> listener) {
        name.setText(item.getName());
        number.setText(String.valueOf(item.getDevices().size()));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGroupClicked(item);
            }
        });
    }
}
