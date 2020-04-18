package rayan.rayanapp.ViewHolders;

import android.animation.ValueAnimator;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListener;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;

public abstract class BaseViewHolderManagement extends RecyclerView.ViewHolder {
    public BaseViewHolderManagement(View itemView) {
        super(itemView);
    }

    public abstract void onBind(BaseDevice item, @Nullable OnDeviceClickListenerManagement<BaseDevice> listener);


}
