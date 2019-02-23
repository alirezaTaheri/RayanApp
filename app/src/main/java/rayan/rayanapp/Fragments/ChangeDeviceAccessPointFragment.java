package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Adapters.recyclerView.AccessPointsRecyclerViewAdapter;
import rayan.rayanapp.Adapters.recyclerView.NewDevicesRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.ChangeDeviceAccessPointFragmentViewModel;

public class ChangeDeviceAccessPointFragment extends BottomSheetDialogFragment implements OnNewDeviceClicked<AccessPoint> {
    public AccessPoint selectedAccessPoint;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    AccessPointsRecyclerViewAdapter accessPointsRecyclerViewAdapter;
    ChangeDeviceAccessPointFragmentViewModel viewModel;

    public static ChangeDeviceAccessPointFragment newInstance() {
        final ChangeDeviceAccessPointFragment fragment = new ChangeDeviceAccessPointFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_device_access_point, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        viewModel = ViewModelProviders.of(this).get(ChangeDeviceAccessPointFragmentViewModel.class);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        accessPointsRecyclerViewAdapter = new AccessPointsRecyclerViewAdapter(getActivity(), (ChangeDeviceAccessPointFragment)this);
        recyclerView.setAdapter(accessPointsRecyclerViewAdapter);
        accessPointsRecyclerViewAdapter.setItems(viewModel.getSSIDs());
        accessPointsRecyclerViewAdapter.setListener(this);
        ((RayanApplication) getActivity().getApplication()).getWifiBus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(scanResults -> {
                    List<AccessPoint> newDevices = new ArrayList<>();
                    for (int a = 0; a < scanResults.size(); a++)
                        newDevices.add(new AccessPoint(scanResults.get(a).SSID, scanResults.get(a).BSSID, scanResults.get(a).capabilities, scanResults.get(a).level));
                    accessPointsRecyclerViewAdapter.setItems(newDevices);
                });
    }

    @Override
    public void onItemClicked(AccessPoint item) {
            selectedAccessPoint = item;
            accessPointsRecyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    public void onTestDeviceClicked(AccessPoint item) {

    }

    @OnClick(R.id.confirm)
    void confirm(){
        if (TextUtils.isEmpty(password.getText().toString())){
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا رمزعبور را وارد کنید");

        }
        else if (selectedAccessPoint == null){
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا یک مودم را انتخاب کنید");

        } else {
            if (getActivity() instanceof AddNewDeviceActivity){
                ((AddNewDeviceActivity) getActivity()).getNewDevice().setSsid(selectedAccessPoint.getSSID());
                ((AddNewDeviceActivity) getActivity()).getNewDevice().setPwd(password.getText().toString());
            }
            dismiss();
        }

    }

    @OnClick(R.id.cancel)
    void cancel(){
        dismiss();
    }
}
