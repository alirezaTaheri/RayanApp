package rayan.rayanapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.Listeners.OnStatusIconClickListener;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.ViewModels.FavoritesFragmentViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class FavoritesFragment extends Fragment implements OnStatusIconClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    List<Device> devices = new ArrayList<>();
    FavoritesFragmentViewModel favoritesFragmentViewModel;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    public FavoritesFragment() {
    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoritesFragmentViewModel = ViewModelProviders.of(getActivity()).get(FavoritesFragmentViewModel.class);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(getContext(), devices);
        devicesRecyclerViewAdapter.setListener(this);
        favoritesFragmentViewModel.getAllDevices().observe(this, new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable List<Device> devices) {
                devicesRecyclerViewAdapter.updateItems(devices);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(devicesRecyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        return view;
    }

    @Override
    public void onPin1Clicked(Object Item) {
        favoritesFragmentViewModel.togglePin1((Device) Item, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
    }

    @Override
    public void onPin2Clicked(Object Item) {
        favoritesFragmentViewModel.togglePin2((Device) Item, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
    }
}
