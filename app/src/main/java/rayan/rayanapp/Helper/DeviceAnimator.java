package rayan.rayanapp.Helper;

import android.animation.ValueAnimator;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Fragments.DevicesFragment;

public class DeviceAnimator {
    Map<String, ValueAnimator> animatorMap = new HashMap<>();
    DevicesFragment devicesFragment;

    public void deviceTurnedOn(String chipId, int position, DevicesFragment devicesFragment){
        Log.e("DeviceAnimator", "device Turned On");
        ValueAnimator v = animatorMap.get(chipId);
        if (v != null){
            ValueAnimator finalV1 = v;
            devicesFragment.recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    finalV1.cancel();
                    finalV1.end();
                    finalV1.setIntValues((int)finalV1.getAnimatedValue(), 510);
                    finalV1.setDuration(300);
                    finalV1.start();

                }
            });
        }
        else{
            v = ValueAnimator.ofInt(0,510);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    devicesFragment.recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            devicesFragment.updateStripPin1(chipId, position, (int) animation.getAnimatedValue());
                        }
                    });
                }
            });
            v.setDuration(300);
            ValueAnimator finalV = v;
            devicesFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalV.start();
                }
            });
        }

    }
    public void deviceTurnedOff(String chipId, int position, DevicesFragment devicesFragment){
        ValueAnimator v = animatorMap.get(chipId);
        if (v != null){
            Log.e("deviceanimator;:: " , "current is: " + (int)v.getAnimatedValue());
            v.setIntValues((int)v.getAnimatedValue(), 0);
            v.setDuration(300);
            ValueAnimator finalV1 = v;
            devicesFragment.recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    finalV1.start();
                }
            });
        }else{
            v = ValueAnimator.ofInt(510,0);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    devicesFragment.recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            devicesFragment.updateStripPin1(chipId, position, (int) animation.getAnimatedValue());

                        }
                    });
                }
            });
            v.setDuration(300);
            ValueAnimator finalV = v;
            devicesFragment.recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    finalV.start();
                }
            });
        }
    }
    public void errorOccurred(String chipId, int position, DevicesFragment devicesFragment){
        Log.e(this.getClass().getSimpleName(), "Stopping The Animation");
        ValueAnimator v;
        v = ValueAnimator.ofInt(510,0);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                devicesFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        devicesFragment.updateStripPin1(chipId, position, (int) animation.getAnimatedValue());
                    }
                });
            }
        });
        v.setDuration(300);
        devicesFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                v.start();
            }
        });
    }
    public void turningOn(String chipId, int position, DevicesFragment devicesFragment){
        Log.e(this.getClass().getSimpleName(), "Starting The Animation");
        ValueAnimator v;
        if (animatorMap.get(chipId) == null){
            v = ValueAnimator.ofInt(0,510);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    devicesFragment.updateStripPin1(chipId, position, (int) animation.getAnimatedValue());
                }
            });
            animatorMap.put(chipId, v);
        }else {
            v = animatorMap.get(chipId);
            v.setIntValues(0, 510);
        }
        v.setDuration(4000);
        v.start();
    }
    public void turningOff(String chipId, int position, DevicesFragment devicesFragment){
        Log.e(this.getClass().getSimpleName(), "turning off animation...");
        ValueAnimator v;
        if (animatorMap.get(chipId) == null){
            v = ValueAnimator.ofInt(510,0);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    devicesFragment.updateStripPin1(chipId, position, (int) animation.getAnimatedValue());
                }
            });
            animatorMap.put(chipId, v);
        }else {
            v = animatorMap.get(chipId);
            v.setIntValues(510,0);
        }
        v.setDuration(4000);
        v.start();
    }
    public void stopAnimation(String chipId, int position, DevicesFragment devicesFragment){

    }
}
