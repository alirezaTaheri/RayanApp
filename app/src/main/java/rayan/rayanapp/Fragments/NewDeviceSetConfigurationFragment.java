package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Dialogs.ProgressDialog;
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.base.BaseResponse_Device;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.NewDeviceSetConfigurationFragmentViewModel;

public class NewDeviceSetConfigurationFragment extends BackHandledFragment implements BlockingStep {

    private NewDeviceSetConfigurationFragmentViewModel mViewModel;
    @BindView(R.id.changeAccessPoint)
    TextView changeAccessPoint;
    @BindView(R.id.changeGroup)
    TextView changeGroup;
    @BindView(R.id.name)
    EditText nameEditText;
    DoneWithFragment listener;

    public static NewDeviceSetConfigurationFragment newInstance() {
        return new NewDeviceSetConfigurationFragment();
    }
    //id: 2131296496
    //TAG: android:switcher:2131296496:1
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.e(this.getClass().getSimpleName(), "TAG is: " + getTag() + " Id Is: " + getId());
        View view =  inflater.inflate(R.layout.new_device_set_configuration_fragment, container, false);
        ButterKnife.bind(this,view);
        return view;
    }
    @OnClick(R.id.changeAccessPoint)
    void changeAccessPoint(){
        ChangeDeviceAccessPointFragment.newInstance(AddNewDeviceActivity.getNewDevice().getSsid()).show(activity.getSupportFragmentManager(), "changeAccessPoint");
    }

    @OnClick(R.id.changeGroup)
    void changeGroup(){
        ChangeGroupFragment.newInstance().show(activity.getSupportFragmentManager(), "changeGroup");
    }

    @Override
    public boolean onBackPressed() {
        (activity).setStepperPosition(0);
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NewDeviceSetConfigurationFragmentViewModel.class);
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        super.onResume();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    public void setGroupTitle(String title){
        changeGroup.setText(title);
    }

    public void setAccessPointTitle(String title){
        changeAccessPoint.setText(title);
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = ((DoneWithFragment)context);
        activity = (AddNewDeviceActivity) context;
    }
    AddNewDeviceActivity activity;
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void groupCreated(){
        ChangeGroupFragment changeGroupFragment = (ChangeGroupFragment)getChildFragmentManager().findFragmentByTag("changeGroup");
        for (int a = 0; a<activity.getSupportFragmentManager().getFragments().size();a++){
            Log.e("TAGGGGG: " , "TAG of: " +a+" Is: "+ activity.getSupportFragmentManager().getFragments().get(a).getTag());
            Log.e("TAGGGGG: " , "Activity of: " +a+" Is: "+ activity.getSupportFragmentManager().getFragments().get(a).getActivity());
            Log.e("TAGGGGG: " , "Id of: " +a+" Is: "+ activity.getSupportFragmentManager().getFragments().get(a).getId());
            Log.e("TAGGGGG: " , "Parent Fragment of: " +a+" Is: "+ activity.getSupportFragmentManager().getFragments().get(a).getParentFragment());
        }
//        changeGroupFragment.selectGroupMode();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        if (TextUtils.isEmpty(nameEditText.getText().toString().trim()))
            Toast.makeText(getContext(), "لطفا نام دستگاه را وارد کنید", Toast.LENGTH_SHORT).show();
        else if (AddNewDeviceActivity.getNewDevice().getGroup() == null || AddNewDeviceActivity.getNewDevice().getGroup().getId().trim().length() < 1)
            Toast.makeText(getContext(), "لطفا یک گروه را انتخاب کنید", Toast.LENGTH_SHORT).show();
        else if (AddNewDeviceActivity.getNewDevice().getSsid() == null || AddNewDeviceActivity.getNewDevice().getSsid().trim().length() < 1)
            Toast.makeText(getContext(), "لطفا یک مودم را انتخاب کنید", Toast.LENGTH_SHORT).show();
        else {
            mViewModel.internetProvided().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe((aBoolean, throwable) -> {
                if (aBoolean) {
                    ProgressDialog progressDialog = new ProgressDialog(activity);
                    callback.getStepperLayout().showProgress("درحال برقراری ارتباط با دستگاه" + "\n" + "لطفا کمی صبرکنید");
                    AddNewDeviceActivity.getNewDevice().setName(nameEditText.getText().toString());
                    Log.e("ConfigurationFragment", "Installing: "+AddNewDeviceActivity.getNewDevice());
                    if (AddNewDeviceActivity.getNewDevice().getType()!= null & !AddNewDeviceActivity.getNewDevice().getType().equals(AppConstants.DEVICE_TYPE_RemoteHub)) {
                        progressDialog.show();
                        progressDialog.cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e(getClass().getSimpleName(), ">>>>>>>>>>>>>>>>>Canceling The Process");
                                mViewModel.getConfigDeviceDisposable().dispose();
                                progressDialog.dismiss();
                            }
                        });
                        mViewModel.installSwitch(
                                (WifiManager) Objects.requireNonNull(activity).getApplicationContext().getSystemService(Context.WIFI_SERVICE),
                                new RegisterDeviceRequest(AddNewDeviceActivity.getNewDevice().getChip_id(),AddNewDeviceActivity.getNewDevice().getName(), AddNewDeviceActivity.getNewDevice().getType()),
                                activity,
                                AppConstants.NEW_DEVICE_IP
                        ).observe(this, new Observer<BaseResponse_Device>() {
                            @Override
                            public void onChanged(@Nullable BaseResponse_Device s) {
                                switch (s.getOutput()) {
                                    case AppConstants.SUCCESSFUL:
                                        callback.goToNextStep();
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
                                    default:
                                        break;
                                }
                            }
                        });
                        mViewModel.registerDeviceSendToDevice(
                                (WifiManager) Objects.requireNonNull(activity).getApplicationContext().getSystemService(Context.WIFI_SERVICE),
                                ((AddNewDeviceActivity) activity), new RegisterDeviceRequest(AddNewDeviceActivity.getNewDevice().getChip_id(), AddNewDeviceActivity.getNewDevice().getName(), AddNewDeviceActivity.getNewDevice().getType())
                                , AppConstants.NEW_DEVICE_IP)
                                .observe(this, s -> {
                                    if (s != null && s.getResult() != null)
                                        switch (s.getResult()) {
                                            case AppConstants.SUCCESSFUL:
                                                callback.goToNextStep();
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
                                            default:
                                                if (s.getResult().contains(AppConstants.SUCCESSFUL)) {
                                                    AddNewDeviceActivity.getNewDevice().setToggleCount(Integer.parseInt(s.getCount()));
                                                    ((AddNewDeviceActivity) activity).getStepperAdapter().notifyDataSetChanged();
                                                    callback.goToNextStep();
                                                }
                                                break;
                                        }
                                    else
                                        Toast.makeText(getContext(), "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                                    callback.getStepperLayout().hideProgress();
                                    progressDialog.dismiss();
                                });
                    }
                    else if (AddNewDeviceActivity.getNewDevice().getType()!= null & AddNewDeviceActivity.getNewDevice().getType().equals(AppConstants.DEVICE_TYPE_RemoteHub)) {
                        progressDialog.show();
                        progressDialog.cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e(getClass().getSimpleName(), ">>>>>>>>>>>>>>>>>Canceling The Installation Process");
                                mViewModel.getConfigDeviceDisposable().dispose();
                                progressDialog.dismiss();
                            }
                        });
                        mViewModel.installRemoteHub(
                                (WifiManager) Objects.requireNonNull(activity).getApplicationContext().getSystemService(Context.WIFI_SERVICE),
                                activity,
                                AddNewDeviceActivity.getNewDevice(),
                                AppConstants.NEW_DEVICE_IP)
                                .observe(this, s -> {
                            if (s != null && s.getResult() != null)
                                switch (s.getResult()) {
                                    case AppConstants.SUCCESS_RESULT:
                                        activity.getStepperAdapter().notifyDataSetChanged();
                                        callback.goToNextStep();
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
                                }
                            else
                                Toast.makeText(getContext(), "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                            callback.getStepperLayout().hideProgress();
                            progressDialog.dismiss();
                        });
                    }
                    else
                        Toast.makeText(activity, "نوع دستگاه انتخابی مشخص نیست", Toast.LENGTH_SHORT).show();
                } else {
                    ProvideInternetFragment.newInstance().show(activity.getSupportFragmentManager(), "provideInternet");
                }
            });
        }
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }
}
