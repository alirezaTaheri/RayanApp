package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;

public class DeviceViewHolderManagement extends BaseViewHolder<Device, OnDeviceClickListenerManagement<Device>> {
    private final String TAG = DeviceViewHolderManagement.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    private List<String> waiting = new ArrayList<>();
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    public DeviceViewHolderManagement(View itemView, List<String> waiting) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.waiting = waiting;
    }

    @Override
    public void onBind(Device item, @Nullable OnDeviceClickListenerManagement<Device> listener) {
        name.setText(item.getName1());
        if (waiting.contains(item.getChipId()))
            progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.INVISIBLE);
        itemView.setOnClickListener(v -> {
            listener.onItemClick(item);
        });
    }
}
