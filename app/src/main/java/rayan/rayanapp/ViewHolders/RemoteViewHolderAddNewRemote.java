package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Listeners.OnRemoteClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class RemoteViewHolderAddNewRemote extends BasicViewHolder<Remote, OnRemoteClicked<Remote>> {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.icon)
    ImageView icon;
    private final String TAG = "RemoteViewHolderAddNewRemote";
    public RemoteViewHolderAddNewRemote(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Remote item, OnRemoteClicked<Remote> listener, boolean hideDivider){
        name.setText(item.getName());
        itemView.setOnClickListener(v -> listener.onRemoteClicked(item));
        icon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),item.getType().equals(AppConstants.REMOTE_TYPE_TV)?R.drawable.ic_tv:R.drawable.ic_air_conditioner));
    }

    @Override
    public void onBind(Remote item, @Nullable OnRemoteClicked<Remote> listener) {
    }

}
