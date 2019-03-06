package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.varunest.sparkbutton.SparkButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.R;

public class DeviceViewHolder2Bridges extends DeviceViewHolder1Bridge {
    private final String TAG = DeviceViewHolder2Bridges.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    SparkButton pin1;
    @BindView(R.id.pin2)
    SparkButton pin2;

    public DeviceViewHolder2Bridges(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(Device item, @Nullable OnToggleDeviceListener<Device> listener) {
        Log.e(TAG, "Processing this Device: " + item);
        name.setText(item.getName1());
//        if (!item.isLocallyAccessibility() || item.getIp() == null)
//            bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.red_acc_4));
//        else
            bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.baseColor2));
        if (item.getPin1().equals("on")){
            pin1.setChecked(true);
        }
        else {
            pin1.setChecked(false);
        }
        if (item.getPin2().equals("on")){
            pin2.setChecked(true);
        }
        else {
            pin2.setChecked(false);
        }
        if (listener != null){
            pin1.setOnClickListener(v -> listener.onPin1Clicked(item, getAdapterPosition()));
            pin2.setOnClickListener(v -> listener.onPin2Clicked(item, getAdapterPosition()));
        }


    }
}
