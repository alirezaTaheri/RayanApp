package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Helper.DialogPresenter;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.KeyboardUtil;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.ChangePasswordViewModel;
import rayan.rayanapp.ViewModels.LoginViewModel;

public class ChangePasswordFragment extends Fragment {
    private final String TAG = ChangePasswordFragment.class.getSimpleName();
    private ChangePasswordViewModel changePasswordViewModel;
    LoginViewModel loginViewModel;
    String username;
    Bundle bundle;
    @BindView(R.id.currentPassword_EditText)
    EditText currentPassword_EditText;
    @BindView(R.id.newPassword_EditText)
    EditText newPassword_EditText;
    @BindView(R.id.newPasswordRepeat_EditText)
    TextView newPasswordRepeat_EditText;

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        ButterKnife.bind(this, view);
    //    new KeyboardUtil(getActivity(), view.findViewById(R.id.changepassLayout));
        bundle = this.getArguments();
        if(bundle != null) {
            username = bundle.getString("user");
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changePasswordViewModel = ViewModelProviders.of(this).get(ChangePasswordViewModel.class);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }
      @OnClick(R.id.change_passwordbtn)
      void changePassword(){
        if (newPassword_EditText.getText().toString().equals(newPasswordRepeat_EditText.getText().toString())) {
            if(bundle != null){
                changePasswordViewModel.changePassword(currentPassword_EditText.getText().toString(),
                        newPassword_EditText.getText().toString(), username).observe(this, baseResponse -> {
                    if (baseResponse.getStatus().getCode().equals("200")) {
                        loginViewModel.login(username, newPassword_EditText.getText().toString() , new DialogPresenter(getActivity().getSupportFragmentManager()));
                        Toast.makeText(getActivity(), "تغییر رمز با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                        if (baseResponse.getData().getUser().getRegistered().equals("true")) {
                            RayanApplication.getPref().saveToken(baseResponse.getData().getToken());
                            RayanApplication.getPref().createSession(baseResponse.getData().getUser().getId(), baseResponse.getData().getUser().getUsername(),  newPassword_EditText.getText().toString(), baseResponse.getData().getUser().getUserInfo(), baseResponse.getData().getUser().getEmail());
                        }
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (baseResponse.getStatus().getCode().equals("403")) {
                        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"کد وارد شده اشتباه است");
                    } else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter password")) {
                        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content)," کد تغییر رمز را وارد کنید");
                    }else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter new_password")) {
                        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"رمز جدید خود را وارد کنید");
                    } else {
                        Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                        Log.e("message", baseResponse.getData().getMessage());
                        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
                    }
                });
            }else {
            changePasswordViewModel.changePassword(currentPassword_EditText.getText().toString(),
                    newPassword_EditText.getText().toString(), RayanApplication.getPref().getUsername()).observe(this, baseResponse -> {
                if (baseResponse.getStatus().getCode().equals("200")) {
                    Toast.makeText(getActivity(), "تغییر رمز با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (baseResponse.getStatus().getCode().equals("403")&& baseResponse.getData().getMessage().equals("You must enter password")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"رمز وارد شده اشتباه است");
                }else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter password")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"رمز فعلی را وارد کنید");
                }else if (baseResponse.getStatus().getCode().equals("422") && baseResponse.getData().getMessage().equals("You must enter new_password")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"رمز جدید خود را وارد کنید");
                }else {
                    Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                    Log.e("message", baseResponse.getData().getMessage());
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
                }
            });}
        }else{
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"رمزهای وارد شده برابر نیستند");
        }
    }

}
