package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;

public class DrawerSortByGroupViewHolder extends BasicViewHolder<Group, OnGroupClicked<Group>> {

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
        number.setText(String.valueOf(item.getBaseDevices().size()));
        if (RayanApplication.getPref().getCurrentShowingGroup() == null && item.getId() == null)
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_selected_item_base_color));
        else if (RayanApplication.getPref().getCurrentShowingGroup()!= null && RayanApplication.getPref().getCurrentShowingGroup().equals(item.getId()))
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_selected_item_base_color));
        else
            itemView.setBackground(null);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGroupClicked(item);
            }
        });
    }

}
