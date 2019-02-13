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
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.EditUserViewModel;
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
        if (password_register_EditText.getText().toString().equals(passwordReapet_register_EditText.getText().toString())) {
            registerUserViewModel.registerUser(phone_register_EditText.getText().toString(),
                    password_register_EditText.getText().toString(),email_register_EditText.getText().toString() ).observe(this, baseResponse -> {
                if (baseResponse.getStatus().getCode().equals("200")) {
                    Toast.makeText(getActivity(), "ثبت نام با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                    RayanApplication.getPref().createSession(baseResponse.getData().getUser().getId(), baseResponse.getData().getUser().getUsername(),  password_register_EditText.getText().toString(), baseResponse.getData().getUser().getUserInfo());

                    registerUserViewModel.login();
                    ConfirmCodeFragment confirmCodeFragment= new ConfirmCodeFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.signup_frameLayout, confirmCodeFragment, "findThisFragment")
                            .addToBackStack(null)
                            .commit();

                } else if (baseResponse.getStatus().getCode().equals("400")) {
                    Toast.makeText(getActivity(), "قبلا ثبت نام کرده اید", Toast.LENGTH_SHORT).show();
                } else if (baseResponse.getStatus().getCode().equals("422")) {
                    Toast.makeText(getActivity(), "برای ثبت نام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                    Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getActivity(),"رمزهای وارد شده برابر نیستند",Toast.LENGTH_SHORT).show();
        }
    }

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
}
