package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.GroupsActivity;
import rayan.rayanapp.Listeners.OnToolbarNameChange;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class EditGroupFragment2 extends Fragment{
    private Group group;
   // OnToolbarNameChange onToolbarNameChange;
    private String groupId;
    private List<User> admins;
    private List<User> humanUsers;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.saveGroupName)
    TextView saveGroupName;
    @BindView(R.id.adminCount)
    TextView adminCount;
    @BindView(R.id.userCount)
    TextView userCount;
   private EditGroupFragmentViewModel editGroupFragmentViewModel;

    public static EditGroupFragment2 newInstance() {
        EditGroupFragment2 fragment = new EditGroupFragment2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editGroupFragmentViewModel = ViewModelProviders.of(this).get(EditGroupFragmentViewModel.class);
        admins=EditGroupFragment.admins;
        humanUsers=EditGroupFragment.humanUsers;
        group=EditGroupFragment.group;
        groupId=group.getId();
       // Toast.makeText(getContext(), String.valueOf(admins.size()), Toast.LENGTH_SHORT).show();
       // Toast.makeText(getContext(), String.valueOf(humanUsers.size()), Toast.LENGTH_SHORT).show();

//        editGroupFragmentViewModel.getGroupLive(group.getId()).observe(this, group1 -> {
//            this.group = group1;
//            admins = group1.getAdmins();
//            humanUsers = group1.getHumanUsers();
//
//        });
//        onToolbarNameChange=(OnToolbarNameChange)getActivity();
//        onToolbarNameChange.toolbarNameChanged(group.getName());
        ((GroupsActivity) getActivity()).toolbarNameChanged("مدیریت گروه");
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
                if (name.getText().toString().equals(group.getName())) {
                    saveGroupName.setVisibility(View.INVISIBLE);
                } else {
                    saveGroupName.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        adminCount.setText(String.valueOf(admins.size()));
        userCount.setText(String.valueOf(humanUsers.size()));
        return view;
    }

    @OnClick(R.id.deleteGroup)
    void clickOndeleteGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_alert_dialog, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        TextView dialog_title= dialogView.findViewById(R.id.dialog_title);
        TextView dialog_message= dialogView.findViewById(R.id.dialog_message);
        TextView dialog_submitBtn= dialogView.findViewById(R.id.dialog_submitBtn);
        TextView dialog_cancelBtn= dialogView.findViewById(R.id.dialog_cancelBtn);
        dialog_title.setText("حذف گروه");
        dialog_message.setText("آیا مایل به حذف گروه هستید؟");
        dialog_submitBtn.setOnClickListener(v -> {
            clickOnDeleteGroupSubmit();
            alertDialog.dismiss();
        });
        dialog_cancelBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
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
        editGroupFragmentViewModel.deleteGroup(groupId).observe(this, baseResponse -> {
            Log.e("baseResponse", baseResponse.getStatus().getCode());
            if (baseResponse.getStatus().getCode().equals("404")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "این گروه وجود ندارد");
            } else if (baseResponse.getStatus().getCode().equals("403")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شما قادر به حذف این گروه نیستید");
            } else if (baseResponse.getStatus().getCode().equals("204")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "گروه با موفقیت حذف شد");
                Intent intent=new Intent(getContext(), GroupsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                startActivity(intent);
            } else
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "مشکلی وجود دارد");
        });
    }

    @OnClick(R.id.adminsLayout)
    void adminsLayoutClicked(){
        clickOnButton.OnAdminButtonClicked();
    }
    @OnClick(R.id.usersLayout)
    void userslayoutClicked(){
        clickOnButton.onUserButtonClicked();
    }
    public interface ClickOnButton{
        void OnAdminButtonClicked();
        void onUserButtonClicked();
    }
    ClickOnButton clickOnButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ClickOnButton) {
            clickOnButton = (ClickOnButton) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clickOnButton = null;
    }
    @Override
    public void onResume() {
        super.onResume();
//        onToolbarNameChange.toolbarNameChanged(group.getName());
        ((GroupsActivity) getActivity()).toolbarNameChanged("مدیریت گروه");
    }

}
