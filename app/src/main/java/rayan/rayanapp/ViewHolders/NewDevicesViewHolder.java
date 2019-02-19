package rayan.rayanapp.ViewHolders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;

public class NewDevicesViewHolder extends BaseViewHolder<NewDevice, OnNewDeviceClicked<NewDevice>> {
    private final String TAG = NewDevicesViewHolder.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    private Context context;

    public NewDevicesViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.context = context;
    }

    @Override
    public void onBind(NewDevice item, @Nullable OnNewDeviceClicked<NewDevice> listener) {
        name.setText(item.getSSID());
        name.setOnClickListener(v -> listener.onItemClicked(item));
    }

}
