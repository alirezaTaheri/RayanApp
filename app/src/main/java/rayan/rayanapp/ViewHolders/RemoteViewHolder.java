package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Listeners.OnDeviceClickListener;
import rayan.rayanapp.Listeners.OnRemoteClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class RemoteViewHolder extends DeviceViewHolder1Bridge {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    ImageView icon;
    private final String TAG = "RemoteViewHolderAddNewRemote";
    public RemoteViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBind(BaseDevice baseDevice, @Nullable OnDeviceClickListener<BaseDevice> listener) {
        Remote item = (Remote) baseDevice;
        Log.d("RemoteViewHolder", "onBind() with: Remote" + item);
        name.setText(item.getName());
        clickableLayout.setOnClickListener(v -> listener.onClick_Remote(item, getAdapterPosition()));
//        icon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),item.getType().equals(AppConstants.REMOTE_TYPE_TV)?R.drawable.ic_tv:R.drawable.ic_air_conditioner));
        icon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_air_conditioner));
    }
}
