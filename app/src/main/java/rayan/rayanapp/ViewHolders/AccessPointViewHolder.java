package rayan.rayanapp.ViewHolders;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Fragments.ChangeDeviceAccessPointFragment;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;

public class AccessPointViewHolder extends BaseViewHolder<AccessPoint, OnNewDeviceClicked<AccessPoint>> {
    private final String TAG = AccessPointViewHolder.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    ChangeDeviceAccessPointFragment fragment;
    public AccessPointViewHolder(View itemView, ChangeDeviceAccessPointFragment fragment) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.fragment = fragment;
    }

    @Override
    public void onBind(AccessPoint item, @Nullable OnNewDeviceClicked<AccessPoint> listener) {
        Log.e("nnnnnnnnnnn" ,"nnnnnnn: " + fragment.selectedAccessPoint);
        if (fragment.selectedAccessPoint != null && fragment.selectedAccessPoint.getSSID().equals(item.getSSID()))
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(),R.drawable.background_selected_item_base_color));
        else itemView.setBackground(null);
        name.setText(item.getSSID());
        name.setOnClickListener(v -> listener.onItemClicked(item));
        itemView.setOnClickListener(v -> listener.onItemClicked(item));
    }

}
