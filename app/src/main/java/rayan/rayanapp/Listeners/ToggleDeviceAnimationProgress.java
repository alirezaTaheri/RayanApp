package rayan.rayanapp.Listeners;

import android.animation.ValueAnimator;

public interface ToggleDeviceAnimationProgress {
    void toggleAnimationProgressChangedPin1(int progress, int position);
    void toggleAnimationProgressChangedPin2(int progress, int position);
    void startToggleAnimationPin1(String chipId, int position);
    void startToggleAnimationPin2();
//    void stopToggleAnimationPin1(ValueAnimator valueAnimator, int position, int currentProgress, int progressWidth);
    void stopToggleAnimationPin1(String chipId);
//    void stopToggleAnimationPin2(ValueAnimator valueAnimator, int position, int currentProgress, int progressWidth);
    void stopToggleAnimationPin2(int position);
}
