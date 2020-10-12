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
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.OnDeviceClickListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class DeviceViewHolderRemote extends DeviceViewHolder1Bridge {
    private final String TAG = DeviceViewHolderRemote.class.getSimpleName();
    private Map<String, ValueAnimator> animators = new HashMap<>();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.port1)
    ImageView pin1;
    @BindView(R.id.bottomStrip)
    View bottomStrip;
    @BindView(R.id.clickableLayout)
    LinearLayout clickableLayout;
    public DeviceViewHolderRemote(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(BaseDevice baseDevice, @Nullable OnDeviceClickListener<BaseDevice> listener) {
        Remote item = (Remote) baseDevice;
        AppConstants.disableEnableControls(true, (ViewGroup) clickableLayout);
        Log.e("UNIQUETAG", "OnBindViewHolder: Type3" +" "+ item.getName() +"\n"+ itemView.getId());
        Log.e(TAG, "Processing this Device: " + item);
        bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.baseColor2));
        name.setText(item.getName());
        pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_hub));
        pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_hub));
        bottomStrip.getLayoutParams().width = 0;
        bottomStrip.requestLayout();
        if (listener != null){
            clickableLayout.setOnClickListener(vv -> listener.onClick_RemoteHub(item, this.getAdapterPosition()));
        }
    }

}
