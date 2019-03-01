package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.TextView;
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
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.Listeners.DoneWithSelectAccessPointFragment;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.ChangeDeviceAccessPointFragmentViewModel;

public class ChangeDeviceAccessPointFragment extends BottomSheetDialogFragment implements OnNewDeviceClicked<AccessPoint> {
    public AccessPoint selectedAccessPoint;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    AccessPointsRecyclerViewAdapter accessPointsRecyclerViewAdapter;
    ChangeDeviceAccessPointFragmentViewModel viewModel;
    @BindView(R.id.selectedAccessPoint)
    TextView selectedAccessPointTitle;

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
        selectedAccessPoint = new AccessPoint("","","",0);
        viewModel = ViewModelProviders.of(this).get(ChangeDeviceAccessPointFragmentViewModel.class);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        accessPointsRecyclerViewAdapter = new AccessPointsRecyclerViewAdapter(getActivity(),this);
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
        if (((AddNewDeviceActivity)getActivity()).getNewDevice().getSsid() != null)
            selectedAccessPointTitle.setText(((AddNewDeviceActivity)getActivity()).getNewDevice().getSsid());
        selectedAccessPoint.setSSID(((AddNewDeviceActivity) getActivity()).getNewDevice().getSsid() != null? ((AddNewDeviceActivity) getActivity()).getNewDevice().getSsid():"");
        password.setText(((AddNewDeviceActivity) getActivity()).getNewDevice().getPwd());
    }

    @Override
    public void onItemClicked(AccessPoint item) {
            selectedAccessPoint = item;
            accessPointsRecyclerViewAdapter.notifyDataSetChanged();
            selectedAccessPointTitle.setText(item.getSSID());
    }

    @Override
    public void onTestDeviceClicked(AccessPoint item) {

    }

    @OnClick(R.id.confirm)
    void confirm(){
        if (TextUtils.isEmpty(password.getText().toString())){
            Toast.makeText(getActivity(), "لطفا رمزعبور را وارد کنید", Toast.LENGTH_SHORT).show();
        }
        else if (selectedAccessPoint == null){
            Toast.makeText(getActivity(), "لطفا یک مودم را انتخاب کنید", Toast.LENGTH_SHORT).show();
        } else {
            listener.accessPointSelected(selectedAccessPoint.getSSID(), password.getText().toString());
            ((NewDeviceSetConfigurationFragment)((AddNewDeviceActivity)getActivity()).getStepperAdapter().findStep(1)).setAccessPointTitle(selectedAccessPoint.getSSID());
            dismiss();
        }
    }

    @OnClick(R.id.cancel)
    void cancel(){
        dismiss();
    }

    DoneWithSelectAccessPointFragment listener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DoneWithSelectAccessPointFragment) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
