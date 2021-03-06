package rayan.rayanapp.Util.diffUtil;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rayan.rayanapp.Data.Device;


/**
 * Created by alireza321 on 19/12/2018.
 */

public class DevicesDiffCallBack extends DiffUtil.Callback {
    private List<Device> newDevices;
    private List<Device> oldDevices;
    public DevicesDiffCallBack(List<Device> newDevices, List<Device> oldDevices){
        this.newDevices = newDevices;
        this.oldDevices = oldDevices;
//        Log.e(this.getClass().getSimpleName(), "GoodBye oldDevices: " + oldDevices);
//        Log.e(this.getClass().getSimpleName(), "GoodBye newDevices: " + newDevices);
    }
    @Override
    public int getOldListSize() {
        return oldDevices.size();
    }

    @Override
    public int getNewListSize() {
        return newDevices.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Device oldDevice = oldDevices.get(oldItemPosition);
        Device newDevice = newDevices.get(newItemPosition);
        return oldDevice.getChipId().equals(newDevice.getChipId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Device oldDevice = oldDevices.get(oldItemPosition);
        Device newDevice = newDevices.get(newItemPosition);
//        Log.e(this.getClass().getSimpleName(), "GoodBye areContentsTheSame: "+newDevice.getName1()+" " +newDevice.getPin1()+" __ "+ oldDevice.getPin1());
//                && oldDevice.getPin2().equals(newDevice.getPin2())
//                && oldDevice.getName1().equals(newDevice.getName1())
//                && oldDevice.isFavorite() == newDevice.isFavorite()
//                && oldDevice.isLocallyAccessibility() == newDevice.isLocallyAccessibility()));
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
//                && oldDevice.getState2_1().equals(newDevice.getState2_1())
//                && oldDevice.getIp().equals(newDevice.getIp())
//                && (oldDevice.isReadyForMqtt() == newDevice.isReadyForMqtt());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
//        Log.e(this.getClass().getSimpleName(), "GoodBye I am in getChangePayload oldDevices: " + oldDevices);
//        Log.e(this.getClass().getSimpleName(), "GoodBye I am in getChangePayload newDevices: " + newDevices);
        Device oldDevice = oldDevices.get(oldItemPosition);
        Device newDevice = newDevices.get(newItemPosition);
//        Log.e(this.getClass().getSimpleName(), "GoodBye newDevice: " + newDevice);
//        Log.e(this.getClass().getSimpleName(), "GoodBye oldDevice: " + oldDevice);
        Bundle b = new Bundle();
        if (!newDevice.getName1().equals(oldDevice.getName1()))
            b.putString("name", newDevice.getName1());
//            b.putString("chipId", newDevice.getChipId());
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
            b.putString("position", newDevice.getPin1());
        }
        if (!newDevice.isLocallyAccessibility() == (oldDevice.isLocallyAccessibility()))
            b.putBoolean("la", newDevice.isLocallyAccessibility());
        if (b.size() == 0) return null;
        return b;
    }
}
