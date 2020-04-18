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
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;

public class RemoteHubViewHolderManagement extends DeviceViewHolderManagement {
    private final String TAG = RemoteHubViewHolderManagement.class.getSimpleName();
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

    public RemoteHubViewHolderManagement(View itemView, List<String> waiting, Context context) {
        super(itemView, waiting, context);
        ButterKnife.bind(this,itemView);
        this.waiting = waiting;
        this.context=context;
    }

    @Override
    public void onBind(BaseDevice baseDevice, @Nullable OnDeviceClickListenerManagement<BaseDevice> listener) {
        Log.e(TAG ,"onBindRemoteHub " + baseDevice + "\n"+baseDevice.isFavorite());
        RemoteHub item = (RemoteHub) baseDevice;
        name.setText(item.getName());
//        optionsIcon.setChecked(item.isFavorite());
        if (waiting.contains(item.getChipId()))
            progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.INVISIBLE);
        favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context, item.isFavorite()?R.drawable.ic_star_full:R.drawable.ic_star_empty));
        visibilityIcon.setImageDrawable(ContextCompat.getDrawable(context, item.isVisibility()?R.drawable.ic_visibility_on:R.drawable.ic_visibility_off));
        itemView.setOnClickListener(v -> {
            listener.onRemoteHubClicked(item);
        });
        favoriteIcon.setOnClickListener(v ->  listener.onFavoriteIconClicked(item));
        visibilityIcon.setOnClickListener(v -> listener.onVisibilityIconClicked(item));
        deviceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_hub));
    }
}
