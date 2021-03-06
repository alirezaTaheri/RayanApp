package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.KeyboardUtil;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.ForgetPasswordViewModel;

public class ForgetPasswordFragment extends Fragment {
    private final String TAG = ForgetPasswordFragment.class.getSimpleName();
    private ForgetPasswordViewModel forgetPasswordViewModel;
    @BindView(R.id.phone_forgetpass_EditText)
    EditText phone_forgetpass_EditText;
    @BindView(R.id.email_forgetpass_EditText)
    EditText email_forgetpass_EditText;
    public static ForgetPasswordFragment newInstance() {
        ForgetPasswordFragment fragment = new ForgetPasswordFragment();
        return fragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget__password, container, false);
        ButterKnife.bind(this, view);
       // new KeyboardUtil(getActivity(), view.findViewById(R.id.forgetPassLayout));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        forgetPasswordViewModel = ViewModelProviders.of(this).get(ForgetPasswordViewModel.class);
        // TODO: Use the ViewModel
    }
    @OnClick(R.id.submit_forgetpass_btn)
    void submit_forgetpass_btn(){
            forgetPasswordViewModel.forgetPassword(phone_forgetpass_EditText.getText().toString(),
                    email_forgetpass_EditText.getText().toString()).observe(this, baseResponse -> {
                if (baseResponse.getStatus().getCode().equals("200")) {
                   // RayanApplication.getPref().createSession(baseResponse.getData().getUser().getId(), baseResponse.getData().getUser().getUsername(),  password_register_EditText.getText().toString(), baseResponse.getData().getUser().getUserInfo());
                  //  forgetPasswordViewModel.login();
                    Bundle bundle = new Bundle();
                    bundle.putString("user",phone_forgetpass_EditText.getText().toString());
                    ChangePasswordFragment changePasswordFragment= new ChangePasswordFragment();
                    changePasswordFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.forgetpass_frameLayout, changePasswordFragment, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                } else if (baseResponse.getStatus().getCode().equals("404")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شما باید ثبت نام کنید");
                }
                else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter mobile number")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شماره تلفن خود را وارد کنید");
                }
                else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter email")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"ایمیل خود را وارد کنید");
                }
                else {
                    Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
                }
            });
    }
    @OnFocusChange(R.id.phone_forgetpass_EditText)
    void onPhoneEditTextFocusChange(){
        phone_forgetpass_EditText.setHint("09xxxxxxxxx");
    }
    @OnFocusChange(R.id.email_forgetpass_EditText)
    void onEmailEditTextFocusChange(){
        phone_forgetpass_EditText.setHint("");
    }
}
