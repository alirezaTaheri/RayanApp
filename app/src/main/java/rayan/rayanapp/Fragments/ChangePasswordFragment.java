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
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.ChangePasswordViewModel;

public class ChangePasswordFragment extends Fragment {
    private final String TAG = ChangePasswordFragment.class.getSimpleName();
    private ChangePasswordViewModel changePasswordViewModel;

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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changePasswordViewModel = ViewModelProviders.of(this).get(ChangePasswordViewModel.class);
        // TODO: Use the ViewModel
    }
      @OnClick(R.id.change_passwordbtn)
      void changePassword(){
        if (newPassword_EditText.getText().toString().equals(newPasswordRepeat_EditText.getText().toString())) {
            changePasswordViewModel.changePassword(currentPassword_EditText.getText().toString(),
                    newPassword_EditText.getText().toString(), RayanApplication.getPref().getUsername()).observe(this, baseResponse -> {
                if (baseResponse.getStatus().getCode().equals("200")) {
                    Toast.makeText(getActivity(), "تغییر رمز با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                } else if (baseResponse.getStatus().getCode().equals("404")) {
                    Toast.makeText(getActivity(), "رمز وارد شده اشتباه است", Toast.LENGTH_SHORT).show();
                } else if (baseResponse.getStatus().getCode().equals("422")) {
                    Toast.makeText(getActivity(), "برای تغییر رمز فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                    Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getActivity(),"رمزهای وارد شده برابر نیستند",Toast.LENGTH_SHORT).show();
        }
    }

}
