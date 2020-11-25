package rayan.rayanapp.ViewHolders;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class GroupDevicesViewHolder extends BaseViewHolder<Device, OnDeviceClickListenerManagement<Device>> {
    private final String TAG = GroupDevicesViewHolder.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    ImageView pin1;
    @BindView(R.id.pin2)
    ImageView pin2;
    Context context;
    public GroupDevicesViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.context=context;
    }

    @Override
    public void onBind(Device item, @Nullable OnDeviceClickListenerManagement<Device> listener) {
        name.setText(item.getName1());
        if (item.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1)){
            pin1.setImageDrawable(item.getPin1().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_lamp_on) : context.getResources().getDrawable(R.drawable.ic_lamp_off));
            pin2.setVisibility(View.INVISIBLE);
        }else if (item.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2)) {
            pin1.setImageDrawable(item.getPin1().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_lamp_on) : context.getResources().getDrawable(R.drawable.ic_lamp_off));
            pin2.setImageDrawable(item.getPin2().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_lamp_on) : context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals(AppConstants.DEVICE_TYPE_TOUCH_2)) {
            pin1.setImageDrawable(item.getPin1().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_lamp_on) : context.getResources().getDrawable(R.drawable.ic_lamp_off));
            pin2.setImageDrawable(item.getPin2().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_lamp_on) : context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals(AppConstants.DEVICE_TYPE_PLUG)) {
            pin1.setImageDrawable(item.getPin1().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_plug_on) : context.getResources().getDrawable(R.drawable.ic_plug_off));
            pin2.setVisibility(View.INVISIBLE);
        }else { pin1.setImageDrawable(item.getPin1().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_device) : context.getResources().getDrawable(R.drawable.ic_device)); }
        }
}
