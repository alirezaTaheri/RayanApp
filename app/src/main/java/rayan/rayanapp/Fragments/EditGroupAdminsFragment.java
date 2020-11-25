package rayan.rayanapp.Fragments;

import android.Manifest;
import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.GroupsActivity;
import rayan.rayanapp.Adapters.recyclerView.GroupUsersRecyclerViewAdapter;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class EditGroupAdminsFragment extends Fragment implements OnAdminClicked<User> {
   // OnToolbarNameChange onToolbarNameChange;
    private List<User> admins;
    private String userId;
    Group group;
    String groupId;
    ArrayList<String> adminsUserNames = new ArrayList<>();
    private EditGroupFragmentViewModel editGroupFragmentViewModel;
    GroupUsersRecyclerViewAdapter managersRecyclerViewAdapter;
    @BindView(R.id.recyclerView)
    RecyclerView adminsRecyclerView;
    @BindView(R.id.addToGrouptxt)
    TextView addToGrouptxt;
    Activity activity;
    public static EditGroupAdminsFragment newInstance(String id) {
        EditGroupAdminsFragment fragment = new EditGroupAdminsFragment();
        Bundle b = new Bundle();
        b.putString("id", id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editGroupFragmentViewModel = ViewModelProviders.of(this).get(EditGroupFragmentViewModel.class);
        editGroupFragmentViewModel.getJustAdminsInGroup(getArguments().getString("id")).observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                admins=users;
                for (int i = 0; i <= admins.size() - 1; i++) {
                    adminsUserNames.add(admins.get(i).getUsername());
                }
                managersRecyclerViewAdapter.setItems(admins);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                for (int i = 0; i <= admins.size() - 1; i++) {
                    admins.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(admins.get(i).getUsername(), getActivity()));
                    admins.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(admins.get(i).getUsername(), getActivity()));
                }
            }
        });
        editGroupFragmentViewModel.getGroupLive(getArguments().getString("id")).observe(this, new Observer<Group>() {
            @Override
            public void onChanged(@Nullable Group g) {
                group = g;
            }
        });
     managersRecyclerViewAdapter = new GroupUsersRecyclerViewAdapter(getActivity(),adminsUserNames,"admins_users");
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
        adminsRecyclerView.setItemViewCacheSize(100);
       adminsRecyclerView.setAdapter(managersRecyclerViewAdapter);
       addToGrouptxt.setText("اضافه کردن مدیر گروه");
        return view;
    }

    @Override
    public void onRemoveAdminClicked(User user) {
        userId = user.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_alert_dialog, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        TextView dialog_title= dialogView.findViewById(R.id.dialog_title);
        TextView dialog_message= dialogView.findViewById(R.id.dialog_message);
        TextView dialog_submitBtn= dialogView.findViewById(R.id.dialog_submitBtn);
        TextView dialog_cancelBtn= dialogView.findViewById(R.id.dialog_cancelBtn);
        dialog_title.setText("حذف مدیر");
        dialog_message.setText("آیا مایل به حذف مدیر گروه هستید؟");
        dialog_submitBtn.setOnClickListener(v -> {
            clickOnRemoveAdminSubmit();
            alertDialog.dismiss();
        });
        dialog_cancelBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
//        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditGroupAdminsFragmentRemoveAdmin", "حذف مدیر", "بازگشت", "آیا مایل به حذف مدیر گروه هستید؟");
//        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @OnClick(R.id.addToGroupLayout)
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
}

