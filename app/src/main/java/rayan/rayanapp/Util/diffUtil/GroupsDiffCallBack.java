package rayan.rayanapp.Util.diffUtil;

import android.support.v7.util.DiffUtil;

import java.util.List;

import rayan.rayanapp.Retrofit.Models.Responses.Group;

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
        return oldGroups.get(i).getName().equals(newGroups.get(i1).getName()) &&
                oldGroups.get(i).getHumanUsers().size() == newGroups.get(i1).getHumanUsers().size() &&
                oldGroups.get(i).getAdmins().size() == newGroups.get(i1).getAdmins().size() &&
                oldGroups.get(i).getDevices().size() == newGroups.get(i1).getDevices().size();
    }
}
