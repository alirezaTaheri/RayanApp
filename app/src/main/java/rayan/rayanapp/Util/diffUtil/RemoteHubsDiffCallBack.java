package rayan.rayanapp.Util.diffUtil;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import java.util.List;

import rayan.rayanapp.Data.RemoteHub;


/**
 * Created by alireza321 on 19/12/2018.
 */

public class RemoteHubsDiffCallBack extends DiffUtil.Callback {
    private List<RemoteHub> newRemoteHubs;
    private List<RemoteHub> oldRemoteHubs;
    public RemoteHubsDiffCallBack(List<RemoteHub> newRemoteHubs, List<RemoteHub> oldRemoteHubs){
        this.newRemoteHubs = newRemoteHubs;
        this.oldRemoteHubs = oldRemoteHubs;
    }
    @Override
    public int getOldListSize() {
        return oldRemoteHubs.size();
    }

    @Override
    public int getNewListSize() {
        return newRemoteHubs.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        RemoteHub oldRemoteHub = oldRemoteHubs.get(oldItemPosition);
        RemoteHub newRemoteHub = newRemoteHubs.get(newItemPosition);
        return oldRemoteHub.getChipId().equals(newRemoteHub.getChipId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        RemoteHub oldRemoteHub = oldRemoteHubs.get(oldItemPosition);
        RemoteHub newRemoteHub = newRemoteHubs.get(newItemPosition);
        return
                oldRemoteHub.getName().equals(newRemoteHub.getName())
                && oldRemoteHub.isFavorite() == newRemoteHub.isFavorite()
                && oldRemoteHub.getSsid().equals(newRemoteHub.getSsid());
//                && oldRemoteHub.getState2_1().equals(newRemoteHub.getState2_1())
//                && oldRemoteHub.getIp().equals(newRemoteHub.getIp())
//                && (oldRemoteHub.isReadyForMqtt() == newRemoteHub.isReadyForMqtt());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        RemoteHub oldRemoteHub = oldRemoteHubs.get(oldItemPosition);
        RemoteHub newRemoteHub = newRemoteHubs.get(newItemPosition);
        Bundle b = new Bundle();
        if (!newRemoteHub.getName().equals(oldRemoteHub.getName()))
            b.putString("name", newRemoteHub.getName());
        if (b.size() == 0) return null;
        return b;
    }
}
