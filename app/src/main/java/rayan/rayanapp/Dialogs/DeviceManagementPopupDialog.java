package rayan.rayanapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.varunest.sparkbutton.SparkButton;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.DeviceManagementOptionsClickListener;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;

public class DeviceManagementPopupDialog extends AlertDialog {

    DeviceManagementOptionsClickListener listener;
    String message;
    @BindView(R.id.favoriteContainer)
    RelativeLayout favoriteContainer;
    @BindView(R.id.hideContainer)
    RelativeLayout visibilityContainer;
    @BindView(R.id.favoriteIcon)
    SparkButton favoriteIcon;
    @BindView(R.id.visibilityIcon)
    SparkButton visibilityIcon;

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
        favoriteIcon.setEnabled(false);
        visibilityIcon.setEnabled(false);
        favoriteIcon.setChecked(item.isFavorite());
        visibilityIcon.setChecked(item.isHidden());
        favoriteContainer.setOnClickListener(v -> {
            favoriteIcon.setChecked(!item.isFavorite());
            item.setFavorite(!item.isFavorite());
            listener.onFavoriteClicked(item);
        });
        visibilityContainer.setOnClickListener(v -> {
            visibilityIcon.setChecked(!item.isHidden());
            item.setHidden(!item.isHidden());
            listener.onVisibilityClicked(item);
        });
        this.setCancelable(true);
        super.onCreate(savedInstanceState);
    }

}
