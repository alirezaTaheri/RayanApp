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
import rayan.rayanapp.Util.AppConstants;

public class DeviceViewHolder1Bridge extends BaseViewHolder<Device, OnToggleDeviceListener<Device>> {
    private final String TAG = DeviceViewHolder1Bridge.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    SparkButton pin1;
    @BindView(R.id.bottomStrip)
    View bottomStrip;
    public DeviceViewHolder1Bridge(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(Device item, @Nullable OnToggleDeviceListener<Device> listener) {
        Log.e(TAG, "Processing this Device: " + item);
//        if (!item.isLocallyAccessibility() || item.getIp() == null)
//            bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.red_acc_4));
//        else
            bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.baseColor2));
        name.setText(item.getName1());
        if (item.getPin1().equals(AppConstants.ON_STATUS)){
            pin1.setChecked(true);
        }
        else {
            pin1.setChecked(false);
        }
        if (listener != null)
        pin1.setOnClickListener(v -> listener.onPin1Clicked(item, this.getAdapterPosition()));
    }


    public void setAnimationProgress(int progress){
        bottomStrip.getLayoutParams().width = progress;
        bottomStrip.requestLayout();
    }

}
