package rayan.rayanapp.Fragments;

import android.Manifest;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Contact;
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.Listeners.OnUserClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class EditGroupFragment extends Fragment implements OnUserClicked<User>, OnAdminClicked<User>{
    static final int PICK_CONTACT=1;
    private Group group;
    private String userId;
    private final String TAG = EditGroupFragment.class.getSimpleName();
    String cNumber;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static EditGroupFragment instance = null;
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

    @BindView(R.id.addManagerButton)
    ImageView addManagerButton;
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
        instance = this;
        if (getArguments() != null) {
            this.group = editGroupFragmentViewModel.getGroup(getArguments().getString("id"));
            int c = 0;
            for (int i = 0; i <= group.getAdmins().size() - 1; i++) {
                if (group.getAdmins().get(i).getId().equals(RayanApplication.getPref().getId())) {
                    c++;
                }
            }
            if (c > 0)
                RayanApplication.getPref().setIsGroupAdminKey(true);
            else RayanApplication.getPref().setIsGroupAdminKey(false);
            editGroupFragmentViewModel.getGroupLive(getArguments().getString("id")).observe(this, group1 -> {
                this.group = group1;
                usersRecyclerViewAdapter.setItems(group1.getHumanUsers());
                managersRecyclerViewAdapter.setItems(group1.getAdmins());
                devicesRecyclerViewAdapter.setItems(group1.getDevices());
            });
        }
        usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(getActivity());
        usersRecyclerViewAdapter.setListener(this);
        managersRecyclerViewAdapter = new AdminsRecyclerViewAdapter(getActivity());
        managersRecyclerViewAdapter.setListener(this);
        devicesRecyclerViewAdapter = new GroupDevicesRecyclerViewAdapter(getActivity(), new ArrayList<>());
        setHasOptionsMenu(true);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.leaveGroup){
            clickOnLeaveGroup();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_group_menu, menu);  // Use filter.xml from step 1
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
//                    if(resultCode==RESULT_OK)
//                    {

                        if(requestCode == REQUEST_CODE_PICK_CONTACT  )
                        {

                            Bundle bundle =  data.getExtras();
                            ArrayList<String> contacts = bundle.getStringArrayList("result");
                            for (int a = 0; a<contacts.size();a++){
                                contacts.set(a, contacts.get(a).split(";")[1].replaceAll("\\s+", "").replace("+98", "0"));
                            }
                            Log.e("////////////" ," ///////////: " + contacts);

                            editGroupFragmentViewModel.addUserByMobile(contacts, group.getId()).observe(getActivity(), baseResponse -> {
                                if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
                                    // TODO: 2/23/2019 user not found or nouser??
                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"کاربری با این شماره وجود ندارد");
                                }
                                else if (baseResponse.getStatus().getCode().equals("403") && baseResponse.getData().getMessage().equals("forbidden")){
                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شما مجاز به اضافه کردن کاربر نیستید");
                                }
                                else if (baseResponse.getStatus().getCode().equals("400") && baseResponse.getData().getMessage().equals("Repeated")){
                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"این کاربر هم‌اکنون عضو گروه است");
                                }
                                else if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nogroup")){
                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"این گروه وجود ندارد");
                                }
                                else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter mobiles")){
                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شماره تلفن ها را وارد کنید");
                                }
                                else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter group_id")){
                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شماره گروه را وارد کنید");
                                }
                                else if (baseResponse.getStatus().getCode().equals("200")){
                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"کاربر با موفقیت اضافه شد");
                                    editGroupFragmentViewModel.getGroups();
                                }
                                else
                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
                            });
                        }
                    }
//                    Uri contactData = data.getData();
//                    Cursor c =  getActivity().managedQuery(contactData, null, null, null, null);
//                    if (c.moveToFirst()) {
//                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
//                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//                        if (hasPhone.equalsIgnoreCase("1")) {
//                            Cursor phones = getActivity().getContentResolver().query(
//                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
//                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
//                                    null, null);
//                            phones.moveToFirst();
//                            cNumber = phones.getString(phones.getColumnIndex("data1"));
//                            cNumber = cNumber.trim();
//                            while (cNumber.contains(" "))
//                                cNumber = cNumber.replace(" ", "");
//                            cNumber = cNumber.replace("+98","0");
//                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                            Contact contact = new Contact();
//                            contact.setName(name);
//                            contact.setNumbers(cNumber);
//                            editGroupFragmentViewModel.addUserByMobile(cNumber, group.getId()).observe(getActivity(), baseResponse -> {
//                                if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
//                                    // TODO: 2/23/2019 user not found or nouser??
//                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"کاربری با این شماره وجود ندارد");
//                                }
//                                else if (baseResponse.getStatus().getCode().equals("403") && baseResponse.getData().getMessage().equals("forbidden")){
//                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شما مجاز به اضافه کردن کاربر نیستید");
//                                }
//                                else if (baseResponse.getStatus().getCode().equals("400") && baseResponse.getData().getMessage().equals("Repeated")){
//                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"این کاربر هم‌اکنون عضو گروه است");
//                                }
//                                else if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nogroup")){
//                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"این گروه وجود ندارد");
//                                }
//                                else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter mobiles")){
//                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شماره تلفن ها را وارد کنید");
//                                }
//                                else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter group_id")){
//                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شماره گروه را وارد کنید");
//                                }
//                                else if (baseResponse.getStatus().getCode().equals("200")){
//                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"کاربر با موفقیت اضافه شد");
//                                    editGroupFragmentViewModel.getGroups();
//                                }
//                                else
//                                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
//                            });
//                        }
//                    }
//                }
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
                if (name.getText().toString().equals(group.getName())){
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

    public static final int REQUEST_CODE_PICK_CONTACT = 1;
    public static final int  MAX_PICK_CONTACT= 10;
    @OnClick(R.id.addUserButton)
    void addUser(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
//            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//            startActivityForResult(intent, PICK_CONTACT);
            Intent phonebookIntent = new Intent("intent.action.INTERACTION_TOPMENU");
            phonebookIntent.putExtra("additional", "phone-multi");
            phonebookIntent.putExtra("maxRecipientCount", MAX_PICK_CONTACT);
            phonebookIntent.putExtra("FromMMS", true);
            startActivityForResult(phonebookIntent, REQUEST_CODE_PICK_CONTACT);
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
        userId=user.getId();
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditGroupFragment1","حذف کاربر", "بازگشت", "آیا مایل به حذف کاربر هستید؟");
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @OnClick(R.id.saveGroupName)
    void saveGroupName(){
        editGroupFragmentViewModel.editGroup(name.getText().toString(),group.getId()).observe(this, baseResponse -> {
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("Invalid group_id")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شماره گروه نادرست است");
            }
            else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter group_id")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شماره گروه را وارد کنید");
            }
            else if (baseResponse.getStatus().getCode().equals("200")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"گروه با موفقیت اصلاح شد");
                editGroupFragmentViewModel.getGroups();
                saveGroupName.setVisibility(View.INVISIBLE);
            }
            else
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
        });
    }

    @OnClick(R.id.leaveGroup_btn)
    void clickOnLeaveGroup(){
        userId=RayanApplication.getPref().getId();
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditGroupFragment3","ترک گروه", "بازگشت", "آیا مایل به ترک گروه هستید؟" );
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @Override
    public void onRemoveAdminClicked(User user) {
        userId=user.getId();
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditGroupFragment2","حذف مدیر", "بازگشت", "آیا مایل به حذف مدیر گروه هستید؟");
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void clickOnRemoveUserSubmit() {
        editGroupFragmentViewModel.deleteUser(userId, group.getId()).observe(this, baseResponse -> {
            Log.e("remove user code",baseResponse.getStatus().getCode());
//            Toast.makeText(getActivity(), "remove user code"+baseResponse.getStatus().getCode()+" "+ baseResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nouser")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"این کاربر وجود ندارد");
            }
            else if (baseResponse.getStatus().getCode().equals("404")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"حذف این کاربر امکان پذیر نیست");
            }
            else if (baseResponse.getStatus().getCode().equals("403")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شما قادر به حذف این کاربر نیستید");
            }
            else if (baseResponse.getStatus().getCode().equals("204")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"کاربر با موفقیت از گروه حذف شد");
//                ((DoneWithFragment)getActivity()).operationDone();
                editGroupFragmentViewModel.getGroups();
            }
            else if (baseResponse.getStatus().getCode().equals("200")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"کاربر با موفقیت از گروه حذف شد");
//                ((DoneWithFragment)getActivity()).operationDone();
                editGroupFragmentViewModel.getGroups();
            }
            else
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
        });
    }

    public void clickOnRemoveAdminSubmit() {
        editGroupFragmentViewModel.deleteAdmin(userId, group.getId()).observe(this, baseResponse -> {
            Log.e("remove admin code",baseResponse.getStatus().getCode());
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nouser")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"این کاربر وجود ندارد");
            }
            else if (baseResponse.getStatus().getCode().equals("200")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مدیر با موفقیت حذف شد");
                editGroupFragmentViewModel.getGroups();
            }
            else
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
        });
    }
    public void clickOnLeaveGroupSubmit(){
        editGroupFragmentViewModel.deleteUser(userId, group.getId()).observe(this, baseResponse -> {
            Log.e("remove user code",baseResponse.getStatus().getCode());
//            Toast.makeText(getActivity(), "remove user code"+baseResponse.getStatus().getCode()+" "+ baseResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nouser")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"این کاربر وجود ندارد");
            }
            else if (baseResponse.getStatus().getCode().equals("404")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"حذف این کاربر امکان پذیر نیست");
            }
            else if (baseResponse.getStatus().getCode().equals("403")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شما قادر به حذف این کاربر نیستید");
            }
            else if (baseResponse.getStatus().getCode().equals("204")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شما با موفقیت از گروه خارج شدید");
//                ((DoneWithFragment)getActivity()).operationDone();
                getActivity().onBackPressed();
            }
            else if (baseResponse.getStatus().getCode().equals("200")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شما با موفقیت از گروه خارج شدید");
//                ((DoneWithFragment)getActivity()).operationDone();
                getActivity().onBackPressed();
            }
            else
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
        });
    }
    public static EditGroupFragment getInstance() {
        return instance;
    }

}
