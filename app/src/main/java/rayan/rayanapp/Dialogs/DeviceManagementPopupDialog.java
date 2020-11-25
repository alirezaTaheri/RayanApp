package rayan.rayanapp.Dialogs;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.DeviceManagementOptionsClickListener;
import rayan.rayanapp.R;

public class DeviceManagementPopupDialog extends AlertDialog {

    DeviceManagementOptionsClickListener listener;
    String message;
    @BindView(R.id.favoriteContainer)
    RelativeLayout favoriteContainer;
    @BindView(R.id.hideContainer)
    RelativeLayout visibilityContainer;
    @BindView(R.id.favoriteIcon)
    ImageView favoriteIcon;
    @BindView(R.id.visibilityIcon)
    ImageView visibilityIcon;

    Device item;
    public DeviceManagementPopupDialog(@NonNull Context context, DeviceManagementOptionsClickListener listener, Device item) {
        super(context);
        this.listener = listener;
        this.message = message;
        this.item = item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.dialog_device_management_popup, null);
        setView(content);
        ButterKnife.bind(this, content);
        favoriteIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), item.isFavorite()?R.drawable.ic_star_full:R.drawable.ic_star_empty));
        visibilityIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), item.isHidden()?R.drawable.ic_visibility_off:R.drawable.ic_visibility_on));
        favoriteContainer.setOnClickListener(v -> {
            favoriteIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), !item.isFavorite()?R.drawable.ic_star_full:R.drawable.ic_star_empty));
            item.setFavorite(!item.isFavorite());
            listener.onFavoriteClicked(item);
        });
        visibilityContainer.setOnClickListener(v -> {
            visibilityIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), !item.isHidden()?R.drawable.ic_visibility_off:R.drawable.ic_visibility_on));
            item.setHidden(!item.isHidden());
            listener.onVisibilityClicked(item);
        });
        this.setCancelable(true);
        super.onCreate(savedInstanceState);
    }

}
