package rayan.rayanapp.ViewHolders;

import androidx.annotation.Nullable;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;

public class GroupViewHolder  extends BaseViewHolder<Group, OnGroupClicked<Group>> {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.group_item_layout)
    RelativeLayout groupItemLayout;
    @BindView(R.id.grouplistLinee)
    View grouplistLine;
    private final String TAG = "GroupViewHolder";
    public GroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Group item, OnGroupClicked<Group> listener, boolean hideDivider){
        name.setText(item.getName());
        groupItemLayout.setOnClickListener((v)-> listener.onGroupClicked(item));
//        Log.d(TAG, "bind() called with: item = [" + item + "], listener = [" + listener + "], hideDivider = [" + hideDivider + "]");
//        if (hideDivider){
//            grouplistLine.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    public void onBind(Group item, @Nullable OnGroupClicked<Group> listener) {
    }

}
