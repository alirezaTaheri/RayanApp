package rayan.rayanapp.ViewHolders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
    @BindView(R.id.favoriteIcon)
    SparkButton favoriteIcon;
    private List<String> waiting;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    Context context;
    @BindView(R.id.deviceImage)
    ImageView deviceImage;
    public DeviceViewHolderManagement(View itemView, List<String> waiting, Context context) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.waiting = waiting;
        this.context=context;
    }

    @Override
    public void onBind(Device item, @Nullable OnDeviceClickListenerManagement<Device> listener) {
        Log.e("////////:" ,"This Device: " + item + "\n"+item.isFavorite());
        name.setText(item.getName1());
        favoriteIcon.setChecked(item.isFavorite());
        if (waiting.contains(item.getChipId()))
            progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.INVISIBLE);
        itemView.setOnClickListener(v -> {
            listener.onItemClick(item);
        });
        favoriteIcon.setOnClickListener(v -> {
            listener.onFavoriteIconClicked(item);});
        if (item.getType().equals("switch_1")){ deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals("switch_2")) { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals("touch_2")) { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals("plug")) { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_plug_off_1));
        }else { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_device));
        }
    }
}
