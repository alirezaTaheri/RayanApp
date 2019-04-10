package rayan.rayanapp.Data;

import java.util.List;

import rayan.rayanapp.Retrofit.Models.Responses.api.Group;

public class ExpandableGroup extends com.thoughtbot.expandablerecyclerview.models.ExpandableGroup<Group> {
    public ExpandableGroup(String title, List<Group> items) {
        super(title, items);
    }
}
