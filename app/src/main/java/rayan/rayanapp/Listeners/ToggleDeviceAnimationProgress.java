package rayan.rayanapp.Listeners;

import android.animation.ValueAnimator;

public interface ToggleDeviceAnimationProgress {
    void startToggleAnimationPin1(String chipId, int position);
    void startToggleAnimationPin2(String chipId, int position);
    void stopToggleAnimationPin1(String chipId);
    void stopToggleAnimationPin2(String chipId);
}
