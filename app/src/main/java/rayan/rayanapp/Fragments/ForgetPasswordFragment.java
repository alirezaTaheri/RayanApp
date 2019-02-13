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
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.ForgetPasswordViewModel;

public class ForgetPasswordFragment extends Fragment {
    private final String TAG = ForgetPasswordFragment.class.getSimpleName();
    private ForgetPasswordViewModel forgetPasswordViewModel;
    @BindView(R.id.phone_forgetpass_EditText)
    EditText phone_forgetpass_EditText;
    @BindView(R.id.email_forgetpass_EditText)
    EditText email_forgetpass_EditText;
    public static ForgetPasswordFragment newInstance(String param1, String param2) {
        ForgetPasswordFragment fragment = new ForgetPasswordFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget__password, container, false);
        ButterKnife.bind(this, view);
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
                    Toast.makeText(getActivity(), "با موفقیت انجام شد", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getActivity(), "شما باید ثبت نام کنید", Toast.LENGTH_SHORT).show();
                } else if (baseResponse.getStatus().getCode().equals("422")) {
                    Toast.makeText(getActivity(), "برای بازیابی رمز فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                    Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
