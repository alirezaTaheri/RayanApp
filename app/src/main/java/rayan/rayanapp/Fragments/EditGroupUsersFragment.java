package rayan.rayanapp.Fragments;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.GroupsActivity;
import rayan.rayanapp.Adapters.recyclerView.AdminsRecyclerViewAdapter;
import rayan.rayanapp.Adapters.recyclerView.UsersRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.Listeners.OnToolbarNameChange;
import rayan.rayanapp.Listeners.OnUserClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class EditGroupUsersFragment extends Fragment implements OnAdminClicked<User> {
    private List<User> users;
    //OnToolbarNameChange onToolbarNameChange;
    private String userId;
    Group group;
    ArrayList<String> userNames = new ArrayList<>();
    private EditGroupFragmentViewModel editGroupFragmentViewModel;
    AdminsRecyclerViewAdapter usersRecyclerViewAdapter;
    @BindView(R.id.recyclerView)
    RecyclerView usersRecyclerView;
    @BindView(R.id.addToGrouptxt)
    TextView addToGrouptxt;
    @BindView(R.id.addToGroupLayout)
    LinearLayout addToGroupLayout;
    @BindView(R.id.addToGroupLine)
    View addToGroupLine;


    public static EditGroupUsersFragment newInstance() {
        return new EditGroupUsersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editGroupFragmentViewModel = ViewModelProviders.of(this).get(EditGroupFragmentViewModel.class);
        users=EditGroupFragment.humanUsers;
        group=EditGroupFragment.group;

        for (int i = 0; i <= users.size() - 1; i++) {
            userNames.add(users.get(i).getUsername());
        }
        editGroupFragmentViewModel.getGroupLive(group.getId()).observe(this, group1 -> {
            this.group = group1;
            users = group1.getHumanUsers();
            for (int i = 0; i <= users.size() - 1; i++) {
                userNames.add(users.get(i).getUsername());
            }
            for (int i = 0; i <= users.size() - 1; i++) {
                users.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(users.get(i).getUsername(), getActivity()));
                users.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(users.get(i).getUsername(), getActivity()));
            }
            usersRecyclerViewAdapter.setItems(users);
        });
        usersRecyclerViewAdapter = new AdminsRecyclerViewAdapter(getActivity(),userNames,"admins_users");
        usersRecyclerViewAdapter.setListener(this);
//        onToolbarNameChange=(OnToolbarNameChange)getActivity();
//        onToolbarNameChange.toolbarNameChanged("کاربران گروه");

        ((GroupsActivity) getActivity()).toolbarNameChanged("کاربران گروه");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_group_admins, container, false);
        ButterKnife.bind(this, view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersRecyclerView.setAdapter(usersRecyclerViewAdapter);
        addToGrouptxt.setVisibility(View.GONE);
        addToGroupLayout.setVisibility(View.GONE);
        addToGroupLine.setVisibility(View.GONE);
        init(group);
        return view;
    }
    public void init(Group group) {
        usersRecyclerViewAdapter.setItems(group.getHumanUsers());
    }

    @Override
    public void onRemoveAdminClicked(User item) {
        userId = item.getId();
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditGroupUsersFragmentRemoveUser", "حذف کاربر", "بازگشت", "آیا مایل به حذف کاربر گروه هستید؟");
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void clickOnRemoveUserSubmit() {
        if (users.size() == 1) {
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "تنها عضو گروه هستید! برای خروج گروه را حذف کنید");
        } else {
            doLeaveGroup(userId, group.getId());
        }
    }

    public void doLeaveGroup(String id, String groupId) {
        editGroupFragmentViewModel.deleteUser(id, groupId).observe(this, baseResponse -> {
            Log.e("remove user code", baseResponse.getStatus().getCode());
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nouser")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "این کاربر وجود ندارد");
            } else if (baseResponse.getStatus().getCode().equals("404")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "حذف این کاربر امکان پذیر نیست");
            } else if (baseResponse.getStatus().getCode().equals("403")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شما قادر به حذف این کاربر نیستید");
            } else if (baseResponse.getStatus().getCode().equals("204")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "کاربر با موفقیت حذف گردید");
            } else if (baseResponse.getStatus().getCode().equals("200")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "کاربر با موفقیت حذف گردید");
            } else
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "مشکلی وجود دارد");
        });
    }
    @Override
    public void onResume() {
        super.onResume();

        ((GroupsActivity) getActivity()).toolbarNameChanged("کاربران گروه");
       // onToolbarNameChange.toolbarNameChanged("کاربران گروه");
    }
}

