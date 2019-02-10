package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.Listeners.OnUserClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.User;

public class NewDevicesViewHolder extends BaseViewHolder<NewDevice, OnNewDeviceClicked<NewDevice>> {
    private final String TAG = NewDevicesViewHolder.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    public NewDevicesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(NewDevice item, @Nullable OnNewDeviceClicked<NewDevice> listener) {
        name.setText(item.getSSID());
//        name.setOnClickListener(v -> listener.onItemClicked(item));
    }
}
