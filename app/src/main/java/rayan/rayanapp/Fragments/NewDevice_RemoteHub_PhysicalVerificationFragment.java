package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stepstone.stepper.StepperLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Dialogs.ProgressDialog;
import rayan.rayanapp.Dialogs.YesNoDialog;
import rayan.rayanapp.Listeners.StepperItemSimulation;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.NewDevicePhysicalVerificationViewModel;

public class NewDevice_RemoteHub_PhysicalVerificationFragment extends Fragment implements StepperItemSimulation, YesNoDialogListener {
    private OnFragmentInteractionListener mListener;
    private NewDevice device;
    private int toggleCount = -1;
    @BindView(R.id.command)
    TextView command;
    NewDevicePhysicalVerificationViewModel viewModel;

    public NewDevice_RemoteHub_PhysicalVerificationFragment() {

    }

    public static NewDevice_RemoteHub_PhysicalVerificationFragment newInstance() {
        NewDevice_RemoteHub_PhysicalVerificationFragment fragment = new NewDevice_RemoteHub_PhysicalVerificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(NewDevicePhysicalVerificationViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_device_physical_verification_remote_hub, container, false);
        ButterKnife.bind(this, view);
        toggleCount = AddNewDeviceActivity.getNewDevice().getToggleCount();
        String[] s = new String[5];
        s[0] = "لطفا جهت دسترسی شما به دستگاه، یکبار ";
        s[1] = "دکمه روی دستگاه را لمس کنید";
        s[2] = " و ";
        s[3] = "دکمه بعدی";
        s[4] = " در اپلیکیشن را بزنید.";
        SpannableString s0 = new SpannableString(s[0]);
        SpannableString s2 = new SpannableString(s[2]);
        SpannableString s4 = new SpannableString(s[4]);
        SpannableString s1 = new SpannableString(s[1]);
        s1.setSpan(new UnderlineSpan(), 0, s1.length(), 0);
        s1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.baseColor)), 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString s3 = new SpannableString(s[3]);
        s3.setSpan(new UnderlineSpan(), 0, s3.length(), 0);
        s3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.baseColor)), 0, s3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        command.setText(TextUtils.concat(s0,s1,s2,s3,s4));
        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        viewModel.to_remoteHub_physical_verification().observe(this, result -> {
            progressDialog.dismiss();
            Log.e("RemoteHubPHV", "Received Result: " + result);
            assert result != null;
            switch (result){
                case AppConstants.SUCCESS_RESULT:
                    Toast.makeText(getActivity(), "دسترسی شما با موفقیت تایید شد", Toast.LENGTH_SHORT).show();
                    callback.goToNextStep();
                    break;
                case AppConstants.ERROR_RESULT:
                    Toast.makeText(getActivity(), "دسترسی شما تایید نشد", Toast.LENGTH_SHORT).show();
                    YesNoDialog yesNoDialog = new YesNoDialog(activity, this,"دسترسی شما تایید نشد"+"\nآیا مایل به تلاش دوباره هستید؟", null);
                    yesNoDialog.show();
                    break;
                case AppConstants.EXPIRED:
                    Toast.makeText(getActivity(), "زمان شما به اتمام رسیده است", Toast.LENGTH_SHORT).show();
                    yesNoDialog = new YesNoDialog(activity, this,"زمان شما به اتمام رسیده است"+"\nآیا مایل به تلاش دوباره هستید؟", null);
                    yesNoDialog.show();
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    AddNewDeviceActivity.getNewDevice().setFailed(true);
                    Toast.makeText(getContext(), "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                    yesNoDialog = new YesNoDialog(activity, this,"مشکلی در دسترسی وجود دارد"+"\nآیا مایل به تلاش دوباره هستید؟", null);
                    yesNoDialog.show();
                    break;
                case AppConstants.UNKNOWN_EXCEPTION:
                    Toast.makeText(getContext(), "یک مشکل ناشناخته رخ داده است", Toast.LENGTH_SHORT).show();
                    yesNoDialog = new YesNoDialog(activity, this,"یک مشکل ناشناخته رخ داده است"+"\nآیا مایل به تلاش دوباره هستید؟", null);
                    yesNoDialog.show();
                    break;
                case AppConstants.CONNECT_EXCEPTION:
                    Toast.makeText(getContext(), "ارسال پیام به دستگاه موفق نبود", Toast.LENGTH_SHORT).show();
                    yesNoDialog = new YesNoDialog(activity, this,"ارسال پیام به دستگاه موفق نبود"+"\nآیا مایل به تلاش دوباره هستید؟", null);
                    yesNoDialog.show();
                    break;
                case AppConstants.UNKNOWN_HOST_EXCEPTION:
                    Toast.makeText(getContext(), "متاسفانه نمی‌توان با دستگاه ارتباط برقرار کرد", Toast.LENGTH_SHORT).show();
                    yesNoDialog = new YesNoDialog(activity, this,"متاسفانه نمی‌توان با دستگاه ارتباط برقرار کرد"+"\nآیا مایل به تلاش دوباره هستید؟", null);
                    yesNoDialog.show();
                    break;
                    default: Toast.makeText(getActivity(), "مشکلی در نصب دستگاه وجود دارد", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackClicked() {
        ((AddNewDeviceActivity)getContext()).setStepperPosition(1);
    }

    @Override
    public void onYesClicked(YesNoDialog dialog, Bundle data) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        viewModel.resend_config_to_remoteHub(AddNewDeviceActivity.getNewDevice(), AppConstants.NEW_DEVICE_IP)
        .observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String result) {
                progressDialog.dismiss();
                Log.e("RemoteHubPHV", "Received Result: " + result);
                assert result != null;
                switch (result){
                    case AppConstants.SUCCESS_RESULT:
                        Toast.makeText(getActivity(), "دسترسی شما با موفقیت تایید شد", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        break;
                    case AppConstants.ERROR_RESULT:
                        Toast.makeText(getActivity(), "دسترسی شما تایید نشد", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.EXPIRED:
                        Toast.makeText(getActivity(), "زمان شما به اتمام رسیده است", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.SOCKET_TIME_OUT:
                        AddNewDeviceActivity.getNewDevice().setFailed(true);
                        Toast.makeText(getContext(), "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.UNKNOWN_EXCEPTION:
                        Toast.makeText(getContext(), "یک مشکل ناشناخته رخ داده است", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.CONNECT_EXCEPTION:
                        Toast.makeText(getContext(), "ارسال پیام به دستگاه موفق نبود", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.UNKNOWN_HOST_EXCEPTION:
                        Toast.makeText(getContext(), "متاسفانه نمی‌توان با دستگاه ارتباط برقرار کرد", Toast.LENGTH_SHORT).show();
                        break;
                    default: Toast.makeText(getActivity(), "مشکلی در نصب دستگاه وجود دارد", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onNoClicked(YesNoDialog dialog, Bundle data) {
        getActivity().onBackPressed();
        dialog.dismiss();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    AddNewDeviceActivity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddNewDeviceActivity) context;
    }
}
