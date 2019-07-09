package rayan.rayanapp.Listeners;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;

public interface ToggleDeviceAnimationProgress {
//    void startToggleAnimationPin1(String chipId, int position);
//    void startToggleAnimationPin2(String chipId, int position);
//    void stopToggleAnimationPin1(String chipId);
//    void stopToggleAnimationPin2(String chipId);
    void turnOnDeviceAnimationPin1(String chipID, int position);
    void turnOffDeviceAnimationPin1(String chipID, int position);
    void turnOnDeviceAnimationPin2(String chipID, int position);
    void turnOffDeviceAnimationPin2(String chipID, int position);
    void sendingMessageTimeoutPin1(String chipId, int position);
    void sendingMessageTimeoutPin2(String chipId, int position);
    void updateStripPin1(int position, int width);
    void updateStripPin2(int position, int width);
    int getDeviceItemWidth(int position);
    RecyclerView getRecyclerView();
}
