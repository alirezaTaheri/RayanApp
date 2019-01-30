package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.varunest.sparkbutton.SparkButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;

public class DeviceViewHolderManagement extends BaseViewHolder<Device, OnDeviceClickListenerManagement<Device>> {
    private final String TAG = DeviceViewHolderManagement.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;

    public DeviceViewHolderManagement(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(Device item, @Nullable OnDeviceClickListenerManagement<Device> listener) {
        name.setText(item.getName1());
        itemView.setOnClickListener(v -> {
            listener.onItemClick(item);
        });
    }
}
