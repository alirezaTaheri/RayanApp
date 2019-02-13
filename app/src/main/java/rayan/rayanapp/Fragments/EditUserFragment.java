package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.DevicesFragmentViewModel;
import rayan.rayanapp.ViewModels.EditUserViewModel;


public class EditUserFragment extends Fragment {
    private final String TAG = EditUserFragment.class.getSimpleName();
    List<String> genderList = new ArrayList();
    @BindView(R.id.email_EditUser_EditText)
    EditText emailEditText;
    @BindView(R.id.gender_EditUser_Spinner)
    Spinner genderSpinner;
    @BindView(R.id.name_EditUser_EditText)
    EditText nameEditText;
    @BindView(R.id.phone_EditUser_EditText)
    EditText phoneEditText;
    EditUserViewModel editUserViewModel;
    String gender;

    public static EditUserFragment newInstance(String param1, String param2) {
        EditUserFragment fragment = new EditUserFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);
        ButterKnife.bind(this, view);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, genderList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(dataAdapter);
        phoneEditText.setText(RayanApplication.getPref().getUsername());
        phoneEditText.setFocusable(false);
        emailEditText.setFocusable(false);
        emailEditText.setText(RayanApplication.getPref().getUsername());
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editUserViewModel = ViewModelProviders.of(this).get(EditUserViewModel.class);
        genderList.add("انتخاب جنسیت");
        genderList.add("مرد");
        genderList.add("زن");
    }


    public interface ClickOnChangePassword{
        void clickOnChangePassword();
    }
    ClickOnChangePassword clickOnChangePassword;

    @OnClick(R.id.edituser_change_passwordbtn)
    void clickOnChangePassword(){
        clickOnChangePassword.clickOnChangePassword();
    }

    @OnClick({R.id.edituser_submitbtn})
    void clickOnSubmit() {

//        int position=genderSpinner.getSelectedItemPosition();
//
//        switch(position) {
//            case 1:
//                gender="Male";
//                break;
//            case 2:
//               gender="Female";
//                break;
//            default:
//               break;
//        }
        ///be jaye gender felan password ferestade mishe ta badan api v2 ok she
        editUserViewModel.editUser(nameEditText.getText().toString(), RayanApplication.getPref().getPassword()).observe(this, baseResponse -> {
             if (baseResponse.getStatus().getCode().equals("200")){
                Toast.makeText(getActivity(), "تغییر اطلاعات با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
            }
            else {
                 Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                 Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
             }
        });
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
    public void onDetach() {
        super.onDetach();
        clickOnChangePassword = null;
    }
}
