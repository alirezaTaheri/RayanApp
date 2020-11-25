package rayan.rayanapp.Fragments;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.NetworkUtil;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.RegisterUserViewModel;

public class RegisterUserFragment extends Fragment {
    private final String TAG = RegisterUserFragment.class.getSimpleName();
    RegisterUserViewModel registerUserViewModel;
    @BindView(R.id.phone_register_EditText)
    EditText phone_register_EditText;
    @BindView(R.id.email_register_EditText)
    EditText email_register_EditText;
    @BindView(R.id.password_register_EditText)
    EditText password_register_EditText;
    @BindView(R.id.passwordReapet_register_EditText)
    EditText passwordReapet_register_EditText;

    public static RegisterUserFragment newInstance(String param1, String param2) {
        RegisterUserFragment fragment = new RegisterUserFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_user, container, false);
        ButterKnife.bind(this, view);
       // new KeyboardUtil(getActivity(), view.findViewById(R.id.registerUserLayout));
        if (NetworkUtil.getConnectivityStatusString(getContext()).equals(AppConstants.NOT_CONNECTED)){
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"دستگاه به اینترنت متصل نیست");
        }
        phone_register_EditText.setHint("09xxxxxxxxx");
        phone_register_EditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(phone_register_EditText.getText().toString().length()==11)     //size as per your requirement
                {
                    email_register_EditText.requestFocus();
                }
                return false;
            }
        });
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerUserViewModel = ViewModelProviders.of(this).get(RegisterUserViewModel.class);
    }


//    public interface ClickOnRegisterBtn{
//        void clickOnRegisterBtn();
//    }
//    RegisterUserFragment.ClickOnRegisterBtn clickOnRegisterBtn;
//
//    @OnClick(R.id.register_btn)
//    void clickOnRegisterBtn(){
//        clickOnRegisterBtn.clickOnRegisterBtn();
//    }

    @OnClick({R.id.register_btn})
    void clickOnRegisterBtn() {
        if (NetworkUtil.getConnectivityStatusString(getContext()).equals(AppConstants.NOT_CONNECTED)){
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"دستگاه به اینترنت متصل نیست");
        }else {
        if (password_register_EditText.getText().toString().equals(passwordReapet_register_EditText.getText().toString())) {
            registerUserViewModel.registerUser(phone_register_EditText.getText().toString(),
                    password_register_EditText.getText().toString(),email_register_EditText.getText().toString() ).observe(this, baseResponse -> {
                if (baseResponse.getStatus().getCode().equals("200")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"ثبت نام با موفقیت انجام شد");
                    RayanApplication.getPref().createSession(baseResponse.getData().getUser().getId(), baseResponse.getData().getUser().getUsername(),  password_register_EditText.getText().toString(), baseResponse.getData().getUser().getUserInfo(), baseResponse.getData().getUser().getEmail());
                    registerUserViewModel.login();
                    ConfirmCodeFragment confirmCodeFragment= new ConfirmCodeFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.signup_frameLayout, confirmCodeFragment, "findThisFragment")
                            .addToBackStack(null)
                            .commit();

                }
                else if (baseResponse.getStatus().getCode().equals("400")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"قبلا ثبت نام کرده اید");
                }
                else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter username")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"نام کاربری را وارد کنید");
                }
                else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter email")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"ایمیل خود را وارد کنید");
                }
                else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter password")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"رمز ورود خود را وارد کنید");
                }
                else {
                    Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
                }
            });
        }else{
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"رمزهای وارد شده برابر نیستند");
        }
    }}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof RegisterUserFragment.ClickOnRegisterBtn) {
//            clickOnRegisterBtn = (RegisterUserFragment.ClickOnRegisterBtn) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // clickOnRegisterBtn = null;
    }
    @OnFocusChange(R.id.phone_register_EditText)
    void onPhone_register_EditTextFocusChange(){
        phone_register_EditText.setHint("09xxxxxxxxx");
    }
    @OnFocusChange(R.id.email_register_EditText)
    void onEmail_register_EditTextFocusChange(){
        phone_register_EditText.setHint("");
    }
    @OnFocusChange(R.id.password_register_EditText)
    void onPassword_register_EditTextFocusChange(){
        phone_register_EditText.setHint("");
    }
    @OnFocusChange(R.id.passwordReapet_register_EditText)
    void onPasswordReapet_register_EditTextFocusChange(){
        phone_register_EditText.setHint("");
    }
}
