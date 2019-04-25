package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.GroupsActivity;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class EditGroupFragment2 extends Fragment {
    private Group group;
    String groupId;
    private List<User> admins;
    //static Gson gson;
    private List<User> humanUsers;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.saveGroupName)
    TextView saveGroupName;

    @BindView(R.id.adminCount)
    TextView adminCount;

    @BindView(R.id.userCount)
    TextView userCount;
    EditGroupFragmentViewModel editGroupFragmentViewModel;

    public static EditGroupFragment2 newInstance() {
        EditGroupFragment2 fragment = new EditGroupFragment2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editGroupFragmentViewModel = ViewModelProviders.of(this).get(EditGroupFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_group_2, container, false);
        ButterKnife.bind(this, view);
        name.setText(EditGroupFragment.group.getName());
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (name.getText().toString().equals(EditGroupFragment.group.getName())) {
                    saveGroupName.setVisibility(View.INVISIBLE);
                } else {
                    saveGroupName.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        adminCount.setText(String.valueOf(EditGroupFragment.admins.size()));
        userCount.setText(String.valueOf(EditGroupFragment.humanUsers.size()));
        return view;
    }

    @OnClick(R.id.deleteGroup)
    void clickOndeleteGroup() {
        groupId = EditGroupFragment.group.getId();
        editGroupFragmentViewModel.deleteGroup(groupId).observe(this, baseResponse -> {
            Log.e("baseResponse", baseResponse.getStatus().getCode());
            if (baseResponse.getStatus().getCode().equals("404")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "این گروه وجود ندارد");
            } else if (baseResponse.getStatus().getCode().equals("403")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شما قادر به حذف این گروه نیستید");

            } else if (baseResponse.getStatus().getCode().equals("204")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "گروه با موفقیت حذف شد");
                startActivity(new Intent(getContext(), GroupsActivity.class));
                editGroupFragmentViewModel.getGroups();
            } else
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "مشکلی وجود دارد");
        });
//        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditGroupFragment2_DeleteGroup", "حذف گروه", "بازگشت", "آیا مایل به حذف گروه هستید؟");
//        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @OnClick(R.id.saveGroupName)
    void saveGroupName() {
        editGroupFragmentViewModel.editGroup(name.getText().toString(), EditGroupFragment.group.getId()).observe(this, baseResponse -> {
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("Invalid group_id")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شماره گروه نادرست است");
            } else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter group_id")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شماره گروه را وارد کنید");
            } else if (baseResponse.getStatus().getCode().equals("200")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "گروه با موفقیت اصلاح شد");
                editGroupFragmentViewModel.getGroups();
                //  saveGroupName.setVisibility(View.INVISIBLE);
            } else
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "مشکلی وجود دارد");
        });
    }

    public void clickOnDeleteGroupSubmit(){

    }
}
