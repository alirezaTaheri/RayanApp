package rayan.rayanapp.Listeners;

import android.animation.ValueAnimator;

public interface ToggleDeviceAnimationProgress {
    void toggleAnimationProgressChanged(int progress, int position);
    void stopToggleAnimation(ValueAnimator valueAnimator, int position, int currentProgress, int progressWidth);
}
