package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.OnDeviceClickListener;
import rayan.rayanapp.Listeners.OnRemoteClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class RemoteHubViewHolder extends DeviceViewHolder1Bridge {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    ImageView icon;
    @BindView(R.id.clickableLayout)
    LinearLayout clickableLayout;
    private final String TAG = "RemoteViewHolderAddNewRemote";
    public RemoteHubViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBind(BaseDevice baseDevice, @Nullable OnDeviceClickListener<BaseDevice> listener) {
        RemoteHub remoteHub = (RemoteHub) baseDevice;
        name.setText(remoteHub.getName());
        clickableLayout.setOnClickListener(v -> {
            listener.onClick_RemoteHub(baseDevice, getAdapterPosition());
        });
    }

    @Override
    public void changePosition(BaseDevice item, OnDeviceClickListener<BaseDevice> listener) {
        Log.d(TAG, "changePosition() called with: item = [" + item + "], listener = [" + listener + "]");
        if (listener != null){
            clickableLayout.setOnClickListener(vv -> listener.onClick_RemoteHub(item, item.getPosition()));
        }
    }

}
