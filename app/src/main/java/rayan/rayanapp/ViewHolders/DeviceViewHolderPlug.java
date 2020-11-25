package rayan.rayanapp.ViewHolders;

import android.animation.ValueAnimator;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class DeviceViewHolderPlug extends DeviceViewHolder1Bridge {
    private final String TAG = DeviceViewHolderPlug.class.getSimpleName();
    private Map<String, ValueAnimator> animators = new HashMap<>();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    ImageView pin1;
    @BindView(R.id.bottomStrip)
    View bottomStrip;
    @BindView(R.id.clickableLayout)
    LinearLayout clickableLayout;
    public DeviceViewHolderPlug(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(Device item, @Nullable OnToggleDeviceListener<Device> listener) {
        AppConstants.disableEnableControls(true, (ViewGroup) clickableLayout);
        Log.e("UNIQUETAG", "OnBindViewHolder: Type3" +" "+ item.getName1() +"\n"+ itemView.getId());
        Log.e(TAG, "Processing this Device: " + item);
        bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.baseColor2));
        name.setText(item.getName1());
        if (item.getPin1().equals(AppConstants.ON_STATUS)){
            pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_plug_on_1));
//            pin1.setChecked(true);
            itemView.post(new Runnable() {
                @Override
                public void run(){
                    bottomStrip.getLayoutParams().width = itemView.getWidth();
                    bottomStrip.requestLayout();
                }
            });
        }
        else {
            pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_plug_off_1));
//            pin1.setChecked(false);
            bottomStrip.getLayoutParams().width = 0;
            bottomStrip.requestLayout();
        }

        if (listener != null){
            clickableLayout.setOnClickListener(vv -> listener.onPin1Clicked(item, this.getAdapterPosition()));
        }
    }

    public void startToggleAnimationPin1(ValueAnimator v){
        Log.e("<<<<<<<<<<<<<<<<","<Starting bridge1");
        AppConstants.disableEnableControls(false, (ViewGroup) itemView);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bottomStrip.getLayoutParams().width = (int) animation.getAnimatedValue();
                bottomStrip.requestLayout();
            }
        });
        v.setDuration(AppConstants.TOGGLE_ANIMATION_TIMEOUT);
        v.start();
    }

    public void stopToggleAnimationPin1(ValueAnimator v, OnToggleDeviceListener<Device> listener, Device item){
        Log.e("<<<<<<<<<<<<<<<<","<Stopping bridge1" + item);
        AppConstants.disableEnableControls(true, (ViewGroup) itemView);
        if (v != null) {
            v.cancel();
            if (item.getPin1().equals(AppConstants.ON_STATUS)) {
                v.setIntValues((int) v.getAnimatedValue(),
                        ((int) v.getAnimatedValue() + (itemView.getWidth() - (int) v.getAnimatedValue()) / 3),
                        ((int) v.getAnimatedValue() + (itemView.getWidth() - (int) v.getAnimatedValue()) / 3 * 2),
                        itemView.getWidth());
            } else {
                v.setIntValues((int) v.getAnimatedValue(),
                        ((int) v.getAnimatedValue() - ((int) v.getAnimatedValue()) / 3),
                        ((int) v.getAnimatedValue() - ((int) v.getAnimatedValue()) / 3 * 2),
                        0);
            }
        }
        else {
            if (item.getPin1().equals(AppConstants.ON_STATUS))
                v = ValueAnimator.ofInt(0,getDeviceItemWidth());
            else
                v = ValueAnimator.ofInt(getDeviceItemWidth(), 0);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    bottomStrip.getLayoutParams().width = (int) animation.getAnimatedValue();
                    bottomStrip.requestLayout();
                }
            });
        }
            v.setDuration(300);
            v.start();
        pin1.setImageDrawable(item.getPin1().equals(AppConstants.ON_STATUS)? ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_plug_on_1):ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_plug_off_1));
//            pin1.setChecked(item.getPin1().equals(AppConstants.ON_STATUS));
        if (listener != null){
            clickableLayout.setOnClickListener(vv -> listener.onPin1Clicked(item, this.getAdapterPosition()));
        }
    }

    public void changeName(String name){
        this.name.setText(name);
    }

    public int getDeviceItemWidth(){
        return itemView.getWidth();
    }

    public void changePosition(Device item, OnToggleDeviceListener<Device> listener){
        if (listener != null){
            clickableLayout.setOnClickListener(vv -> listener.onPin1Clicked(item, this.getAdapterPosition()));
        }
    }
    public void accessPointChanged(Device device, OnToggleDeviceListener<Device> l){
        l.onAccessPointChanged(device);
    }

}
