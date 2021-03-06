package rayan.rayanapp.Listeners;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;

import rayan.rayanapp.Helper.DeviceAnimator;

public interface ToggleDeviceAnimationProgress {
    void turnOnDeviceAnimationPin1(String chipID, int position, String type);
    void turnOffDeviceAnimationPin1(String chipID, int position, String type);
    void turnOnDeviceAnimationPin2(String chipID, int position, String type);
    void turnOffDeviceAnimationPin2(String chipID, int position, String type);
    void turnOnDevicePin1(String chipID, int position, String type);
    void turnOffDevicePin1(String chipID, int position, String type);
    void turnOnDevicePin2(String chipID, int position, String type);
    void turnOffDevicePin2(String chipID, int position, String type);
    void sendingMessageTimeoutPin1(String chipId, int position, String type);
    void sendingMessageTimeoutPin2(String chipId, int position, String type);
    void updateStripPin1(int position, int width);
    void updateStripPin2(int position, int width);
    int getDeviceItemWidth(int position);
    RecyclerView getRecyclerView();
    DeviceAnimator getDeviceAnimator();
}
