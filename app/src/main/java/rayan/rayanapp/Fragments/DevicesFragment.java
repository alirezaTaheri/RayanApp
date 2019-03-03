package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.Listeners.OnStatusIconClickListener;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.ViewModels.DevicesFragmentViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class DevicesFragment extends Fragment implements OnStatusIconClickListener {
    DevicesFragmentViewModel devicesFragmentViewModel;

    public DevicesFragment() {}
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    List<Device> devices = new ArrayList<>();
    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(getContext(), devices);
        devicesRecyclerViewAdapter.setListener(this);
        devicesFragmentViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DevicesFragmentViewModel.class);
        devicesFragmentViewModel.getAllDevices().observe(this, devices -> {
         devicesRecyclerViewAdapter.updateItems(devices);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, view);
        if (isTablet(getActivity())) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180)));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180)));
        }


        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(devicesRecyclerViewAdapter);
        return view;
    }


    @Override
    public void onPin1Clicked(Object Item) {
            devicesFragmentViewModel.togglePin1((Device) Item, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));

    }

    @Override
    public void onPin2Clicked(Object Item) {
        devicesFragmentViewModel.togglePin2((Device) Item, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
    }

    @Override
    public void onResume() {
        super.onResume();
        devicesFragmentViewModel.getGroups();
    }

    public static int calculateNoOfColumns(Context context, float columnWidthDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    public static boolean isTablet(Context ctx){
        return (ctx.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
