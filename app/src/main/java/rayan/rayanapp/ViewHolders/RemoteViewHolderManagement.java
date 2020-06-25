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
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class RemoteViewHolderManagement extends DeviceViewHolderManagement {
    private final String TAG = RemoteViewHolderManagement.class.getSimpleName();
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

    public RemoteViewHolderManagement(View itemView, List<String> waiting, Context context) {
        super(itemView, waiting, context);
        ButterKnife.bind(this,itemView);
        this.waiting = waiting;
        this.context=context;
    }

    @Override
    public void onBind(BaseDevice baseDevice, @Nullable OnDeviceClickListenerManagement<BaseDevice> listener) {
        Log.e(TAG ,"onBindRemote " + baseDevice + "\n"+baseDevice.isFavorite());
        Remote item = (Remote) baseDevice;
        name.setText(item.getName());
        favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context, item.isFavorite()?R.drawable.ic_star_full:R.drawable.ic_star_empty));
        visibilityIcon.setImageDrawable(ContextCompat.getDrawable(context, item.isVisibility()?R.drawable.ic_visibility_on:R.drawable.ic_visibility_off));
        itemView.setOnClickListener(v -> {
            listener.onRemoteCLicked(item);
        });
        favoriteIcon.setOnClickListener(v ->  listener.onFavoriteIconClicked(item));
        visibilityIcon.setOnClickListener(v -> listener.onVisibilityIconClicked(item));
//        deviceImage.setImageDrawable(context.getResources().getDrawable(item.getType().equals(AppConstants.REMOTE_TYPE_TV)?R.drawable.ic_tv:R.drawable.ic_air_conditioner));
        deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_air_conditioner));
    }
}
