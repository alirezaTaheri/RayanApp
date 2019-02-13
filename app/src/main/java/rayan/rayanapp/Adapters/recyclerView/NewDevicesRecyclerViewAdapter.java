package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.view.ViewGroup;

import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewHolders.NewDevicesViewHolder;

public class NewDevicesRecyclerViewAdapter extends GenericRecyclerViewAdapter<NewDevice,OnNewDeviceClicked<NewDevice>, NewDevicesViewHolder>  {

    public NewDevicesRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public NewDevicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewDevicesViewHolder(inflate(R.layout.item_new_device, parent));
    }
}
