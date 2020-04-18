package rayan.rayanapp.Listeners;

import android.widget.ImageView;


public interface OnDeviceClickListenerManagement<T> extends BaseRecyclerListener {
    void onDeviceClicked(T item);
    void onRemoteHubClicked(T item);
    void onRemoteCLicked(T item);
    void onFavoriteIconClicked(T item);
    void onVisibilityIconClicked(T item);
}
