package rayan.rayanapp.ViewHolders;

import android.animation.ValueAnimator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.OnDeviceClickListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class DeviceViewHolderRemoteHub extends DeviceViewHolder1Bridge {
    private final String TAG = DeviceViewHolderRemoteHub.class.getSimpleName();
    private Map<String, ValueAnimator> animators = new HashMap<>();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.port1)
    ImageView pin1;
    @BindView(R.id.bottomStrip)
    View bottomStrip;
    @BindView(R.id.clickableLayout)
    LinearLayout clickableLayout;
    public DeviceViewHolderRemoteHub(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(BaseDevice baseDevice, @Nullable OnDeviceClickListener<BaseDevice> listener) {
        RemoteHub item = (RemoteHub) baseDevice;
        AppConstants.disableEnableControls(true, (ViewGroup) clickableLayout);
        bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.baseColor2));
        name.setText(item.getName());
        pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_hub));
        bottomStrip.getLayoutParams().width = 0;
        bottomStrip.requestLayout();
        if (listener != null){
        clickableLayout.setOnClickListener(vv -> listener.onClick_RemoteHub(item, this.getAdapterPosition()));
        }
    }


    public void startToggleAnimationPin1(ValueAnimator v){
        Log.e("<<<<<<<<<<<<<<<<","<Starting bridge1");
        AppConstants.disableEnableControls(false, (ViewGroup) itemView);
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
}
