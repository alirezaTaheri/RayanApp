package rayan.rayanapp.Fragments;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Adapters.recyclerView.UsersRecyclerViewAdapter;
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.CreateGroupViewModel;

public class CreateGroupFragment extends Fragment {
    private CreateGroupViewModel createGroupViewModel;
    static final int PICK_CONTACT=1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static CreateGroupFragment instance = null;
    @BindView(R.id.groupNameEditText)
    EditText name;
    String nameTxt;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    public UsersRecyclerViewAdapter usersRecyclerViewAdapter;
    public List<User> users = new ArrayList<>();
    public ArrayList<String> numbers = new ArrayList<>();
    public static CreateGroupFragment newInstance() {
        return new CreateGroupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_group_fragment, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(getActivity());
        usersRecyclerViewAdapter.setItems(users);
        recyclerView.setAdapter(usersRecyclerViewAdapter);
        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case (PICK_CONTACT):
//                if (resultCode == Activity.RESULT_OK) {
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
//                            String cNumber;
//                            cNumber = phones.getString(phones.getColumnIndex("data1"));
//                            cNumber = cNumber.trim();
//                            while (cNumber.contains(" "))
//                                cNumber = cNumber.replace(" ", "");
//                            cNumber = cNumber.replace("+98","0");
//                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                            Contact contact = new Contact();
//                            contact.setName(name);
//                            contact.setNumbers(cNumber);
//                            User user = new User();
//                            user.setContactName(name);
//                            user.setUsername(cNumber);
//                            users.add(user);
//                            numbers.add(cNumber);
//                            usersRecyclerViewAdapter.setItems(users);
//                        }
//                    }
//                }
//                break;
//        }
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        instance=this;
        createGroupViewModel = ViewModelProviders.of(this).get(CreateGroupViewModel.class);
    }
    @OnClick(R.id.addUserButton)
    void addUser(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
//            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//            startActivityForResult(intent, PICK_CONTACT);
            PhoneContactListBottomSheetFragment phoneContactListFragment =new PhoneContactListBottomSheetFragment().newInstance("CreateGroupFragment");
            phoneContactListFragment.show(getActivity().getSupportFragmentManager(), phoneContactListFragment.getTag());
        }
        else getContactPermission();

    }
    @OnClick(R.id.createGroup)
    public void createGroup(){
        nameTxt=name.getText().toString();
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("CreateGroupFragment","ایجاد گروه", "بازگشت", "آیا مایل به ایجاد گروه هستید؟");
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void getContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }


    public void clickOnSubmit() {
        createGroupViewModel.createGroup(nameTxt, numbers).observe(this, baseResponse -> {
            if (baseResponse.getStatus().getCode().equals("422")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا نام گروه را وارد کنید");
            }
            else if (baseResponse.getStatus().getCode().equals("200")){
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"گروه با موفقیت ایجاد شد");
                ((DoneWithFragment)getActivity()).operationDone();
            }
            else
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
        });
    }
    public static CreateGroupFragment getInstance() {
        return instance;
    }
}