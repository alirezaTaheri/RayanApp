package rayan.rayanapp.Util.diffUtil;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;

public class GroupsDiffCallBack extends DiffUtil.Callback {
    private List<Group> newGroups;
    private List<Group> oldGroups;
    public GroupsDiffCallBack(List<Group> newGroups, List<Group> oldGroups){
        this.oldGroups = oldGroups;
        this.newGroups = newGroups;
    }
    @Override
    public int getOldListSize() {
        return oldGroups.size();
    }

    @Override
    public int getNewListSize() {
        return newGroups.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return oldGroups.get(i).getId().equals(newGroups.get(i1).getId());
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
//        boolean devices = true;
//        Group oldG = oldGroups.get(i);
//        Group newG = newGroups.get(i1);
//        for (int a = 0; a<oldG.getDevices().size();a++)
//            for (int b = 0;b<newG.getDevices().size();b++)
//                if (oldG.getDevices().get(a).getId().equals(newG.getDevices().get(b).getId())
//                        && oldG.getDevices().get(a).getName1().equals(newG.getDevices().get(b).getName1()))
//                    devices = false;
        return oldGroups.get(i).getName().equals(newGroups.get(i1).getName()) &&
                oldGroups.get(i).getHumanUsers().size() == newGroups.get(i1).getHumanUsers().size() &&
                oldGroups.get(i).getAdmins().size() == newGroups.get(i1).getAdmins().size() &&
                oldGroups.get(i).getDevices().size() == newGroups.get(i1).getDevices().size();
    }
}
