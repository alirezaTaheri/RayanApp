package rayan.rayanapp.Helper;

import android.animation.ValueAnimator;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.Util.AppConstants;

public class DeviceAnimator {
    Map<String, ValueAnimator> animatorMap = new HashMap<>();
    Map<String, Boolean> responseReceived = new HashMap<>();
    private int itemWidth = -1;
    private int halfItemWidth = -1;
    private final String TAG = "DeviceAnimator";

    public void setItemWidth(ToggleDeviceAnimationProgress fragment){
        fragment.getRecyclerView().post(new Runnable() {
            @Override
            public void run() {
                itemWidth = fragment.getDeviceItemWidth(0);
                halfItemWidth = itemWidth/2;
            }
        });
    }

    public void deviceTurnedOnPin1(String chipId, int position, ToggleDeviceAnimationProgress fragment, String type){
        Log.d(TAG, "deviceTurnedOnPin1() called with: chipId = [" + chipId + "], position = [" + position + "], fragment = [" + fragment + "], type = [" + type + "]");
        boolean singleBridge = type.equals(AppConstants.DEVICE_TYPE_SWITCH_1) || type.equals(AppConstants.DEVICE_TYPE_PLUG);
        if (itemWidth == -1)
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    itemWidth = fragment.getDeviceItemWidth(position);
                    halfItemWidth = itemWidth/2;
                }
            });
        responseReceived.put(chipId+AppConstants.NAMING_PREFIX_PIN1, true);
        Log.e("DeviceAnimator", "device Turned On Animation Starting");
        ValueAnimator v = animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN1);
        if (v != null){
            v.removeAllUpdateListeners();
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            fragment.updateStripPin1(position, (int) animation.getAnimatedValue());
                        }
                    });
                }
            });
            ValueAnimator finalV1 = v;
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    finalV1.setIntValues((int)finalV1.getAnimatedValue(), singleBridge?itemWidth:halfItemWidth);
                    finalV1.setDuration(300);
                    finalV1.start();
                }
            });
        }
        else{
            v = ValueAnimator.ofInt(0,singleBridge?itemWidth:halfItemWidth);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            fragment.updateStripPin1(position, (int) animation.getAnimatedValue());
                        }
                    });
                }
            });
            v.setDuration(300);
            ValueAnimator finalV = v;
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    finalV.start();
                }
            });
        }

    }
    public void deviceTurnedOffPin1(String chipId, int position, ToggleDeviceAnimationProgress fragment, String type){
        Log.d(TAG, "deviceTurnedOffPin1() called with: chipId = [" + chipId + "], position = [" + position + "], fragment = [" + fragment + "], type = [" + type + "]");
        boolean singleBridge = type.equals(AppConstants.DEVICE_TYPE_SWITCH_1) || type.equals(AppConstants.DEVICE_TYPE_PLUG);
        if (itemWidth == -1)
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    itemWidth = fragment.getDeviceItemWidth(position);
                    halfItemWidth = itemWidth/2;
                }
            });
        responseReceived.put(chipId+AppConstants.NAMING_PREFIX_PIN1, true);
        Log.e("DeviceAnimator", "device Turned Off Animation Starting");
        ValueAnimator v = animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN1);
        if (v != null){
            Log.e("deviceanimator;:: " , "current is: " + (int)v.getAnimatedValue());
            v.removeAllUpdateListeners();
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            fragment.updateStripPin1(position, (int) animation.getAnimatedValue());

                        }
                    });
                }
            });
            v.setIntValues((int)v.getAnimatedValue(), 0);
            v.setDuration(300);
            ValueAnimator finalV1 = v;
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    finalV1.start();
                }
            });
        }else{
            v = ValueAnimator.ofInt(singleBridge?itemWidth:halfItemWidth,0);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            fragment.updateStripPin1(position, (int) animation.getAnimatedValue());

                        }
                    });
                }
            });
            v.setDuration(300);
            ValueAnimator finalV = v;
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    finalV.start();
                }
            });
        }
    }
    public void turningOnPin1(String chipId, int position, ToggleDeviceAnimationProgress fragment, String type){
        Log.d(TAG, "turningOnPin1() called with: chipId = [" + chipId + "], position = [" + position + "], fragment = [" + fragment + "], type = [" + type + "]");
        boolean singleBridge = type.equals(AppConstants.DEVICE_TYPE_SWITCH_1) || type.equals(AppConstants.DEVICE_TYPE_PLUG);
        if (itemWidth == -1)
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    itemWidth = fragment.getDeviceItemWidth(position);
                    halfItemWidth = itemWidth/2;
                }
            });
        responseReceived.put(chipId+AppConstants.NAMING_PREFIX_PIN1, false);
        Log.e(this.getClass().getSimpleName(), "Starting Turning on Animation");
        ValueAnimator v;
        if (animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN1) == null){
            v = ValueAnimator.ofInt(0,singleBridge?itemWidth:halfItemWidth);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.updateStripPin1(position, (int) animation.getAnimatedValue());
                }
            });
            animatorMap.put(chipId+AppConstants.NAMING_PREFIX_PIN1, v);
        }else {
            v = animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN1);
            v.removeAllUpdateListeners();
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.updateStripPin1(position, (int) animation.getAnimatedValue());
                }
            });
            v.setIntValues(0, singleBridge?itemWidth:halfItemWidth);
        }
        v.setDuration(4000);
        v.start();
    }
    public void turningOffPin1(String chipId, int position, ToggleDeviceAnimationProgress fragment, String type){
        Log.d(TAG, "turningOffPin1() called with: chipId = [" + chipId + "], position = [" + position + "], fragment = [" + fragment + "], type = [" + type + "]");
        boolean singleBridge = type.equals(AppConstants.DEVICE_TYPE_SWITCH_1) || type.equals(AppConstants.DEVICE_TYPE_PLUG);
        if (itemWidth == -1)
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    itemWidth = fragment.getDeviceItemWidth(position);
                    halfItemWidth = itemWidth/2;
                }
            });
        responseReceived.put(chipId+AppConstants.NAMING_PREFIX_PIN1, false);
        Log.e(this.getClass().getSimpleName(), "turning off animation...Pin2" + (singleBridge?itemWidth:halfItemWidth));
        ValueAnimator v;
        if (animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN1) == null){
            v = ValueAnimator.ofInt(singleBridge?itemWidth:halfItemWidth,0);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.updateStripPin1(position, (int) animation.getAnimatedValue());
                }
            });
            animatorMap.put(chipId+AppConstants.NAMING_PREFIX_PIN1, v);
        }else {
            v = animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN1);
            v.removeAllUpdateListeners();
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.updateStripPin1(position, (int) animation.getAnimatedValue());
                }
            });
            v.setIntValues(singleBridge?itemWidth:halfItemWidth,0);
        }
        v.setDuration(4000);
        v.start();
    }

    public void deviceTurnedOnPin2(String chipId, int position, ToggleDeviceAnimationProgress fragment, String type){
        Log.d(TAG, "deviceTurnedOnPin2() called with: chipId = [" + chipId + "], position = [" + position + "], fragment = [" + fragment + "], type = [" + type + "]");
        boolean singleBridge = type.equals(AppConstants.DEVICE_TYPE_SWITCH_1) || type.equals(AppConstants.DEVICE_TYPE_PLUG);
        if (itemWidth == -1)
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    itemWidth = fragment.getDeviceItemWidth(position);
                    halfItemWidth = itemWidth/2;
                }
            });
        responseReceived.put(chipId+AppConstants.NAMING_PREFIX_PIN2, true);
        Log.e("DeviceAnimator", "device Turned On Pin 2");
        ValueAnimator v = animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN2);
        if (v != null){
            ValueAnimator finalV1 = v;
            v.removeAllUpdateListeners();
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            fragment.updateStripPin2(position, (int) animation.getAnimatedValue());
                        }
                    });
                }
            });
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    finalV1.setIntValues((int)finalV1.getAnimatedValue(), singleBridge?itemWidth:halfItemWidth);
                    finalV1.setDuration(300);
                    finalV1.start();
                }
            });
        }
        else{
            v = ValueAnimator.ofInt(0,singleBridge?itemWidth:halfItemWidth);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            fragment.updateStripPin2(position, (int) animation.getAnimatedValue());
                        }
                    });
                }
            });
            v.setDuration(300);
            ValueAnimator finalV = v;
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    finalV.start();
                }
            });
        }

    }
    public void deviceTurnedOffPin2(String chipId, int position, ToggleDeviceAnimationProgress fragment, String type){
        Log.d(TAG, "deviceTurnedOffPin2() called with: chipId = [" + chipId + "], position = [" + position + "], fragment = [" + fragment + "], type = [" + type + "]");
        boolean singleBridge = type.equals(AppConstants.DEVICE_TYPE_SWITCH_1) || type.equals(AppConstants.DEVICE_TYPE_PLUG);
        if (itemWidth == -1)
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    itemWidth = fragment.getDeviceItemWidth(position);
                    halfItemWidth = itemWidth/2;
                }
            });
        responseReceived.put(chipId+AppConstants.NAMING_PREFIX_PIN2, true);
        ValueAnimator v = animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN2);
        if (v != null){
            Log.e("deviceanimator;:: " , "current is: " + (int)v.getAnimatedValue());
            v.removeAllUpdateListeners();
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            fragment.updateStripPin2(position, (int) animation.getAnimatedValue());

                        }
                    });
                }
            });
            v.setIntValues((int)v.getAnimatedValue(), 0);
            v.setDuration(300);
            ValueAnimator finalV1 = v;
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    finalV1.start();
                }
            });
        }else{
            v = ValueAnimator.ofInt(singleBridge?itemWidth:halfItemWidth,0);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.getRecyclerView().post(new Runnable() {
                        @Override
                        public void run() {
                            fragment.updateStripPin2(position, (int) animation.getAnimatedValue());

                        }
                    });
                }
            });
            v.setDuration(300);
            ValueAnimator finalV = v;
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    finalV.start();
                }
            });
        }
    }
    public void turningOnPin2(String chipId, int position, ToggleDeviceAnimationProgress fragment, String type){
        Log.d(TAG, "turningOnPin2() called with: chipId = [" + chipId + "], position = [" + position + "], fragment = [" + fragment + "], type = [" + type + "]");
        boolean singleBridge = type.equals(AppConstants.DEVICE_TYPE_SWITCH_1) || type.equals(AppConstants.DEVICE_TYPE_PLUG);
        if (itemWidth == -1)
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    itemWidth = fragment.getDeviceItemWidth(position);
                    halfItemWidth = itemWidth/2;
                }
            });
        responseReceived.put(chipId+AppConstants.NAMING_PREFIX_PIN2, false);
        Log.e(this.getClass().getSimpleName(), "Starting The Animation");
        ValueAnimator v;
        if (animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN2) == null){
            v = ValueAnimator.ofInt(0,singleBridge?itemWidth:halfItemWidth);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.updateStripPin2(position, (int) animation.getAnimatedValue());
                }
            });
            animatorMap.put(chipId+AppConstants.NAMING_PREFIX_PIN2, v);
        }else {
            v = animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN2);
            v.removeAllUpdateListeners();
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.updateStripPin2(position, (int) animation.getAnimatedValue());
                }
            });
            v.setIntValues(0, singleBridge?itemWidth:halfItemWidth);
        }
        v.setDuration(4000);
        v.start();
    }
    public void turningOffPin2(String chipId, int position, ToggleDeviceAnimationProgress fragment, String type){
        Log.d(TAG, "turningOffPin2() called with: chipId = [" + chipId + "], position = [" + position + "], fragment = [" + fragment + "], type = [" + type + "]");
        boolean singleBridge = type.equals(AppConstants.DEVICE_TYPE_SWITCH_1) || type.equals(AppConstants.DEVICE_TYPE_PLUG);
        if (itemWidth == -1)
            fragment.getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    itemWidth = fragment.getDeviceItemWidth(position);
                    halfItemWidth = itemWidth/2;
                }
            });
        responseReceived.put(chipId+AppConstants.NAMING_PREFIX_PIN2, false);
        Log.e(this.getClass().getSimpleName(), "turning off animation...Pin2" + (singleBridge?itemWidth:halfItemWidth));
        ValueAnimator v;
        if (animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN2) == null){
            v = ValueAnimator.ofInt(singleBridge?itemWidth:halfItemWidth,0);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.updateStripPin2(position, (int) animation.getAnimatedValue());
                }
            });
            animatorMap.put(chipId+AppConstants.NAMING_PREFIX_PIN2, v);
        }else {
            v = animatorMap.get(chipId+AppConstants.NAMING_PREFIX_PIN2);
            v.removeAllUpdateListeners();
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fragment.updateStripPin2(position, (int) animation.getAnimatedValue());
                }
            });
            v.setIntValues(singleBridge?itemWidth:halfItemWidth,0);
        }
        v.setDuration(4000);
        v.start();
    }

    public boolean isResponseReceivedPin1(String chioId){
        return responseReceived.get(chioId+AppConstants.NAMING_PREFIX_PIN1);
    }
    public boolean isResponseReceivedPin2(String chioId){
        return responseReceived.get(chioId+AppConstants.NAMING_PREFIX_PIN2);
    }

}
