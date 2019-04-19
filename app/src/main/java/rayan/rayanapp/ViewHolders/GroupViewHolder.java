package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;

public class GroupViewHolder  extends BaseViewHolder<Group, OnGroupClicked<Group>> {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.group_item_layout)
    RelativeLayout groupItemLayout;

    public GroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBind(Group item, @Nullable OnGroupClicked<Group> listener) {
        name.setText(item.getName());
        groupItemLayout.setOnClickListener((v)-> listener.onGroupClicked(item));
    }
}
