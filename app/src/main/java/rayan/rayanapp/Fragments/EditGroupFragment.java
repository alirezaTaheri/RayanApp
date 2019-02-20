package rayan.rayanapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Adapters.recyclerView.AdminsRecyclerViewAdapter;
import rayan.rayanapp.Adapters.recyclerView.GroupDevicesRecyclerViewAdapter;
import rayan.rayanapp.Adapters.recyclerView.UsersRecyclerViewAdapter;
import rayan.rayanapp.Data.Contact;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.Listeners.OnUserClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class EditGroupFragment extends Fragment implements OnUserClicked<User>, OnAdminClicked<User> {

    static final int PICK_CONTACT=1;
    private final String TAG = EditGroupFragment.class.getSimpleName();
    String cNumber;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @BindView(R.id.managersRecyclerView)
    RecyclerView managersRecyclerView;
    @BindView(R.id.usersRecyclerView)
    RecyclerView usersRecyclerView;
    @BindView(R.id.devicesRecyclerView)
    RecyclerView devicesRecyclerView;
    @BindView(R.id.name)
    EditText name;
    UsersRecyclerViewAdapter usersRecyclerViewAdapter;
    AdminsRecyclerViewAdapter managersRecyclerViewAdapter;
    GroupDevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    EditGroupFragmentViewModel editGroupFragmentViewModel;
    private Group group;
    @BindView(R.id.addManagerButton)
    ImageView addManagerButton;
    private String groupName;
    @BindView(R.id.saveGroupName)
    TextView saveGroupName;
    public EditGroupFragment() {
    }

    public static EditGroupFragment newInstance(Group group) {
        EditGroupFragment fragment = new EditGroupFragment();
        Bundle args = new Bundle();
        args.putString("id", group.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editGroupFragmentViewModel = ViewModelProviders.of(this).get(EditGroupFragmentViewModel.class);
        if (getArguments() != null) {
            this.group = editGroupFragmentViewModel.getGroup(getArguments().getString("id"));
            editGroupFragmentViewModel.getGroupLive(getArguments().getString("id")).observe(this, group1 -> {
                this.group = group1;
                usersRecyclerViewAdapter.setItems(group1.getHumanUsers());
                managersRecyclerViewAdapter.setItems(group1.getAdmins());
                devicesRecyclerViewAdapter.setItems(group1.getDevices());
            });
        }
        Log.e(TAG , "Group Is : " + group);
        groupName = group.getName();
        usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(getActivity());
        usersRecyclerViewAdapter.setListener(this);
        managersRecyclerViewAdapter = new AdminsRecyclerViewAdapter(getActivity());
        managersRecyclerViewAdapter.setListener(this);
        devicesRecyclerViewAdapter = new GroupDevicesRecyclerViewAdapter(getActivity(), new ArrayList<>());

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            } else {
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_edit_group, container, false);
        ButterKnife.bind(this, view);
        getContactPermission();
        managersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        managersRecyclerView.setAdapter(managersRecyclerViewAdapter);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersRecyclerView.setAdapter(usersRecyclerViewAdapter);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        devicesRecyclerView.setAdapter(devicesRecyclerViewAdapter);
        init(group);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (name.getText().toString().equals(groupName)){
                    saveGroupName.setVisibility(View.INVISIBLE);
                }
                else{
                    saveGroupName.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

    @OnClick(R.id.addUserButton)
    void addUser(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }
        else getContactPermission();

    }

    @OnClick(R.id.addManagerButton)
    void addManager(){
        UsersListDialogFragment usersListDialogFragment = UsersListDialogFragment.newInstance(group);
        usersListDialogFragment.show(getActivity().getSupportFragmentManager(), "usersList");
    }

    public void init(Group group){
        name.setText(group.getName());
        groupName = group.getName();
        devicesRecyclerViewAdapter.setItems(group.getDevices());
        usersRecyclerViewAdapter.setItems(group.getHumanUsers());
        managersRecyclerViewAdapter.setItems(group.getAdmins());
    }

    public void getContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
    }
}
    @Override
    public void onRemoveUserClicked(User user) {
        RemoveUserButtomSheetFragment bottomSheetFragment = new RemoveUserButtomSheetFragment().newInstance(user.getId(), group.getId());
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @OnClick(R.id.saveGroupName)
    void saveGroupName(){
        editGroupFragmentViewModel.editGroup(name.getText().toString(),group.getId()).observe(this, baseResponse -> {
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
                Toast.makeText(getActivity(), "این گروه وجود ندارد", Toast.LENGTH_SHORT).show();
            }
            else if (baseResponse.getStatus().getCode().equals("403") && baseResponse.getData().getMessage().equals("Repeated")){
                Toast.makeText(getActivity(), "شما قادر به اصلاح این گروه نیستید", Toast.LENGTH_SHORT).show();
            }
            else if (baseResponse.getStatus().getCode().equals("200")){
                Toast.makeText(getActivity(), "گروه با موفقیت اصلاح شد", Toast.LENGTH_SHORT).show();
                editGroupFragmentViewModel.getGroups();
                saveGroupName.setVisibility(View.INVISIBLE);
            }
            else
                Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRemoveAdminClicked(User user) {
        RemoveAdminButtomSheetFragment bottomSheetFragment = new RemoveAdminButtomSheetFragment().newInstance(user.getId(), group.getId());
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
}
