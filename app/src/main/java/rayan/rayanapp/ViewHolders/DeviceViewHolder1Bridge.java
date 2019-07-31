package rayan.rayanapp.ViewHolders;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.varunest.sparkbutton.SparkButton;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class DeviceViewHolder1Bridge extends BaseViewHolder<Device, OnToggleDeviceListener<Device>> {
    private final String TAG = DeviceViewHolder1Bridge.class.getSimpleName();
    private Map<String, ValueAnimator> animators = new HashMap<>();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    ImageView pin1;
    @BindView(R.id.bottomStrip)
    View bottomStrip;
    @BindView(R.id.clickableLayout)
    LinearLayout clickableLayout;
    MediaPlayer switchOnSound, switchOffSound;
    Uri switchOnPath, switchOffPath;
    public DeviceViewHolder1Bridge(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(Device item, @Nullable OnToggleDeviceListener<Device> listener) {
        AppConstants.disableEnableControls(true, (ViewGroup) clickableLayout);
        Log.e("UNIQUETAG", "OnBindViewHolder: Type1" +" "+ item.getName1() +"\n"+ itemView.getId());
        Log.e(TAG, "Processing this Device: " + item);
        bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.baseColor2));
        name.setText(item.getName1());
        if (item.getPin1().equals(AppConstants.ON_STATUS)){
            pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_on));
//            pin1.setChecked(true);
            itemView.post(() -> {
                bottomStrip.getLayoutParams().width = itemView.getWidth();
                bottomStrip.requestLayout();
            });
        }
        else {
            pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_off));
//            pin1.setChecked(false);
            bottomStrip.getLayoutParams().width = 0;
            bottomStrip.requestLayout();
        }

        if (listener != null){
            clickableLayout.setOnClickListener(vv -> listener.onPin1Clicked(item, this.getAdapterPosition()));
        }
    }

    public void onViewRecycled(ValueAnimator v, boolean enabled){
        AppConstants.disableEnableControls(enabled, (ViewGroup) clickableLayout);

    }
        public void updateBottomStripPin1(int b){
        itemView.post(() -> {
            bottomStrip.getLayoutParams().width = b;
            bottomStrip.requestLayout();
        });
    }

    public int getItemWidth(){
        return itemView.getWidth();
    }

    public void startToggleAnimationPin1(ValueAnimator v){
        Log.e("<<<<<<<<<<<<<<<<","<Starting bridge1");
        AppConstants.disableEnableControls(false, (ViewGroup) clickableLayout);
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
        try {
            Log.e("<<<<<<<<<<<<<<<<", "<Stopping bridge1" + item);
            AppConstants.disableEnableControls(true, clickableLayout);
//            if (v != null) {
//                v.cancel();
//                if (item.getPin1().equals(AppConstants.ON_STATUS)) {
////                    playSoundSwitchOn();
//                    v.setIntValues((int) v.getAnimatedValue(),
//                            ((int) v.getAnimatedValue() + (itemView.getWidth() - (int) v.getAnimatedValue()) / 3),
//                            ((int) v.getAnimatedValue() + (itemView.getWidth() - (int) v.getAnimatedValue()) / 3 * 2),
//                            itemView.getWidth());
//                } else {
////                    playSoundSwitchOff();
//                    v.setIntValues((int) v.getAnimatedValue(),
//                            ((int) v.getAnimatedValue() - ((int) v.getAnimatedValue()) / 3),
//                            ((int) v.getAnimatedValue() - ((int) v.getAnimatedValue()) / 3 * 2),
//                            0);
//                }
//            } else {
//                if (item.getPin1().equals(AppConstants.ON_STATUS)) {
////                    playSoundSwitchOn();
//                    v = ValueAnimator.ofInt(0, getDeviceItemWidth());
//                } else {
////                    playSoundSwitchOff();
//                    v = ValueAnimator.ofInt(getDeviceItemWidth(), 0);
//                }
//                v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        bottomStrip.getLayoutParams().width = (int) animation.getAnimatedValue();
//                        bottomStrip.requestLayout();
//                    }
//                });
//            }
//            v.setDuration(300);
//            v.start();
            pin1.setImageDrawable(item.getPin1().equals(AppConstants.ON_STATUS) ? ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_on) : ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_off));
//            pin1.setChecked(item.getPin1().equals(AppConstants.ON_STATUS));
            if (listener != null) {
                clickableLayout.setOnClickListener(vv -> listener.onPin1Clicked(item, this.getAdapterPosition()));
            }
        }catch (Exception e){
            e.printStackTrace();
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
            clickableLayout.setOnClickListener(vv -> listener.onPin1Clicked(item, item.getPosition()));
        }
    }
    public void accessPointChanged(Device device, OnToggleDeviceListener<Device> l){
        l.onAccessPointChanged(device);
    }

    public void playSoundSwitchOn(){
        switchOnSound = new MediaPlayer();
        switchOnPath = Uri.parse("android.resource://"+itemView.getContext().getPackageName()+"/raw/sound_switch_off");
        try {
            switchOnSound.setDataSource(itemView.getContext(), switchOnPath);
            switchOnSound.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            switchOnSound.prepareAsync();
            switchOnSound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    switchOnSound.start();
                }
            });
            switchOnSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void playSoundSwitchOff(){
        switchOffSound = new MediaPlayer();
        switchOffPath = Uri.parse("android.resource://"+itemView.getContext().getPackageName()+"/raw/sound_switch_off");
        try {
            switchOffSound.setDataSource(itemView.getContext(), switchOffPath);
            switchOffSound.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            switchOffSound.prepareAsync();
            switchOffSound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    switchOffSound.start();
                }
            });
            switchOffSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
//                    mp.release();
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    public void pin1Toggled(boolean on){
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
