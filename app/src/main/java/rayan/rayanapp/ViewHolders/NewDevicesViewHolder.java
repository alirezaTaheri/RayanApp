package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;

public class NewDevicesViewHolder extends BaseViewHolder<AccessPoint, OnNewDeviceClicked<AccessPoint>> {
    private final String TAG = NewDevicesViewHolder.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.connect)
    TextView testDevice;

    public NewDevicesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(AccessPoint item, @Nullable OnNewDeviceClicked<AccessPoint> listener) {
        if (((AddNewDeviceActivity)itemView.getContext()).selectedNewDevice != null &&
                ((AddNewDeviceActivity)itemView.getContext()).selectedNewDevice.getSSID().equals(item.getSSID()))
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_selected_item_base_color));
        else
            itemView.setBackground(null);
        name.setText(item.getSSID());
        name.setOnClickListener(v -> listener.onItemClicked(item));
        testDevice.setOnClickListener(v -> listener.onTestDeviceClicked(item));
    }

}
