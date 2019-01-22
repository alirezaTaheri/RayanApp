package rayan.rayanapp.MainActivity.viewHolders;

import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.varunest.sparkbutton.SparkButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.MainActivity.OnStatusIconClickListener;
import rayan.rayanapp.MainActivity.adapters.DevicesRecyclerViewAdapter;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.BaseViewHolder;

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
    public void onBind(Device item, @Nullable OnStatusIconClickListener<Device> listener, DevicesRecyclerViewAdapter.OnDragStartListener mDragStartListener) {
        Log.e(TAG, "Processing this Device: " + item);
        name.setText(item.getName1());
        itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onDragStarted(DeviceViewHolder2Bridges.this);
                }
                return false;
            }
        });
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
            pin1.setOnClickListener(v -> listener.onPin1Clicked(item));
            pin2.setOnClickListener(v -> listener.onPin2Clicked(item));
        }


    }
}
