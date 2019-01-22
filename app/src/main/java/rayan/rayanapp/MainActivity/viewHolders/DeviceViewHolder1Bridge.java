package rayan.rayanapp.MainActivity.viewHolders;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.varunest.sparkbutton.SparkButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.ItemTouchHelperViewHolder;
import rayan.rayanapp.MainActivity.OnStatusIconClickListener;
import rayan.rayanapp.MainActivity.adapters.DevicesRecyclerViewAdapter;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.BaseViewHolder;

public class DeviceViewHolder1Bridge extends BaseViewHolder<Device, OnStatusIconClickListener<Device>>
    implements ItemTouchHelperViewHolder {
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
    public void onBind(Device item, @Nullable OnStatusIconClickListener<Device> listener,DevicesRecyclerViewAdapter.OnDragStartListener mDragStartListener ) {
        Log.e(TAG, "Processing this Device: " + item);
        name.setText(item.getName1());
        itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onDragStarted(DeviceViewHolder1Bridge.this);
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
        if (listener != null)
        pin1.setOnClickListener(v -> listener.onPin1Clicked(item));

    }


    @Override
    public void onItemSelected() {
//        itemView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onItemClear() {
//        itemView.setBackgroundColor(0);
    }
}
