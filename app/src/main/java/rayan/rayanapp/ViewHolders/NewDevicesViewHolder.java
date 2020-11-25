package rayan.rayanapp.ViewHolders;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Fragments.NewDevicesListFragment;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;

public class NewDevicesViewHolder extends BaseViewHolder<AccessPoint, OnNewDeviceClicked<AccessPoint>> {
    private final String TAG = NewDevicesViewHolder.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.connect)
    TextView testDevice;
    NewDevicesListFragment fragment;
    public NewDevicesViewHolder(View itemView, NewDevicesListFragment fragment) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.fragment = fragment;
    }

    @Override
    public void onBind(AccessPoint item, @Nullable OnNewDeviceClicked<AccessPoint> listener) {
        if (fragment.selectedAccessPoint != null &&
                fragment.selectedAccessPoint.getSSID().equals(item.getSSID()))
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_selected_item_base_color));
        else
            itemView.setBackground(null);
        name.setText(item.getSSID());
        name.setOnClickListener(v -> listener.onItemClicked(item));
        testDevice.setOnClickListener(v -> listener.onTestDeviceClicked(item));
    }

}
