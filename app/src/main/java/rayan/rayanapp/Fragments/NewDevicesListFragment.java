package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Adapters.recyclerView.NewDevicesRecyclerViewAdapter;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.NewDevicesListViewModel;
import rayan.rayanapp.Wifi.WifiHandler;

public class NewDevicesListFragment extends Fragment implements OnNewDeviceClicked<NewDevice> {

    private NewDevicesListViewModel viewModel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    NewDevicesRecyclerViewAdapter newDevicesRecyclerViewAdapter;
    public static NewDevicesListFragment newInstance() {
        return new NewDevicesListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newDevicesRecyclerViewAdapter = new NewDevicesRecyclerViewAdapter(getActivity());
        newDevicesRecyclerViewAdapter.setListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_devices_list, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewModel = ViewModelProviders.of(this).get(NewDevicesListViewModel.class);
        viewModel.scan();
        newDevicesRecyclerViewAdapter.setItems(viewModel.getSSIDs());
        recyclerView.setAdapter(newDevicesRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClicked(NewDevice item) {
        WifiHandler.connectToSSID(getActivity(),item.getSSID(), "12345678");
    }
}
