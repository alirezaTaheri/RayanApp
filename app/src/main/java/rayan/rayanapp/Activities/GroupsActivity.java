package rayan.rayanapp.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rayan.rayanapp.Fragments.CreateGroupFragment;
import rayan.rayanapp.Fragments.EditGroupFragment;
import rayan.rayanapp.Fragments.GroupsListFragment;
import rayan.rayanapp.Fragments.NewDeviceSetConfigurationFragment;
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;

public class GroupsActivity extends AppCompatActivity implements GroupsListFragment.ClickOnGroup, DoneWithFragment {

    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        GroupsListFragment groupsListFragment = GroupsListFragment.newInstance();
        transaction.replace(R.id.frameLayout, groupsListFragment);
        transaction.commit();

    }

    @Override
    public void clickOnGroup(Group group) {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        EditGroupFragment editGroupFragment = EditGroupFragment.newInstance(group);
        transaction.replace(R.id.frameLayout, editGroupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void createGroup() {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        CreateGroupFragment createGroupFragment = CreateGroupFragment.newInstance();
        transaction.replace(R.id.frameLayout, createGroupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void operationDone() {
        onBackPressed();
    }
}
