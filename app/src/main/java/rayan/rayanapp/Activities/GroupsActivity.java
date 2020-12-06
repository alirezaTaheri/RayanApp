package rayan.rayanapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.PhoneContact;
import rayan.rayanapp.Fragments.CreateGroupFragment;
import rayan.rayanapp.Fragments.EditGroupAdminsFragment;
import rayan.rayanapp.Fragments.EditGroupFragment;
import rayan.rayanapp.Fragments.EditGroupFragment2;
import rayan.rayanapp.Fragments.EditGroupUsersFragment;
import rayan.rayanapp.Fragments.GroupsListFragment;
import rayan.rayanapp.Fragments.YesNoButtomSheetFragment;
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.Listeners.OnAddUserToGroupSubmitClicked;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GroupsActivity extends AppCompatActivity implements EditGroupFragment.ClickOnEditGroupButton, EditGroupFragment2.ClickOnButton, GroupsListFragment.ClickOnGroup, DoneWithFragment, OnBottomSheetSubmitClicked, OnAddUserToGroupSubmitClicked {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.status)
    TextView status;
    GroupsListFragment groupsListFragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    EditGroupFragment editGroupFragment;
    CreateGroupFragment createGroupFragment;
    EditGroupAdminsFragment editGroupAdminsFragment;
    EditGroupUsersFragment editGroupUsersFragment;
    EditGroupFragment2 editGroupFragment2;
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_right, R.anim.animation_transition_ext_to_left, R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_from_right);
        editGroupFragment = EditGroupFragment.newInstance(group);
        transaction.replace(R.id.frameLayout, editGroupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void createGroup() {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_right, R.anim.animation_transition_ext_to_left, R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_from_right);
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
            case "CreateGroupFragment":
                createGroupFragment.clickOnSubmit();
                break;
//            case "EditGroupUsersFragmentRemoveUser":
//                editGroupUsersFragment.clickOnRemoveUserSubmit();
//                break;
//            case "EditGroupAdminsFragmentRemoveAdmin":
//                editGroupAdminsFragment.clickOnRemoveAdminSubmit();
//                break;
            case "EditGroupFragment3":
                editGroupFragment.clickOnLeaveGroupSubmit();
                break;
//            case "EditGroupFragment2_DeleteGroup":
//                editGroupFragment2.clickOnDeleteGroupSubmit();
//                break;
            default:
                break;
        }
    }

    @Override
    public void addUserToGroupSubmitClicked(ArrayList<PhoneContact> SelectedContacts, String Tag) {
        Log.e("tag of fragment", Tag);
        switch (Tag) {
            case "CreateGroupFragment":
                createGroupFragment.users.clear();
                createGroupFragment.numbers.clear();
                for (int i=0;i<=SelectedContacts.size()-1;i++){
                    User user = new User();
                   user.setContactName(SelectedContacts.get(i).getName());
                   user.setUsername(SelectedContacts.get(i).getNumbers());
                    createGroupFragment.users.add(user);
                    createGroupFragment.numbers.add(SelectedContacts.get(i).getNumbers());
                }
                createGroupFragment.usersRecyclerViewAdapter.setItems(createGroupFragment.users);
                break;
//            case "EditGroupFragment":
//                editGroupFragment.doAddUserFromPhone(SelectedContacts);
//                break;
            default:
                break;
    }
}

    @Override
    public void OnAdminButtonClicked(String id) {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_right, R.anim.animation_transition_ext_to_left, R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_from_right);
        editGroupAdminsFragment = EditGroupAdminsFragment.newInstance(id);
        transaction.replace(R.id.frameLayout, editGroupAdminsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onUserButtonClicked(String id) {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_right, R.anim.animation_transition_ext_to_left, R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_from_right);
        editGroupUsersFragment = EditGroupUsersFragment.newInstance(id);
        transaction.replace(R.id.frameLayout, editGroupUsersFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void toolbarNameChanged(String name) {
        if (status != null)
            status.setText(name);
    }

    @Override
    public void OnEditGroupButtonClicked(String id) {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_right, R.anim.animation_transition_ext_to_left, R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_from_right);
        editGroupFragment2 = EditGroupFragment2.newInstance(id);
        transaction.replace(R.id.frameLayout, editGroupFragment2);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}