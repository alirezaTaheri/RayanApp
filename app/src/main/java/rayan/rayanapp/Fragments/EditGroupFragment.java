package rayan.rayanapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.GroupsActivity;
import rayan.rayanapp.Adapters.recyclerView.AdminsRecyclerViewAdapter;
import rayan.rayanapp.Adapters.recyclerView.GroupDevicesRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Contact;
import rayan.rayanapp.Listeners.OnAdminClicked;
import rayan.rayanapp.Listeners.OnToolbarNameChange;
import rayan.rayanapp.Listeners.OnUserClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class EditGroupFragment extends Fragment {
    OnToolbarNameChange onToolbarNameChange;
    static final int PICK_CONTACT = 1;
    public static Group group;
    private String userId;
    public static List<User> admins;
    public static List<User> humanUsers;
    ArrayList<String> adminsUserNames = new ArrayList<>();
    private final String TAG = EditGroupFragment.class.getSimpleName();
    String cNumber;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static EditGroupFragment instance = null;
    @BindView(R.id.managersRecyclerView)
    RecyclerView managersRecyclerView;
    @BindView(R.id.devicesRecyclerView)
    RecyclerView devicesRecyclerView;
    AdminsRecyclerViewAdapter managersRecyclerViewAdapter;
    GroupDevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    EditGroupFragmentViewModel editGroupFragmentViewModel;
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
                admins = group1.getAdmins();
                humanUsers = group1.getHumanUsers();
                for (int i = 0; i <= admins.size() - 1; i++) {
                    adminsUserNames.add(admins.get(i).getUsername());
                }
                Toast.makeText(getContext(), adminsUserNames.get(0), Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    for (int i = 0; i <= admins.size() - 1; i++) {
                        admins.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(admins.get(i).getUsername(), getActivity()));
                        admins.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(admins.get(i).getUsername(), getActivity()));
                    }
                    for (int i = 0; i <= humanUsers.size() - 1; i++) {
                        humanUsers.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(humanUsers.get(i).getUsername(), getActivity()));
                        humanUsers.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(humanUsers.get(i).getUsername(), getActivity()));
                    }
                } else getContactPermission();
                managersRecyclerViewAdapter.setItems(humanUsers);
                devicesRecyclerViewAdapter.setItems(group1.getDevices());
            });
        }

        managersRecyclerViewAdapter = new AdminsRecyclerViewAdapter(getActivity(),adminsUserNames,"");
        devicesRecyclerViewAdapter = new GroupDevicesRecyclerViewAdapter(getActivity(), new ArrayList<>());
        setHasOptionsMenu(true);
        onToolbarNameChange=(OnToolbarNameChange)getActivity();
        onToolbarNameChange.toolbarNameChanged(group.getName());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.editGroupBasic:
                clickOnEditGroupButton.OnEditGroupButtonClicked();
                break;
            case R.id.leaveGroup :
                clickOnLeaveGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.leave_menu, menu);
        MenuItem editGroupBasic = menu.findItem(R.id.editGroupBasic);
        editGroupBasic.setVisible(RayanApplication.getPref().getIsGroupAdminKey());
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_group, container, false);
        ButterKnife.bind(this, view);
        getContactPermission();
        managersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        managersRecyclerView.setAdapter(managersRecyclerViewAdapter);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        devicesRecyclerView.setAdapter(devicesRecyclerViewAdapter);
        init(group);
        return view;
    }

    public static final int REQUEST_CODE_PICK_CONTACT = 1;
    public static final int MAX_PICK_CONTACT = 10;

    @OnClick(R.id.addUserToGroup)
    void addUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }
        else getContactPermission();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//            startActivityForResult(intent, PICK_CONTACT);
//            Intent phonebookIntent = new Intent("intent.action.INTERACTION_TOPMENU");
//            phonebookIntent.putExtra("additional", "phone-multi");
//            phonebookIntent.putExtra("maxRecipientCount", MAX_PICK_CONTACT);
//            phonebookIntent.putExtra("FromMMS", true);
//            //startActivityForResult(Intent.createChooser(phonebookIntent,""), REQUEST_CODE_PICK_CONTACT);
//            //its important:
//            startActivityForResult(phonebookIntent, REQUEST_CODE_PICK_CONTACT);

//            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//            startActivityForResult(intent, PICK_CONTACT);
//            Intent phonebookIntent = new Intent("intent.action.INTERACTION_TOPMENU");
//            phonebookIntent.putExtra("additional", "phone-multi");
//            phonebookIntent.putExtra("maxRecipientCount", MAX_PICK_CONTACT);
//            phonebookIntent.putExtra("FromMMS", true);
//            startActivityForResult(phonebookIntent, REQUEST_CODE_PICK_CONTACT);
//            PhoneContactListBottomSheetFragment phoneContactListFragment =new PhoneContactListBottomSheetFragment().newInstance("EditGroupFragment");
//            phoneContactListFragment.show(getActivity().getSupportFragmentManager(), phoneContactListFragment.getTag());

//        } else getContactPermission();

    }
    public void init(Group group) {
        devicesRecyclerViewAdapter.setItems(group.getDevices());
        managersRecyclerViewAdapter.setItems(group.getAdmins());
    }

    //    @OnClick(R.id.leaveGroup_btn)
    void clickOnLeaveGroup() {
        userId = RayanApplication.getPref().getId();
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditGroupFragment3", "ترک گروه", "بازگشت", "آیا مایل به ترک گروه هستید؟");
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void clickOnRemoveUserSubmit() {
        editGroupFragmentViewModel.deleteUser(userId, group.getId()).observe(this, baseResponse -> {
            Log.e("remove user code", baseResponse.getStatus().getCode());
//            Toast.makeText(getActivity(), "remove user code"+baseResponse.getStatus().getCode()+" "+ baseResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nouser")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "این کاربر وجود ندارد");
            } else if (baseResponse.getStatus().getCode().equals("404")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "حذف این کاربر امکان پذیر نیست");
            } else if (baseResponse.getStatus().getCode().equals("403")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شما قادر به حذف این کاربر نیستید");
            } else if (baseResponse.getStatus().getCode().equals("204")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "کاربر با موفقیت از گروه حذف شد");
//                ((DoneWithFragment)getActivity()).operationDone();
                editGroupFragmentViewModel.getGroups();
            } else if (baseResponse.getStatus().getCode().equals("200")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "کاربر با موفقیت از گروه حذف شد");
//                ((DoneWithFragment)getActivity()).operationDone();
                editGroupFragmentViewModel.getGroups();
            } else
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "مشکلی وجود دارد");
        });
    }

    public void clickOnLeaveGroupSubmit() {
        if (adminsUserNames.contains(RayanApplication.getPref().getUsername())) {
            if (admins.size() == 1) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "هر گروه نیاز به یک مدیر دارد");
            } else {
                doLeaveGroup(userId, group.getId());
            }
        } else {
            doLeaveGroup(userId, group.getId());
        }
    }

    public static EditGroupFragment getInstance() {
        return instance;
    }

    public void doLeaveGroup(String id, String groupId) {
        editGroupFragmentViewModel.deleteUser(id, groupId).observe(this, baseResponse -> {
            Log.e("remove user code", baseResponse.getStatus().getCode());
//            Toast.makeText(getActivity(), "remove user code"+baseResponse.getStatus().getCode()+" "+ baseResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nouser")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "این کاربر وجود ندارد");
            } else if (baseResponse.getStatus().getCode().equals("404")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "حذف این کاربر امکان پذیر نیست");
            } else if (baseResponse.getStatus().getCode().equals("403")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شما قادر به حذف این کاربر نیستید");
            } else if (baseResponse.getStatus().getCode().equals("204")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شما با موفقیت از گروه خارج شدید");
//                ((DoneWithFragment)getActivity()).operationDone();
                getActivity().onBackPressed();
            } else if (baseResponse.getStatus().getCode().equals("200")) {
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شما با موفقیت از گروه خارج شدید");
//                ((DoneWithFragment)getActivity()).operationDone();
                getActivity().onBackPressed();
            } else
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "مشکلی وجود دارد");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        onToolbarNameChange.toolbarNameChanged(group.getName());
    }
//    public void doAddUserFromPhone(ArrayList<PhoneContact> selectedContacts){
//        ArrayList<String> contacts=new ArrayList<>();
//        for (int i=0;i<=selectedContacts.size()-1;i++){
//            contacts.add(selectedContacts.get(i).getNumbers());
//        }
//        editGroupFragmentViewModel.addUserByMobile(contacts, group.getId()).observe(getActivity(), baseResponse -> {
//            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")) {
//                // TODO: 2/23/2019 user not found or nouser??
//                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "کاربری با این شماره وجود ندارد");
//            } else if (baseResponse.getStatus().getCode().equals("403") && baseResponse.getData().getMessage().equals("forbidden")) {
//                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شما مجاز به اضافه کردن کاربر نیستید");
//            } else if (baseResponse.getStatus().getCode().equals("400") && baseResponse.getData().getMessage().equals("Repeated")) {
//                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "این کاربر هم‌اکنون عضو گروه است");
//            } else if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nogroup")) {
//                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "این گروه وجود ندارد");
//            } else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter mobiles")) {
//                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شماره تلفن ها را وارد کنید");
//            } else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter group_id")) {
//                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "شماره گروه را وارد کنید");
//            } else if (baseResponse.getStatus().getCode().equals("200")) {
//                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "کاربر با موفقیت اضافه شد");
//                editGroupFragmentViewModel.getGroups();
//            } else
//                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "مشکلی وجود دارد");
//        });
//
//    }
    public interface ClickOnEditGroupButton{
        void OnEditGroupButtonClicked();
    }
    ClickOnEditGroupButton clickOnEditGroupButton;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ClickOnEditGroupButton) {
            clickOnEditGroupButton = (ClickOnEditGroupButton) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clickOnEditGroupButton = null;
    }

    public void getContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (int i = 0; i <= admins.size() - 1; i++) {
                        admins.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(admins.get(i).getUsername(), getActivity()));
                        admins.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(admins.get(i).getUsername(), getActivity()));
                    }
                    for (int i = 0; i <= humanUsers.size() - 1; i++) {
                        humanUsers.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(humanUsers.get(i).getUsername(), getActivity()));
                        humanUsers.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(humanUsers.get(i).getUsername(), getActivity()));
                    }
                managersRecyclerViewAdapter.setItems(humanUsers);
                }
                else
                {
                    Toast.makeText(getContext(), "اپلیکیشن به اجازه دسترسی به کانتکت ها نیاز دارد", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
