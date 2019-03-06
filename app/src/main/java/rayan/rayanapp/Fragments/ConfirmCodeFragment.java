package rayan.rayanapp.Fragments;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.R;
import rayan.rayanapp.Receivers.SMSBroadCastReceiver;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.ConfirmCodeViewModel;

public class ConfirmCodeFragment extends Fragment {
    private final String TAG = ConfirmCodeFragment.class.getSimpleName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
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
        SMSBroadCastReceiver SMSBroadCastReceiver=new SMSBroadCastReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(SMSBroadCastReceiver, filter);
        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
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
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"کد وارد شده صحیح نیست");
                } else if (baseResponse.getStatus().getCode().equals("404")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"ابتدا باید ثبت نام کنید");
                }else if (baseResponse.getStatus().getCode().equals("422")) {
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"کد ارسال شده را وارد کنید");
                }
                else {
                    Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");

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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                Log.e("smssss",message);
                code_confirm_EditText.setText(message);
                clickOnRegisterCodeBtn();
            }
        }
    };

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.SEND_SMS);
        int receiveSMS = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(),
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

}
