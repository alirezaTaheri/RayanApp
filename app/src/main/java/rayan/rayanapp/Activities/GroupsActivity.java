package rayan.rayanapp.Activities;

//<<<<<<< HEAD
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
//=======
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242

import rayan.rayanapp.Fragments.CreateGroupFragment;
import rayan.rayanapp.Fragments.EditGroupFragment;
import rayan.rayanapp.Fragments.GroupsListFragment;
//<<<<<<< HEAD
//=======
import rayan.rayanapp.Fragments.YesNoButtomSheetFragment;
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;

public class GroupsActivity extends AppCompatActivity implements GroupsListFragment.ClickOnGroup, DoneWithFragment, OnBottomSheetSubmitClicked {
    GroupsListFragment groupsListFragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    EditGroupFragment editGroupFragment;
    CreateGroupFragment createGroupFragment;
    YesNoButtomSheetFragment yesNoButtomSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        yesNoButtomSheetFragment=new YesNoButtomSheetFragment();
        yesNoButtomSheetFragment.setOnBottomSheetSubmitClicked(this);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        groupsListFragment = GroupsListFragment.newInstance();
        transaction.replace(R.id.frameLayout, groupsListFragment);
        transaction.commit();

    }

    @Override
    public void clickOnGroup(Group group) {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        editGroupFragment = EditGroupFragment.newInstance(group);
        transaction.replace(R.id.frameLayout, editGroupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void createGroup() {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        createGroupFragment = CreateGroupFragment.newInstance();
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

    @Override
    public void submitClicked(String tag) {
        Log.e("tag of fragment",tag);
        switch (tag){
            case "GroupsListFragment":
                groupsListFragment.clickOnSubmit();
                break;
            case "CreateGroupFragment":
                createGroupFragment.clickOnSubmit();
                break;
            case "EditGroupFragment1":
                editGroupFragment.clickOnRemoveUserSubmit();
                break;
            case "EditGroupFragment2":
                editGroupFragment.clickOnRemoveAdminSubmit();
                break;
            case "EditGroupFragment3":
                editGroupFragment.clickOnLeaveGroupSubmit();
                break;
            default:
                break;
        }

    }
}
