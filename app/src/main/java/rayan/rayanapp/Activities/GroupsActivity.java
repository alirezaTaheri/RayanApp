package rayan.rayanapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Fragments.CreateGroupFragment;
import rayan.rayanapp.Fragments.EditGroupFragment;
import rayan.rayanapp.Fragments.GroupsListFragment;
import rayan.rayanapp.Fragments.YesNoButtomSheetFragment;
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GroupsActivity extends AppCompatActivity implements GroupsListFragment.ClickOnGroup, DoneWithFragment, OnBottomSheetSubmitClicked {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    GroupsListFragment groupsListFragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    EditGroupFragment editGroupFragment;
    CreateGroupFragment createGroupFragment;
    YesNoButtomSheetFragment yesNoButtomSheetFragment;

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        yesNoButtomSheetFragment = new YesNoButtomSheetFragment();
        yesNoButtomSheetFragment.setOnBottomSheetSubmitClicked(this);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        groupsListFragment = GroupsListFragment.newInstance();
        transaction.replace(R.id.frameLayout, groupsListFragment);
        transaction.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void clickOnGroup(Group group) {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left, R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        editGroupFragment = EditGroupFragment.newInstance(group);
        transaction.replace(R.id.frameLayout, editGroupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void createGroup() {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left, R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
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
        Log.e("tag of fragment", tag);
        switch (tag) {
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