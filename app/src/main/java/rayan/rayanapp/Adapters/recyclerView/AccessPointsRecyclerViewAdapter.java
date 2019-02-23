package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import android.view.ViewGroup;

import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Fragments.ChangeDeviceAccessPointFragment;
import rayan.rayanapp.Fragments.EditDeviceFragment;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewHolders.AccessPointViewHolder;
import rayan.rayanapp.ViewHolders.NewDevicesViewHolder;

public class AccessPointsRecyclerViewAdapter extends GenericRecyclerViewAdapter<AccessPoint,OnNewDeviceClicked<AccessPoint>, AccessPointViewHolder>  {

    public AccessPoint selectedSSID;
    private ChangeDeviceAccessPointFragment fragment;
    public AccessPointsRecyclerViewAdapter(Context context, ChangeDeviceAccessPointFragment fragment) {
        super(context);
        this.fragment = fragment;
    }

    @Override
    public AccessPointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AccessPointViewHolder(inflate(R.layout.item_access_point, parent), fragment);
    }
}
