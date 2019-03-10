package rayan.rayanapp.ViewHolders;

import android.animation.ValueAnimator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.varunest.sparkbutton.SparkButton;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class DeviceViewHolder1Bridge extends BaseViewHolder<Device, OnToggleDeviceListener<Device>> {
    private final String TAG = DeviceViewHolder1Bridge.class.getSimpleName();
    private Map<String, ValueAnimator> animators = new HashMap<>();
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
        Log.e(TAG, "Processing this Device: " + item+"\nThis Class: " + this + this.getClass() +"\n"+ animators);
//        if (!item.isLocallyAccessibility() || item.getIp() == null)
//            bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.red_acc_4));
//        else
            bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.baseColor2));
        if (item.getPin1().equals(AppConstants.ON_STATUS)){
            pin1.setChecked(true);
            itemView.post(new Runnable() {
                @Override
                public void run(){
                    Log.e("///////////////", "cellWidth: " + itemView.getWidth());
                    bottomStrip.getLayoutParams().width = itemView.getWidth();
                    bottomStrip.requestLayout();
                }
            });
        }
        else {
            pin1.setChecked(false);
            bottomStrip.getLayoutParams().width = 0;
            bottomStrip.requestLayout();
        }
        name.setText(item.getName1());
        if (item.getPin1().equals(AppConstants.ON_STATUS)){
            pin1.setChecked(true);
            itemView.post(new Runnable() {
                @Override
                public void run(){
                    Log.e("///////////////", "cellWidth: " + itemView.getWidth());
                    bottomStrip.getLayoutParams().width = itemView.getWidth();
                    bottomStrip.requestLayout();
                }
            });
        }
        else {
            pin1.setChecked(false);
            bottomStrip.getLayoutParams().width = 0;
            bottomStrip.requestLayout();
        }
        if (listener != null)
        pin1.setOnClickListener(v -> listener.onPin1Clicked(item, this.getAdapterPosition()));
    }


    public void setAnimationProgressPin1(int progress){
        Log.e("Toggling:::: " , "Toggling pin1: " + progress);
        bottomStrip.getLayoutParams().width = progress;
        bottomStrip.requestLayout();
    }

    public void startToggleAnimationPin1(ValueAnimator v){
        Log.e("startingAnimation", "startingAnimation" + itemView.getWidth());
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e("onanimationupdate;" ,"update: " + animation.getAnimatedValue());
                bottomStrip.getLayoutParams().width = (int) animation.getAnimatedValue();
                bottomStrip.requestLayout();
            }
        });
        v.setDuration(4000);
        v.start();
    }

    public void stopToggleOnAnimation(ValueAnimator v, String status){
        if (v != null) {
            v.cancel();
            if (status.equals(AppConstants.ON_STATUS)) {
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
            v.setDuration(300);
            v.start();
        }else {
            if (status.equals(AppConstants.ON_STATUS)){
                bottomStrip.getLayoutParams().width = itemView.getWidth();
                bottomStrip.requestLayout();
            }else{
                bottomStrip.getLayoutParams().width = 0;
                bottomStrip.requestLayout();
            }
        }
    }

    public void messageArrivedOrFinishAnimation(Device item){
        if (item.getPin1().equals(AppConstants.ON_STATUS)){
            pin1.setChecked(true);
            itemView.post(new Runnable() {
                @Override
                public void run(){
                    Log.e("///////////////", "cellWidth: " + itemView.getWidth());
                    bottomStrip.getLayoutParams().width = itemView.getWidth();
                    bottomStrip.requestLayout();
                }
            });
        }
        else {
            pin1.setChecked(false);
            bottomStrip.getLayoutParams().width = 0;
            bottomStrip.requestLayout();
        }
    }

}
