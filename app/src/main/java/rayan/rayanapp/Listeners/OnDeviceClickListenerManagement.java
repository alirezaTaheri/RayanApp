package rayan.rayanapp.Listeners;

import android.widget.ImageView;


public interface OnDeviceClickListenerManagement<T> extends BaseRecyclerListener {
    void onItemClick(T item);
    void onFavoriteIconClicked(T item, ImageView optionsIcon);
}
