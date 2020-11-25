package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.NewDeviceSetConfigurationFragmentViewModel;

public class NewDeviceSetGroupFragment extends BackHandledFragment implements BlockingStep {

    private NewDeviceSetConfigurationFragmentViewModel mViewModel;
    @BindView(R.id.changeGroup)
    TextView changeGroup;
    DoneWithFragment listener;

    public static NewDeviceSetGroupFragment newInstance() {
        return new NewDeviceSetGroupFragment();
    }
    //id: 2131296496
    //TAG: android:switcher:2131296496:1
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_new_device_set_group, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @OnClick(R.id.changeGroup)
    void changeGroup(){
        ChangeGroupFragment.newInstance().show(getActivity().getSupportFragmentManager(), "changeGroup");
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
    public void setGroupTitle(String title){
        changeGroup.setText(title);
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
        if (((AddNewDeviceActivity)getActivity()).getNewDevice().getGroup() == null || ((AddNewDeviceActivity)getActivity()).getNewDevice().getGroup().getId().trim().length()<1)
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا یک گروه را انتخاب کنید");
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
