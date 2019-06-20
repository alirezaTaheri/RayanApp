package rayan.rayanapp.ViewHolders;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varunest.sparkbutton.SparkButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class DeviceViewHolder2Bridges extends DeviceViewHolder1Bridge {
    private final String TAG = DeviceViewHolder2Bridges.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    ImageView pin1;
    @BindView(R.id.pin2)
    ImageView pin2;
    @BindView(R.id.bottomStrip2)
    View bottomStrip2;
    @BindView(R.id.clickableLayout2)
    LinearLayout clickableLayout2;
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
        if (item.getPin1().equals(AppConstants.ON_STATUS)){
            pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_on));
//            pin1.setChecked(true);
            itemView.post(new Runnable() {
                @Override
                public void run(){
                    Log.e("///////////////", "cellWidth: " + itemView.getWidth());
                    bottomStrip.getLayoutParams().width = itemView.getWidth()/2;
                    bottomStrip.requestLayout();
                }
            });
        }
        else {
            bottomStrip.getLayoutParams().width = 0;
            bottomStrip.requestLayout();
            pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_off));
//            pin1.setChecked(false);
        }
        if (item.getPin2().equals(AppConstants.ON_STATUS)){
            pin2.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_on));
            itemView.post(new Runnable() {
                @Override
                public void run(){
                    Log.e("///////////////", "cellWidth: " + itemView.getWidth());
                    bottomStrip2.getLayoutParams().width = itemView.getWidth()/2;
                    bottomStrip2.requestLayout();
                }
            });
        }
        else {
            bottomStrip2.getLayoutParams().width = 0;
            bottomStrip2.requestLayout();
            pin2.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_off));
        }
        if (listener != null){
            clickableLayout.setOnClickListener(v -> listener.onPin1Clicked(item, getAdapterPosition()));
            clickableLayout2.setOnClickListener(v -> listener.onPin2Clicked(item, getAdapterPosition()));
            name.setOnClickListener(null);
        }
    }


    public void startToggleAnimationPin2(ValueAnimator v){
        AppConstants.disableEnableControls(false, (ViewGroup) clickableLayout2);
        Log.e("<<<<<<<<<<<<<<<<","<Starting bridge2 ");
        pin2.setEnabled(false);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bottomStrip2.getLayoutParams().width = (int) animation.getAnimatedValue();
                bottomStrip2.requestLayout();
            }
        });
        v.setDuration(AppConstants.TOGGLE_ANIMATION_TIMEOUT);
        v.start();
    }

    public void stopToggleAnimationPin2(ValueAnimator v, OnToggleDeviceListener<Device> listener, Device item){
        AppConstants.disableEnableControls(true, (ViewGroup) clickableLayout2);
        Log.e("<<<<<<<<<<<<<<<<","<Stopping bridge2" + item);
        pin2.setEnabled(true);
        if (v != null) {
            v.cancel();
            if (item.getPin2().equals(AppConstants.ON_STATUS)) {
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
            if (item.getPin2().equals(AppConstants.ON_STATUS))
                v = ValueAnimator.ofInt(0,getDeviceItemWidth());
            else
                v = ValueAnimator.ofInt(getDeviceItemWidth(), 0);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    bottomStrip2.getLayoutParams().width = (int) animation.getAnimatedValue();
                    bottomStrip2.requestLayout();
                }
            });
        }
        v.setDuration(300);
        v.start();
//        pin1.setChecked(item.getPin1().equals(AppConstants.ON_STATUS));
        pin1.setImageDrawable(item.getPin1().equals(AppConstants.ON_STATUS)?ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_on):ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_off));
        pin2.setImageDrawable(item.getPin2().equals(AppConstants.ON_STATUS)?ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_on):ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_off));
//        pin2.setChecked();
        if (listener != null){
            clickableLayout.setOnClickListener(vv -> listener.onPin1Clicked(item, this.getAdapterPosition()));
            clickableLayout2.setOnClickListener(vv -> listener.onPin2Clicked(item, this.getAdapterPosition()));
            name.setOnClickListener(null);
        }
    }

    public void accessPointChanged(Device device, OnToggleDeviceListener<Device> l){
        l.onAccessPointChanged(device);
    }
    public void changePosition(Device item, OnToggleDeviceListener<Device> listener){
        if (listener != null){
            clickableLayout.setOnClickListener(vv -> listener.onPin1Clicked(item, this.getAdapterPosition()));
            clickableLayout2.setOnClickListener(vv -> listener.onPin2Clicked(item, this.getAdapterPosition()));
        }
    }
    @SuppressLint("CheckResult")
    public void pin2Toggled(boolean on){
        if (on){
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    playSoundSwitchOn();
                }
            }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {

                }
            });
        }
        else {
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    playSoundSwitchOff();
                }
            }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {

                }
            });
        }

    }
}