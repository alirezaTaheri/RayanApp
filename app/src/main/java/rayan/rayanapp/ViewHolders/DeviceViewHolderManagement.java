package rayan.rayanapp.ViewHolders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;

public class DeviceViewHolderManagement extends BaseViewHolderManagement {
    private final String TAG = DeviceViewHolderManagement.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.favoriteIcon)
    ImageView favoriteIcon;
    @BindView(R.id.visibilityIcon)
    ImageView visibilityIcon;
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
    public void onBind(BaseDevice baseDevice, @Nullable OnDeviceClickListenerManagement<BaseDevice> listener) {
        Log.e(TAG ,"manamanamana: " + baseDevice + "\n"+baseDevice.isFavorite());
        Device item = (Device) baseDevice;
        name.setText(item.getName1());
//        optionsIcon.setChecked(item.isFavorite());
        if (waiting.contains(item.getChipId()))
            progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.INVISIBLE);
        favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context, item.isFavorite()?R.drawable.ic_star_full:R.drawable.ic_star_empty));
        visibilityIcon.setImageDrawable(ContextCompat.getDrawable(context, item.isHidden()?R.drawable.ic_visibility_off:R.drawable.ic_visibility_on));
        itemView.setOnClickListener(v -> {
            listener.onDeviceClicked(item);
        });
        favoriteIcon.setOnClickListener(v ->  listener.onFavoriteIconClicked(item));
        visibilityIcon.setOnClickListener(v -> listener.onVisibilityIconClicked(item));
        if (item.getType().equals("switch_1")){ deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals("switch_2")) { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals("touch_2")) { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals("plug")) { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_plug_off_1));
        }else { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_device));
        }
    }
}
