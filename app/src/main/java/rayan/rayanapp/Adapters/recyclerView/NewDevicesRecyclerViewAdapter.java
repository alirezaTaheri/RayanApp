package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.view.ViewGroup;

import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewHolders.NewDevicesViewHolder;

public class NewDevicesRecyclerViewAdapter extends GenericRecyclerViewAdapter<AccessPoint,OnNewDeviceClicked<AccessPoint>, NewDevicesViewHolder>  {

    public AccessPoint selectedSSID;
    public NewDevicesRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public NewDevicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewDevicesViewHolder(inflate(R.layout.item_new_device, parent));
    }
}
