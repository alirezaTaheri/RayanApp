package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.varunest.sparkbutton.SparkButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnStatusIconClickListener;
import rayan.rayanapp.R;

public class DeviceViewHolder1Bridge extends BaseViewHolder<Device, OnStatusIconClickListener<Device>> {
    private final String TAG = DeviceViewHolder1Bridge.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    SparkButton pin1;

    public DeviceViewHolder1Bridge(View itemView) {
        super(itemView);

        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(Device item, @Nullable OnStatusIconClickListener<Device> listener) {
        Log.e(TAG, "Processing this Device: " + item);
        name.setText(item.getName1());
        if (item.getPin1().equals("on")){
            pin1.setChecked(true);
        }
        else {
            pin1.setChecked(false);
        }
        if (listener != null)
        pin1.setOnClickListener(v -> listener.onPin1Clicked(item));

    }


}
