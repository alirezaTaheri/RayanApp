package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.ConfirmCodeViewModel;

public class ConfirmCodeFragment extends Fragment {
    private final String TAG = ConfirmCodeFragment.class.getSimpleName();
    private ConfirmCodeViewModel confirmCodeViewModel;
        @BindView(R.id.code_confirm_EditText)
        TextView code_confirm_EditText;
    public static ConfirmCodeFragment newInstance(String param1, String param2) {
        ConfirmCodeFragment fragment = new ConfirmCodeFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_code, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        confirmCodeViewModel = ViewModelProviders.of(this).get(ConfirmCodeViewModel.class);
        // TODO: Use the ViewModel
    }
    @OnClick(R.id.register_code_btn)
    void clickOnRegisterCodeBtn() {
            confirmCodeViewModel.confirmCode(code_confirm_EditText.getText().toString()).observe(this, baseResponse ->{
                if (baseResponse.getStatus().getCode().equals("200")) {
                    Toast.makeText(getActivity(), "با موفقیت وارد شدید", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else if (baseResponse.getStatus().getCode().equals("400")) {
                    Toast.makeText(getActivity(), "کد وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
                } else if (baseResponse.getStatus().getCode().equals("404")) {
                    Toast.makeText(getActivity(), "ابتدا باید ثبت نام کنید", Toast.LENGTH_SHORT).show();
                }else if (baseResponse.getStatus().getCode().equals("422")) {
                    Toast.makeText(getActivity(), "کد ارسال شده را وارد کنید", Toast.LENGTH_SHORT).show();
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
