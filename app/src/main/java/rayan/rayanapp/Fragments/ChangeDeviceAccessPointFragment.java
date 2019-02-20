package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Adapters.recyclerView.NewDevicesRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.ChangeDeviceAccessPointFragmentViewModel;

public class ChangeDeviceAccessPointFragment extends BottomSheetDialogFragment implements OnNewDeviceClicked<NewDevice> {
    private NewDevice selectedSSID;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    NewDevicesRecyclerViewAdapter newDevicesRecyclerViewAdapter;
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
        newDevicesRecyclerViewAdapter = new NewDevicesRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(newDevicesRecyclerViewAdapter);
        newDevicesRecyclerViewAdapter.setItems(viewModel.getSSIDs());
        newDevicesRecyclerViewAdapter.setListener(this);
        ((RayanApplication) getActivity().getApplication()).getWifiBus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(scanResults -> {
                    List<NewDevice> newDevices = new ArrayList<>();
                    for (int a = 0; a < scanResults.size(); a++)
                        newDevices.add(new NewDevice(scanResults.get(a).SSID, scanResults.get(a).BSSID, scanResults.get(a).capabilities, scanResults.get(a).level));
                    newDevicesRecyclerViewAdapter.setItems(newDevices);
                });
    }


    @Override
    public void onItemClicked(NewDevice item) {
        newDevicesRecyclerViewAdapter.selectedSSID = item;
        newDevicesRecyclerViewAdapter.notifyDataSetChanged();
    }
}
