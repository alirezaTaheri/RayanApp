package rayan.rayanapp.Fragments;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jaredrummler.materialspinner.MaterialSpinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditUserViewModel;


public class EditUserFragment extends Fragment implements MaterialSpinner.OnItemSelectedListener {
    private final String TAG = EditUserFragment.class.getSimpleName();
    @BindView(R.id.email_EditUser_EditText)
    EditText emailEditText;
    @BindView(R.id.gender_EditUser_Spinner)
    MaterialSpinner genderSpinner;
    @BindView(R.id.name_EditUser_EditText)
    EditText nameEditText;
    @BindView(R.id.phone_EditUser_EditText)
    EditText phoneEditText;
    EditUserViewModel editUserViewModel;
    String gender;
    String name;
    public static EditUserFragment newInstance(String param1, String param2) {
        EditUserFragment fragment = new EditUserFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);
        ButterKnife.bind(this, view);
        Log.e("settttt",nameEditText.getText().toString());
        setDefaultValues();
        setHasOptionsMenu(true);
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editUserViewModel = ViewModelProviders.of(this).get(EditUserViewModel.class);
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        switch(position) {
            case 0:
                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا یک مورد را انتخاب کنید");
                gender=null;
                break;
            case 1:
                gender="Male";
                break;
            case 2:
                gender="Female";
                break;
            default:
                break;
        }
    }


    public interface ClickOnChangePassword{
        void clickOnChangePassword();
    }
    ClickOnChangePassword clickOnChangePassword;

    @OnClick(R.id.edituser_change_passwordbtn)
    void clickOnChangePassword(){
        clickOnChangePassword.clickOnChangePassword();
    }

    @OnClick(R.id.edituser_submitbtn)
    void clickOnSubmit() {
        name=nameEditText.getText().toString();
       if ((name != null && !name.isEmpty())&& (gender != null && !gender.isEmpty())) {
        editUserViewModel.editUser(name, gender , RayanApplication.getPref().getPassword()).observe(this, baseResponse -> {
             if (baseResponse.getStatus().getCode().equals("200")){
                 SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"تغییر اطلاعات با موفقیت انجام شد");
                RayanApplication.getPref().setNameKey(name);
                RayanApplication.getPref().setGenderKey(gender);
                Log.e("settttt",name+" "+gender);
            }
            else {
                 Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                 SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
             }
        });}else if (name != null && !name.isEmpty()) {
           SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا جنسیت خود را وارد کنید");
           }else if (gender != null && !gender.isEmpty()) {
           SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا نام خود را انتخاب کنید");
       }

           }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ClickOnChangePassword) {
            clickOnChangePassword = (ClickOnChangePassword) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.editGroupBasic){
            clickOnSignOut();
        return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.leave_menu, menu);
      //  menu.findItem(R.id.leaveMenu).setTitle("خروج از حساب کاربری");
        // Use filter.xml from step 1
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clickOnChangePassword = null;
    }
    public void setDefaultValues(){
        genderSpinner.setItems("جنسیت","مرد", "زن");
        genderSpinner.setOnItemSelectedListener(this);

        if (!RayanApplication.getPref().getGenderKey().equals("close")){
            gender=RayanApplication.getPref().getGenderKey();
        }
        if (RayanApplication.getPref().getNameKey()!=null){
            name=RayanApplication.getPref().getNameKey();
        }
        switch(RayanApplication.getPref().getGenderKey()) {
            case "Male":
                genderSpinner.setSelectedIndex(1);
                break;
            case "Female":
                genderSpinner.setSelectedIndex(2);
                break;
            case "chose":
                genderSpinner.setSelectedIndex(0);
                break;
            default:
                break;
        }
        phoneEditText.setText(RayanApplication.getPref().getUsername());
        Log.e("geeeeettt",RayanApplication.getPref().getGenderKey()+" "+RayanApplication.getPref().getNameKey());
        phoneEditText.setFocusable(false);
        emailEditText.setFocusable(false);
        nameEditText.setText(RayanApplication.getPref().getNameKey());
        if(RayanApplication.getPref().getEmailKey() != null && !RayanApplication.getPref().getEmailKey().equals("")) {
            emailEditText.setText(RayanApplication.getPref().getEmailKey());
        }else {
            emailEditText.setText("ایمیل خود را ثبت نکرده اید!");
        }
    }
    void clickOnSignOut(){
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("EditUserFragment","خروج از اکانت", "بازگشت", "آیا مایل به خروج هستید؟" );
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
}
