package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
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
import rayan.rayanapp.Retrofit.Models.Requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.NewDeviceSetConfigurationFragmentViewModel;

public class NewDeviceSetAccessPoint extends BackHandledFragment implements BlockingStep {

    private NewDeviceSetConfigurationFragmentViewModel mViewModel;
    @BindView(R.id.changeAccessPoint)
    TextView changeAccessPoint;
    DoneWithFragment listener;

    public static NewDeviceSetAccessPoint newInstance() {
        return new NewDeviceSetAccessPoint();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_new_device_set_access_point, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @OnClick(R.id.changeAccessPoint)
    void changeAccessPoint(){
        ChangeDeviceAccessPointFragment.newInstance(((AddNewDeviceActivity)getActivity()).getNewDevice().getSsid()).show(getActivity().getSupportFragmentManager(), "changeAccessPoint");
    }


    @Override
    public boolean onBackPressed() {
        ((AddNewDeviceActivity)getActivity()).setStepperPosition(0);
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

    public void setAccessPointTitle(String title){
        changeAccessPoint.setText(title);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = ((DoneWithFragment)getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        if (((AddNewDeviceActivity)getActivity()).getNewDevice().getSsid() == null || ((AddNewDeviceActivity)getActivity()).getNewDevice().getSsid().trim().length()<1)
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا یک مودم را انتخاب کنید");
        else{
            callback.goToNextStep();

//            mViewModel.internetProvided().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe((aBoolean, throwable) -> {
//                if (aBoolean){
//                    ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
//                    progressDialog.show();
//                    progressDialog.cancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Log.e(getClass().getSimpleName(),">>>>>>>>>>>>>>>>>Canceling The Process");
//                            mViewModel.getConfigDeviceDisposable().dispose();
//                            progressDialog.dismiss();
//                        }
//                    });
//                    callback.getStepperLayout().showProgress("درحال برقراری ارتباط با دستگاه"+"\n"+"لطفا کمی صبرکنید");
//                    mViewModel.registerDeviceSendToDevice(
//                            (WifiManager) Objects.requireNonNull(getActivity()).getApplicationContext().getSystemService(Context.WIFI_SERVICE),
//                            ((AddNewDeviceActivity)getActivity()),new RegisterDeviceRequest(((AddNewDeviceActivity) getActivity()).getNewDevice().getChip_id(),((AddNewDeviceActivity) getActivity()).getNewDevice().getName() , ((AddNewDeviceActivity) getActivity()).getNewDevice().getType())
//                            ,AppConstants.NEW_DEVICE_IP)
//                            .observe(this, s -> {
//                                switch (s.getCmd()){
//                                    case AppConstants.NEW_DEVICE_TOGGLE_CMD:
//                                        ((AddNewDeviceActivity) getActivity()).getNewDevice().setToggleCount(Integer.parseInt(s.getCount()));
//                                        ((AddNewDeviceActivity) getActivity()).getStepperAdapter().notifyDataSetChanged();
//                                        callback.goToNextStep();
//                                        break;
//                                    case AppConstants.NEW_DEVICE_PHV_START:
//                                        callback.goToNextStep();
//                                        break;
//                                    case AppConstants.SOCKET_TIME_OUT:
//                                        Toast.makeText(getContext(), "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
//                                        break;
//                                }
//                                callback.getStepperLayout().hideProgress();
//                                progressDialog.dismiss();
//                            });
//                }
//                else{
//                    ProvideInternetFragment.newInstance().show(getActivity().getSupportFragmentManager(), "provideInternet");
//                }
//            });

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
