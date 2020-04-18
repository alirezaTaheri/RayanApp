package rayan.rayanapp.Util.diffUtil;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import java.util.List;

import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;


/**
 * Created by alireza321 on 19/12/2018.
 */

public class BaseDeviceDiffCallBack extends DiffUtil.Callback {
    private List<BaseDevice> newBaseDevices;
    private List<BaseDevice> oldBaseDevices;
    public BaseDeviceDiffCallBack(List<BaseDevice> newBaseDevices, List<BaseDevice> oldBaseDevices){
        this.newBaseDevices = newBaseDevices;
        this.oldBaseDevices = oldBaseDevices;
    }
    @Override
    public int getOldListSize() {
        return oldBaseDevices.size();
    }

    @Override
    public int getNewListSize() {
        return newBaseDevices.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        BaseDevice oldThing = oldBaseDevices.get(oldItemPosition);
        BaseDevice newThing = newBaseDevices.get(newItemPosition);
//        return oldThing.getBaseId().equals(newThing.getBaseId());
        if (oldThing.getClass().getName().equals(newThing.getClass().getName())) {
            if (oldThing.getClass().getName().equals(Device.class.getName())) {
                return ((Device) oldThing).getChipId().equals(((Device) newThing).getChipId());
            }else if (oldThing.getClass().getName().equals(RemoteHub.class.getName())){
                return ((RemoteHub) oldThing).getChipId().equals(((RemoteHub) newThing).getChipId());
            }else if (oldThing instanceof Remote){
                return ((Remote) oldThing).getId().equals(((Remote) newThing).getId());
            }
            else return false;
        }
        else return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        BaseDevice oldThing = oldBaseDevices.get(oldItemPosition);
        BaseDevice newThing = newBaseDevices.get(newItemPosition);
        if (oldThing.getClass().getName().equals(newThing.getClass().getName())) {
            if (oldThing.getClass().getName().equals(Device.class.getName())) {
                Device oldDevice = (Device) oldThing;
                Device newDevice = (Device) newThing;
                return
                        oldDevice.getPin1().equals(newDevice.getPin1())
                                && oldDevice.getPin2().equals(newDevice.getPin2())
                                && oldDevice.getName1().equals(newDevice.getName1())
                                && oldDevice.isFavorite() == newDevice.isFavorite()
                                && oldDevice.isLocallyAccessibility() == newDevice.isLocallyAccessibility()
                                && oldDevice.getSsid().equals(newDevice.getSsid())
                                && oldDevice.getPosition() == (newDevice.getPosition())
                                && oldDevice.isHidden() == (newDevice.isHidden())
                                && oldDevice.getIp().equals(newDevice.getIp());
            }else if(oldThing.getClass().getName().equals(RemoteHub.class.getName())){
                RemoteHub oldRemoteHub = (RemoteHub)oldThing;
                RemoteHub newRemoteHub = (RemoteHub)newThing;
                return oldRemoteHub.getName().equals(newRemoteHub.getName())
                        && oldRemoteHub.isFavorite() == newRemoteHub.isFavorite()
                        && oldRemoteHub.getPosition() == newRemoteHub.getPosition()
                        && oldRemoteHub.getInGroupPosition() == newRemoteHub.getInGroupPosition()
                        && oldRemoteHub.getSsid().equals(newRemoteHub.getSsid());
            }else if(oldThing instanceof Remote){
                Remote oldRemote = (Remote)oldThing;
                Remote newRemote = (Remote)newThing;
                return oldRemote.getName().equals(newRemote.getName())
                        && oldRemote.isFavorite() == newRemote.isFavorite()
                        && oldRemote.getPosition() == newRemote.getPosition()
                        && oldRemote.getInGroupPosition() == newRemote.getInGroupPosition()
                        && oldRemote.isVisibility() == newRemote.isVisibility();
            }
            else return false;
        }
        else return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        BaseDevice oldThing = oldBaseDevices.get(oldItemPosition);
        BaseDevice newThing = newBaseDevices.get(newItemPosition);
        Bundle b = new Bundle();
        if (oldThing.getClass().equals(Device.class)){
            Device oldDevice = (Device) oldThing;
            Device newDevice = (Device) newThing;
            if (!newDevice.getName1().equals(oldDevice.getName1())) {
                b.putString("name", newDevice.getName1());
            }
            if (!newDevice.getSsid().equals(oldDevice.getSsid())){
                Log.e("TATATATA", "SSSID: " + newDevice.getSsid());
                b.putString("ssid", newDevice.getSsid());
            }
            if (!newDevice.getPin1().equals(oldDevice.getPin1())){
                b.putString("pin1", newDevice.getPin1());
            }
            if (!newDevice.getIp().equals(oldDevice.getIp())){
                Log.e(this.getClass().getCanonicalName(), "ipchangedchanged" + newDevice.getIp() + oldDevice.getIp() + newDevice.getName1());
                b.putString("ip", newDevice.getIp());
            }
            if (!newDevice.getPin2().equals(oldDevice.getPin2())){
                b.putString("pin2", newDevice.getPin2());
            }
            if (newDevice.getPosition()!=(oldDevice.getPosition())){
                b.putString("position", String.valueOf(newDevice.getPosition()));
            }
            if (!newDevice.isLocallyAccessibility() == (oldDevice.isLocallyAccessibility()))
                b.putBoolean("la", newDevice.isLocallyAccessibility());

        }else if(oldThing.getClass().equals(RemoteHub.class)){
            RemoteHub oldRemoteHub = (RemoteHub) oldThing;
            RemoteHub newRemoteHub = (RemoteHub) newThing;
            if (!newRemoteHub.getName().equals(oldRemoteHub.getName())) {
                b.putString("name", newRemoteHub.getName());
            }
            if (newRemoteHub.getPosition()!=(oldRemoteHub.getPosition())){
                b.putString("position", String.valueOf(newRemoteHub.getPosition()));
            }
        }else if(oldThing instanceof Remote){
            Remote oldRemote = (Remote) oldThing;
            Remote newRemote = (Remote) newThing;
            if (!newRemote.getName().equals(oldRemote.getName())) {
                b.putString("name", newRemote.getName());
            }
            if (newRemote.getPosition()!=(oldRemote.getPosition())){
                b.putString("position", String.valueOf(newRemote.getPosition()));
            }
        }
        if (b.size() == 0) return null;
        return b;
    }
}
