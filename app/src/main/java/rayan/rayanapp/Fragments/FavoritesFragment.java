package rayan.rayanapp.Fragments;

import android.animation.ValueAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.ViewModels.FavoritesFragmentViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class FavoritesFragment extends Fragment implements OnToggleDeviceListener<Device>, ToggleDeviceAnimationProgress {
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
    public void onPin1Clicked(Device Item, int position) {
        ((RayanApplication)getActivity().getApplication()).getDevicesAccessibilityBus().registerForAnimation(this, recyclerView.getLayoutManager().findViewByPosition(position).getWidth());
        favoritesFragmentViewModel.togglePin1(position, ((RayanApplication)getActivity().getApplication()), Item, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
    }

    @Override
    public void onPin2Clicked(Device Item, int position) {
        ((RayanApplication)getActivity().getApplication()).getDevicesAccessibilityBus().registerForAnimation(this, recyclerView.getLayoutManager().findViewByPosition(position).getWidth());
        favoritesFragmentViewModel.togglePin2(position, (RayanApplication) getActivity().getApplication(), Item, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
    }

    @Override
    public void toggleAnimationProgressChanged(int progress, int position) {
        Bundle b = new Bundle();
        b.putInt("progress", progress);
        devicesRecyclerViewAdapter.notifyItemChanged(position, b);
    }

    @Override
    public void stopToggleAnimation(ValueAnimator valueAnimator, int position, int currentProgress, int progressWidth) {
        getActivity().runOnUiThread(() -> {
            valueAnimator.cancel();
            valueAnimator.setIntValues(currentProgress,
                    (currentProgress +(progressWidth - currentProgress)/3),
                    (currentProgress + (progressWidth - currentProgress)/3*2),
                    progressWidth
            );
            valueAnimator.setDuration(365);
            valueAnimator.start();
        });
    }
}
