package rayan.rayanapp.Helper;


import android.os.Parcelable;

public abstract class RevealAnimationSetting implements Parcelable {
        public abstract int getCenterX();
        public abstract int getCenterY();
        public abstract int getWidth();
        public abstract int getHeight();

//        public static RevealAnimationSetting with(int centerX, int centerY, int width, int height) {
//            return new AutoValue_RevealAnimationSetting(centerX, centerY, width, height);
//        }

}
