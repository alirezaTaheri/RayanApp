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
//        Log.e(this.getClass().getSimpleName(), "GoodBye areContentsTheSame" + (oldDevice.getPin1().equals(newDevice.getPin1())
//                && oldDevice.getPin2().equals(newDevice.getPin2())
//                && oldDevice.getName1().equals(newDevice.getName1())
//                && oldDevice.isFavorite() == newDevice.isFavorite()
//                && oldDevice.isLocallyAccessibility() == newDevice.isLocallyAccessibility()));
        return
                oldDevice.getPin1().equals(newDevice.getPin1())
                && oldDevice.getPin2().equals(newDevice.getPin2())
                && oldDevice.getName1().equals(newDevice.getName1())
                && oldDevice.isFavorite() == newDevice.isFavorite()
                && oldDevice.isLocallyAccessibility() == newDevice.isLocallyAccessibility();
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
        if (!newDevice.getPin1().equals(oldDevice.getPin1())){
            b.putString("pin1", newDevice.getPin1());
//            Log.e(this.getClass().getSimpleName(), "GoodBye pin 1 was detected");
        }
        if (!newDevice.getPin2().equals(oldDevice.getPin2())){
//            Log.e(this.getClass().getSimpleName(), "GoodBye pin 2 was detected");
            b.putString("pin2", newDevice.getPin2());
        }
        if (!newDevice.isLocallyAccessibility() == (oldDevice.isLocallyAccessibility()))
            b.putBoolean("la", newDevice.isLocallyAccessibility());
//        if (newDevice.getIp() != null && !newDevice.getIp().equals(oldDevice.getIp()))
//            b.putString("ip", newDevice.getIp());
//        if (newDevice.getState2_1() != null && !newDevice.getState2_1().equals(oldDevice.getState2_1()))
//            b.putString("status2_1", newDevice.getState2_1());
//        if (newDevice.getState2_2() != null && !newDevice.getState2_2().equals(oldDevice.getState2_2()))
//            b.putString("status2_2", newDevice.getState2_2());
//        Log.e(this.getClass().getSimpleName(), "GoodBye Bundle is: " + b);
        if (b.size() == 0) return null;
        return b;
    }
}
