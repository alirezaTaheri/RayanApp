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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.GroupsActivity;
import rayan.rayanapp.Adapters.recyclerView.AdminsRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.Listeners.OnToolbarNameChange;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class EditGroupAdminsFragment extends Fragment implements OnAdminClicked<User> {
   // OnToolbarNameChange onToolbarNameChange;
    private List<User> admins;
    private String userId;
    Group group;
    ArrayList<String> adminsUserNames = new ArrayList<>();
    private EditGroupFragmentViewModel editGroupFragmentViewModel;
    AdminsRecyclerViewAdapter managersRecyclerViewAdapter;
    @BindView(R.id.recyclerView)
    RecyclerView adminsRecyclerView;
    @BindView(R.id.addToGrouptxt)
    TextView addToGrouptxt;
    public static EditGroupAdminsFragment newInstance() {
        return new EditGroupAdminsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editGroupFragmentViewModel = ViewModelProviders.of(this).get(EditGroupFragmentViewModel.class);
        admins=EditGroupFragment.admins;
        group=EditGroupFragment.group;

        for (int i = 0; i <= admins.size() - 1; i++) {
          adminsUserNames.add(admins.get(i).getUsername());
      }
        editGroupFragmentViewModel.getGroupLive(group.getId()).observe(this, group1 -> {
            this.group = group1;
            admins = group1.getAdmins();
            for (int i = 0; i <= admins.size() - 1; i++) {
                adminsUserNames.add(admins.get(i).getUsername());
            }
               for (int i = 0; i <= admins.size() - 1; i++) {
                    admins.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(admins.get(i).getUsername(), getActivity()));
                    admins.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(admins.get(i).getUsername(), getActivity()));
                }
            managersRecyclerViewAdapter.setItems(admins);
        });
     managersRecyclerViewAdapter = new AdminsRecyclerViewAdapter(getActivity(),adminsUserNames,adminsUserNames.get(admins.size()-1),"admins_users");
     managersRecyclerViewAdapter.setListener(this);
//        onToolbarNameChange=(OnToolbarNameChange)getActivity();
//        onToolbarNameChange.toolbarNameChanged("مدیران گروه");

        ((GroupsActivity) getActivity()).toolbarNameChanged("مدیران گروه");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_edit_group_admins, container, false);
        ButterKnife.bind(this, view);
        adminsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       adminsRecyclerView.setAdapter(managersRecyclerViewAdapter);
       addToGrouptxt.setText("اضافه کردن مدیر گروه");
       init(group);
        return view;
    }

    @Override
    public void onRemoveAdminClicked(User user) {
        userId = user.getId();
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditGroupAdminsFragmentRemoveAdmin", "حذف مدیر", "بازگشت", "آیا مایل به حذف مدیر گروه هستید؟");
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
    public void init(Group group) {
        managersRecyclerViewAdapter.setItems(group.getAdmins());
    }
    @OnClick(R.id.addToGrouptxt)
    void addManager() {
        UsersListDialogFragment usersListDialogFragment = UsersListDialogFragment.newInstance(group);
        usersListDialogFragment.show(getActivity().getSupportFragmentManager(), "usersList");
    }

    public void doRemoveAdmin() {
        editGroupFragmentViewModel.deleteAdmin(userId, group.getId()).observe(this, baseResponse -> {
            Log.e("remove admin code", baseResponse.getStatus().getCode());
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nouser")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "این کاربر وجود ندارد");
            } else if (baseResponse.getStatus().getCode().equals("200")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "مدیر با موفقیت حذف شد");
                editGroupFragmentViewModel.getGroups();
            } else
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "مشکلی وجود دارد");
        });
    }

    public void clickOnRemoveAdminSubmit() {
            if (admins.size() == 1) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "هر گروه نیاز به یک مدیر دارد");
            } else {
                doRemoveAdmin();
            }
        }
    @Override
    public void onResume() {
        super.onResume();

        ((GroupsActivity) getActivity()).toolbarNameChanged("مدیران گروه");
       // onToolbarNameChange.toolbarNameChanged("مدیران گروه");
    }
    }

