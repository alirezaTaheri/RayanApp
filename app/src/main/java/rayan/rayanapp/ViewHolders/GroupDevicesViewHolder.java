package rayan.rayanapp.ViewHolders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.varunest.sparkbutton.SparkButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;

public class GroupDevicesViewHolder extends BaseViewHolder<Device, OnDeviceClickListenerManagement<Device>> {
    private final String TAG = GroupDevicesViewHolder.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.deviceImage)
    ImageView deviceImage;
    Context context;
    public GroupDevicesViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.context=context;
    }

    @Override
    public void onBind(Device item, @Nullable OnDeviceClickListenerManagement<Device> listener) {
        name.setText(item.getName1());
        if (item.getType().equals("switch_1")){ deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals("switch_2")) { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals("touch_2")) { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lamp_off));
        }else if (item.getType().equals("plug")) { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_plug_off_1));
        }else { deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_device)); }
        }
}
