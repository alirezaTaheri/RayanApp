package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;

public class GroupDevicesViewHolder extends BaseViewHolder<Device, OnDeviceClickListenerManagement<Device>> {
    private final String TAG = GroupDevicesViewHolder.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;

    public GroupDevicesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(Device item, @Nullable OnDeviceClickListenerManagement<Device> listener) {
        name.setText(item.getName1());
    }
}
