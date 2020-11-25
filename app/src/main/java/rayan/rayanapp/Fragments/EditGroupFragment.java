package rayan.rayanapp.Fragments;

import android.Manifest;
import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class EditGroupFragment extends Fragment {
   // OnToolbarNameChange onToolbarNameChange;
    static final int PICK_CONTACT = 1;
    public Group group;
    private String userId;
    public List<User> admins;
    public List<User> users;
    ArrayList<String> adminsUserNames = new ArrayList<>();
    ArrayList<String> usersUserName = new ArrayList<>();
    private final String TAG = EditGroupFragment.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @BindView(R.id.managersRecyclerView)
    RecyclerView usersRecyclerView;
    @BindView(R.id.devicesRecyclerView)
    RecyclerView devicesRecyclerView;
    AdminsRecyclerViewAdapter usersRecyclerViewAdapter;
    GroupDevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    EditGroupFragmentViewModel editGroupFragmentViewModel;
    boolean groupAdmin = false;
    MenuItem editGroupBasic;
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
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED))
            getContactPermission();
        editGroupFragmentViewModel = ViewModelProviders.of(this).get(EditGroupFragmentViewModel.class);
        if (getArguments() != null) {
            editGroupFragmentViewModel.getAllDevicesInGroupLive(getArguments().getString("id")).observe(this, new Observer<List<Device>>() {
                @Override
                public void onChanged(@Nullable List<Device> devices) {
                    devicesRecyclerViewAdapter.setItems(devices);
                }
            });
            editGroupFragmentViewModel.getJustAdminsInGroup(getArguments().getString("id")).observe(this, new Observer<List<User>>() {
                @Override
                public void onChanged(@Nullable List<User> users) {
                    for (int a = 0; a<users.size();a++)
                        adminsUserNames.add(users.get(a).getUsername());
                }
            });
            editGroupFragmentViewModel.getAllUsersInGroupLive(getArguments().getString("id")).observe(this, new Observer<List<User>>() {
                @Override
                public void onChanged(@Nullable List<User> users) {
                    EditGroupFragment.this.users = users;
                    int temp = 0;
                    for (int i = 0; i <EditGroupFragment.this.users.size(); i++) {
                        usersUserName.add(EditGroupFragment.this.users.get(i).getUsername());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            EditGroupFragment.this.users.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(users.get(i).getUsername(), activity));
                            EditGroupFragment.this.users.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(users.get(i).getUsername(), activity));
                        }
                        if (users.get(i).getId().equals(RayanApplication.getPref().getId()) && users.get(i).getUserType().equals(AppConstants.ADMIN_TYPE)){
                            temp++;
                        }
                    }
                    if (temp > 0){
                        groupAdmin = true;
                    }
                        if (editGroupBasic != null)
                            editGroupBasic.setVisible(groupAdmin);
                    usersRecyclerViewAdapter.setItems(users);

                }
            });
            editGroupFragmentViewModel.getJustAdminsInGroup(getArguments().getString("id")).observe(this, new Observer<List<User>>() {
                @Override
                public void onChanged(@Nullable List<User> users) {
                    admins = users;
                }
            });
            editGroupFragmentViewModel.getGroupLive(getArguments().getString("id")).observe(this, group -> {
                this.group = group;
                ((GroupsActivity) activity).toolbarNameChanged(group.getName());
            });
        }
        usersRecyclerViewAdapter = new AdminsRecyclerViewAdapter(activity,usersUserName,"");
        devicesRecyclerViewAdapter = new GroupDevicesRecyclerViewAdapter(activity, new ArrayList<>());
        setHasOptionsMenu(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.editGroupBasic:
                clickOnEditGroupButton.OnEditGroupButtonClicked(group.getId());
                break;
            case R.id.leaveGroup :
                clickOnLeaveGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.leave_menu, menu);
        editGroupBasic = menu.findItem(R.id.editGroupBasic);
        editGroupBasic.setVisible(groupAdmin);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String cNumber;
        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {


                    Uri contactUri = data.getData();
                    String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContext().getContentResolver().query(contactUri, projection,
                            null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String number = cursor.getString(numberIndex);
                        Uri uri2 = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number.trim()));
                        Cursor cursor2 = getContext().getContentResolver().query(uri2, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
                        String contactName = null;
                        if(cursor2.moveToFirst()) {
                            contactName = cursor2.getString(cursor2.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                            while (number.contains(" "))
                                number = number.replace(" ", "");
                            number = number.replace("+98","0");
                            editGroupFragmentViewModel.addUserByMobile(number, group.getId()).observe(this, baseResponse -> {
                                if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
                                    Toast.makeText(activity, "کاربری با این شماره وجود ندارد", Toast.LENGTH_SHORT).show();
                                }
                                else if (baseResponse.getStatus().getCode().equals("400") && baseResponse.getData().getMessage().equals("Repeated")){
                                    Toast.makeText(activity, "این کاربر هم‌اکنون عضو گروه است", Toast.LENGTH_SHORT).show();
                                }
                                else if (baseResponse.getStatus().getCode().equals("200")){
                                    Toast.makeText(activity, "کاربر با موفقیت اضافه شد", Toast.LENGTH_SHORT).show();
                                    editGroupFragmentViewModel.getGroups();
                                }
                                else
                                    Toast.makeText(activity, "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
                            });
                        }
                        Log.e("lklklklklklklk", "name" + contactName);
                        cursor2.close();
                    }
                    cursor.close();


//                    Uri contactData = data.getData();
//                    Cursor c =  activity.managedQuery(contactData, null, null, null, null);
//                    if (c.moveToFirst()) {
//                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
//                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//                        if (hasPhone.equalsIgnoreCase("1")) {
//                            Cursor phones = activity.getContentResolver().query(
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
//                            editGroupFragmentViewModel.addUserByMobile(cNumber, group.getId()).observe(this, baseResponse -> {
//                                if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
//                                    Toast.makeText(activity, "کاربری با این شماره وجود ندارد", Toast.LENGTH_SHORT).show();
//                                }
//                                else if (baseResponse.getStatus().getCode().equals("400") && baseResponse.getData().getMessage().equals("Repeated")){
//                                    Toast.makeText(activity, "این کاربر هم‌اکنون عضو گروه است", Toast.LENGTH_SHORT).show();
//                                }
//                                else if (baseResponse.getStatus().getCode().equals("200")){
//                                    Toast.makeText(activity, "کاربر با موفقیت اضافه شد", Toast.LENGTH_SHORT).show();
//                                    editGroupFragmentViewModel.getGroups();
//                                }
//                                else
//                                    Toast.makeText(activity, "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
//                            });
//                        }
//                    }
                }
                break;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_group, container, false);
        ButterKnife.bind(this, view);
        getContactPermission();
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        usersRecyclerView.setItemViewCacheSize(100);
        usersRecyclerView.setAdapter(usersRecyclerViewAdapter);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        devicesRecyclerView.setAdapter(devicesRecyclerViewAdapter);
        return view;
    }

    @OnClick(R.id.addUserToGroup)
    void addUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
//            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//            startActivityForResult(intent, PICK_CONTACT);

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    12);
            Intent i=new Intent(Intent.ACTION_PICK);
            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(i, PICK_CONTACT);
        }
        else getContactPermission();
    }

    void clickOnLeaveGroup() {
        userId = RayanApplication.getPref().getId();
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditGroupFragment3", "ترک گروه", "بازگشت", "آیا مایل به ترک گروه هستید؟");
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void clickOnRemoveUserSubmit() {
        editGroupFragmentViewModel.deleteUser(userId, group.getId()).observe(this, baseResponse -> {
            Log.e("remove user code", baseResponse.getStatus().getCode());
//            Toast.makeText(activity, "remove user code"+baseResponse.getStatus().getCode()+" "+ baseResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nouser")) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "این کاربر وجود ندارد");
            } else if (baseResponse.getStatus().getCode().equals("404")) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "حذف این کاربر امکان پذیر نیست");
            } else if (baseResponse.getStatus().getCode().equals("403")) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "شما قادر به حذف این کاربر نیستید");
            } else if (baseResponse.getStatus().getCode().equals("204")) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "کاربر با موفقیت از گروه حذف شد");
//                ((DoneWithFragment)activity).operationDone();
                editGroupFragmentViewModel.getGroups();
            } else if (baseResponse.getStatus().getCode().equals("200")) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "کاربر با موفقیت از گروه حذف شد");
//                ((DoneWithFragment)activity).operationDone();
                editGroupFragmentViewModel.getGroups();
            } else
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "مشکلی وجود دارد");
        });
    }

    public void clickOnLeaveGroupSubmit() {
        if (adminsUserNames.contains(RayanApplication.getPref().getUsername())) {
            if (admins.size() == 1) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "هر گروه نیاز به یک مدیر دارد");
            } else {
                doLeaveGroup(userId, group.getId());
            }
        } else {
            doLeaveGroup(userId, group.getId());
        }
    }

    public void doLeaveGroup(String id, String groupId) {
        editGroupFragmentViewModel.deleteUser(id, groupId).observe(this, baseResponse -> {
            Log.e("remove user code", baseResponse.getStatus().getCode());
//            Toast.makeText(activity, "remove user code"+baseResponse.getStatus().getCode()+" "+ baseResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("nouser")) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "این کاربر وجود ندارد");
            } else if (baseResponse.getStatus().getCode().equals("404")) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "حذف این کاربر امکان پذیر نیست");
            } else if (baseResponse.getStatus().getCode().equals("403")) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "شما قادر به حذف این کاربر نیستید");
            } else if (baseResponse.getStatus().getCode().equals("204")) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "شما با موفقیت از گروه خارج شدید");
//                ((DoneWithFragment)activity).operationDone();
                activity.onBackPressed();
            } else if (baseResponse.getStatus().getCode().equals("200")) {
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "شما با موفقیت از گروه خارج شدید");
//                ((DoneWithFragment)activity).operationDone();
                activity.onBackPressed();
            } else
                SnackBarSetup.snackBarSetup(activity.findViewById(android.R.id.content), "مشکلی وجود دارد");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
       // onToolbarNameChange.toolbarNameChanged(group.getName());
        if (group != null)
        ((GroupsActivity) activity).toolbarNameChanged(group.getName());
    }
    public interface ClickOnEditGroupButton{
        void OnEditGroupButtonClicked(String id);
    }
    ClickOnEditGroupButton clickOnEditGroupButton;
    Activity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
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
                        admins.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(admins.get(i).getUsername(), activity));
                        admins.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(admins.get(i).getUsername(), activity));
                    }
                    for (int i = 0; i <= users.size() - 1; i++) {
                        users.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(users.get(i).getUsername(), activity));
                        users.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(users.get(i).getUsername(), activity));
                    }
                usersRecyclerViewAdapter.setItems(users);
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
