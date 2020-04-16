package rayan.rayanapp.Util.diffUtil;

import android.support.v7.util.DiffUtil;

import java.util.List;

import rayan.rayanapp.Data.Remote;


public class RemotesDiffCallBack extends DiffUtil.Callback {
    private List<Remote> newRemotes;
    private List<Remote> oldRemotes;
    public RemotesDiffCallBack(List<Remote> newRemotes, List<Remote> oldRemotes){
        this.oldRemotes = oldRemotes;
        this.newRemotes = newRemotes;
    }
    @Override
    public int getOldListSize() {
        return oldRemotes.size();
    }

    @Override
    public int getNewListSize() {
        return newRemotes.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return oldRemotes.get(i).getId().equals(newRemotes.get(i1).getId());
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
//        boolean devices = true;
//        Remote oldG = oldRemotes.get(i);
//        Remote newG = newRemotes.get(i1);
//        for (int a = 0; a<oldG.getDevices().size();a++)
//            for (int b = 0;b<newG.getDevices().size();b++)
//                if (oldG.getDevices().get(a).getId().equals(newG.getDevices().get(b).getId())
//                        && oldG.getDevices().get(a).getName1().equals(newG.getDevices().get(b).getName1()))
//                    devices = false;
        return oldRemotes.get(i).getName().equals(newRemotes.get(i1).getName());
    }
}
