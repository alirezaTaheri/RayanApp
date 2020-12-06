package rayan.rayanapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import rayan.rayanapp.Adapters.recyclerView.GroupUsersRecyclerViewAdapter;
import rayan.rayanapp.Data.Contact;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class EditGroupUsersFragment extends Fragment implements OnAdminClicked<User> {
    private List<User> users;
    //OnToolbarNameChange onToolbarNameChange;
    private String userId;
    static final int PICK_CONTACT = 1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    Group group;
    ArrayList<String> userNames = new ArrayList<>();
    private EditGroupFragmentViewModel editGroupFragmentViewModel;
    GroupUsersRecyclerViewAdapter usersRecyclerViewAdapter;
    @BindView(R.id.recyclerView)
    RecyclerView usersRecyclerView;
    @BindView(R.id.addToGrouptxt)
    TextView addToGrouptxt;
    @BindView(R.id.addToGroupLayout)
    LinearLayout addToGroupLayout;
    @BindView(R.id.addToGroupLine)
    View addToGroupLine;
    Activity activity;

    public static EditGroupUsersFragment newInstance(String id) {
        EditGroupUsersFragment fragment = new EditGroupUsersFragment();
        Bundle b = new Bundle();
        b.putString("id", id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editGroupFragmentViewModel = ViewModelProviders.of(this).get(EditGroupFragmentViewModel.class);
        editGroupFragmentViewModel.getGroupLive(getArguments()
                .getString("id")).observe(this, group1 -> {
            this.group = group1;
        });
        editGroupFragmentViewModel.getAllUsersInGroupLive(getArguments().getString("id")).observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                EditGroupUsersFragment.this.users = users;
                for (int i = 0; i <= users.size() - 1; i++) {
                    userNames.add(users.get(i).getUsername());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                    for (int i = 0; i <= users.size() - 1; i++) {
                        users.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(users.get(i).getUsername(), getActivity()));
                        users.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(users.get(i).getUsername(), getActivity()));
                    }
                usersRecyclerViewAdapter.setItems(users);
            }
        });
        usersRecyclerViewAdapter = new GroupUsersRecyclerViewAdapter(getActivity(),userNames,"admins_users");
        usersRecyclerViewAdapter.setListener(this);
        ((GroupsActivity) getActivity()).toolbarNameChanged("کاربران گروه");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_group_admins, container, false);
        ButterKnife.bind(this, view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersRecyclerView.setItemViewCacheSize(100);
        usersRecyclerView.setAdapter(usersRecyclerViewAdapter);
        addToGrouptxt.setText("اضافه کردن کاربر گروه");
        return view;
    }

    @Override
    public void onRemoveAdminClicked(User item) {
        Log.e("deleting user: " ,"deleting this: " + item);
        userId = item.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_alert_dialog, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        TextView dialog_title= dialogView.findViewById(R.id.dialog_title);
        TextView dialog_message= dialogView.findViewById(R.id.dialog_message);
        TextView dialog_submitBtn= dialogView.findViewById(R.id.dialog_submitBtn);
        TextView dialog_cancelBtn= dialogView.findViewById(R.id.dialog_cancelBtn);
        dialog_title.setText("حذف کاربر");
        dialog_message.setText("آیا مایل به حذف کاربر گروه هستید؟");
        dialog_submitBtn.setOnClickListener(v -> {
            clickOnRemoveUserSubmit();
            alertDialog.dismiss();
        });
        dialog_cancelBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
//        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditGroupUsersFragmentRemoveUser", "حذف کاربر", "بازگشت", "آیا مایل به حذف کاربر گروه هستید؟");
//        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
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
                editGroupFragmentViewModel.getGroups();
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "کاربر با موفقیت حذف گردید");
            } else if (baseResponse.getStatus().getCode().equals("200")) {
                editGroupFragmentViewModel.getGroups();
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
    @OnClick(R.id.addToGroupLayout)
    void addUserToGroup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }
        else getContactPermission();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String cNumber;
        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  getActivity().managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getActivity().getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                            cNumber = cNumber.trim();
                            while (cNumber.contains(" "))
                                cNumber = cNumber.replace(" ", "");
                            cNumber = cNumber.replace("+98","0");
                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            Contact contact = new Contact();
                            contact.setName(name);
                            contact.setNumbers(cNumber);
                            editGroupFragmentViewModel.addUserByMobile(cNumber, group.getId()).observe(getActivity(), baseResponse -> {
                                if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
                                    Toast.makeText(getActivity(), "کاربری با این شماره وجود ندارد", Toast.LENGTH_SHORT).show();
                                }
                                else if (baseResponse.getStatus().getCode().equals("400") && baseResponse.getData().getMessage().equals("Repeated")){
                                    Toast.makeText(getActivity(), "این کاربر هم‌اکنون عضو گروه است", Toast.LENGTH_SHORT).show();
                                }
                                else if (baseResponse.getStatus().getCode().equals("200")){
                                    Toast.makeText(getActivity(), "کاربر با موفقیت اضافه شد", Toast.LENGTH_SHORT).show();
                                    editGroupFragmentViewModel.getGroups();
                                }
                                else
                                    Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }
                break;

        }
    }
    public void getContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
}

