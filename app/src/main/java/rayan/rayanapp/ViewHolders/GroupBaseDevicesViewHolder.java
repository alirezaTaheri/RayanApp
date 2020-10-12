package rayan.rayanapp.ViewHolders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.OnDeviceClickListener;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class GroupBaseDevicesViewHolder extends BaseViewHolder {
    private final String TAG = GroupBaseDevicesViewHolder.class.getSimpleName();

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.port1)
    ImageView pin1;
    @BindView(R.id.port2)
    ImageView pin2;
    @BindView(R.id.type)
    TextView type;
    private Context context;
    public GroupBaseDevicesViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.context=context;
    }

    @Override
    public void onBind(BaseDevice item, OnDeviceClickListener<BaseDevice> listener) {
        if (item.getDeviceType().equals(AppConstants.DEVICE_TYPE_SWITCH_1)){
            type.setText("کلید یک‌پل");
            Device device = (Device) item;
            name.setText(device.getName1());
            pin1.setImageDrawable(device.getPin1().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_lamp_on) : context.getResources().getDrawable(R.drawable.ic_lamp_off));
            pin2.setVisibility(View.INVISIBLE);
        }else if (item.getDeviceType().equals(AppConstants.DEVICE_TYPE_SWITCH_2)) {
            type.setText("کلید دوپل");
            Device device = (Device) item;
            name.setText(device.getName1());
            pin1.setImageDrawable(device.getPin1().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_lamp_on) : context.getResources().getDrawable(R.drawable.ic_lamp_off));
            pin2.setImageDrawable(device.getPin2().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_lamp_on) : context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getDeviceType().equals(AppConstants.DEVICE_TYPE_TOUCH_2)) {
            type.setText("کلید دوپل لمسی");
            Device device = (Device) item;
            name.setText(device.getName1());
            pin1.setImageDrawable(device.getPin1().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_lamp_on) : context.getResources().getDrawable(R.drawable.ic_lamp_off));
            pin2.setImageDrawable(device.getPin2().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_lamp_on) : context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getDeviceType().equals(AppConstants.DEVICE_TYPE_PLUG)) {
            type.setText("پریز");
            Device device = (Device) item;
            name.setText(device.getName1());
            pin1.setImageDrawable(device.getPin1().equals(AppConstants.ON_STATUS)?context.getResources().getDrawable(R.drawable.ic_plug_on) : context.getResources().getDrawable(R.drawable.ic_plug_off));
            pin2.setVisibility(View.INVISIBLE);
        }else if (item.getDeviceType().equals(AppConstants.DEVICE_TYPE_RemoteHub)) {
            type.setText("ریموت هاب");
            RemoteHub remoteHub = (RemoteHub) item;
            name.setText(remoteHub.getName());
            pin1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_plug_on));
            pin2.setVisibility(View.INVISIBLE);
        }else if (item.getDeviceType().equals(AppConstants.DEVICE_TYPE_Remote)) {
            Remote remote = (Remote) item;
            type.setText(remote.getType().equals(AppConstants.REMOTE_TYPE_AC)?"ریموت کولرگازی":"ریموت تلویزیون");
            name.setText(remote.getName());
            pin1.setImageDrawable(remote.getType().equals(AppConstants.REMOTE_TYPE_AC)?context.getResources().getDrawable(R.drawable.ic_air_conditioner) : context.getResources().getDrawable(R.drawable.ic_tv));
            pin2.setVisibility(View.INVISIBLE);
        }else throw new RuntimeException("No Type Matched... Something is not right");

    }
}
