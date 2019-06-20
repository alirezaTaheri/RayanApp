package rayan.rayanapp.Fragments;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.DialogPresenter;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.ViewModels.FavoritesFragmentViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class FavoritesFragment extends Fragment implements OnToggleDeviceListener<Device>, ToggleDeviceAnimationProgress {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.empty_view)
    RelativeLayout emptyView;
    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;
    List<Device> devices = new ArrayList<>();
    FavoritesFragmentViewModel favoritesFragmentViewModel;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    Activity activity;
    LiveData<List<Device>> favoritesObservable;
    Observer<List<Device>> favoritesObserver;
    DialogPresenter dp;
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
        activity = getActivity();
        dp = new DialogPresenter(getActivity().getSupportFragmentManager());
        favoritesFragmentViewModel = ViewModelProviders.of(getActivity()).get(FavoritesFragmentViewModel.class);
        favoritesObservable = favoritesFragmentViewModel.getAllDevicesLive();
        favoritesObserver = new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable List<Device> devices) {
                List<Device> finalDevices = new ArrayList<>();
                String currentGroup = RayanApplication.getPref().getCurrentShowingGroup();
//                Log.e(FavoritesFragment.this.getClass().getSimpleName() ,"All Devices: " + devices.subList(0, devices.size()/3));
//                Log.e(FavoritesFragment.this.getClass().getSimpleName() ,"All Devices: " + devices.subList(devices.size()/3,devices.size()/3*2));
//                Log.e(FavoritesFragment.this.getClass().getSimpleName() ,"All Devices: " + devices.subList(devices.size()/3*2, devices.size()));
//                Log.e(FavoritesFragment.this.getClass().getSimpleName() ,"currentGroup: " + currentGroup);
                if (currentGroup != null)
                    for (int a = 0; a<devices.size();a++){
                        if (devices.get(a).getGroupId().equals(currentGroup)){
                            finalDevices.add(devices.get(a));
                        }
                    }
                else finalDevices = devices;
                if (finalDevices.size() == 0){
                    lottieAnimationView.setMaxProgress(0.5f);
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else{
                    emptyView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
//                ((RayanApplication)getActivity().getApplication()).getMtd().updateDevices(finalDevices);
                Collections.sort(finalDevices, new Comparator<Device>(){
                    public int compare(Device obj1, Device obj2) {
                        // ## Ascending order
//                    return obj1.firstName.compareToIgnoreCase(obj2.firstName); // To compare string values
                        return Integer.compare(obj1.getPosition(), obj2.getPosition()); // To compare integer values
                        // ## Descending order
                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                        // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                    }
                });
//                Log.e(FavoritesFragment.this.getClass().getSimpleName() ,"ShowingFavoriteDevices: " + finalDevices);
                devicesRecyclerViewAdapter.updateItems(finalDevices);
                FavoritesFragment.this.devices = finalDevices;
            }
        };
        favoritesObservable.observe(this,favoritesObserver);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(getContext(), devices);
        devicesRecyclerViewAdapter.setListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(devicesRecyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }


    @Override
    public void onPin1Clicked(Device device, int position) {
        Log.e(this.getClass().getSimpleName(), "Mqtt backup is On? " + RayanApplication.getPref().getIsNodeSoundOn());
        Log.e(this.getClass().getSimpleName()," Pin1 Is Touching: " + "Device: " +device);
        favoritesFragmentViewModel.togglePin1(dp,this, position, ((RayanApplication) getActivity().getApplication()), device);
    }

    @Override
    public void onPin2Clicked(Device device, int position) {
        Log.e(this.getClass().getSimpleName(), "Mqtt backup is On? " + RayanApplication.getPref().getIsNodeSoundOn());
        Log.e(this.getClass().getSimpleName()," Pin1 Is Touching: " + "Device: " +device);
        favoritesFragmentViewModel.togglePin2(dp,this,position, (RayanApplication) getActivity().getApplication(),device);
    }


    @Override
    public void startToggleAnimationPin1(String chipId, int position) {
        Bundle b = new Bundle();
        b.putString("startTogglingPin1", "startTogglingPin1");
        b.putString("chipId", chipId);
        b.putString("status", devicesRecyclerViewAdapter.getItem(position).getPin1().equals(AppConstants.ON_STATUS)?AppConstants.ON_STATUS:AppConstants.OFF_STATUS);
        devicesRecyclerViewAdapter.notifyItemChanged(position,b);
    }

    @Override
    public void startToggleAnimationPin2(String chipId, int position) {
        Bundle b = new Bundle();
        b.putString("startTogglingPin2", "startTogglingPin2");
        b.putString("chipId", chipId);
        b.putString("status", devicesRecyclerViewAdapter.getItem(position).getPin2().equals(AppConstants.ON_STATUS)?AppConstants.ON_STATUS:AppConstants.OFF_STATUS);
        devicesRecyclerViewAdapter.notifyItemChanged(position,b);
    }

    @Override
    public void stopToggleAnimationPin1(String chipId) {
        Bundle b = new Bundle();
        int position = findDevicePosition(chipId);
        b.putString("stopToggleAnimationPin1", "stopToggleAnimationPin1");
        b.putString("chipId", devicesRecyclerViewAdapter.getItem(position).getChipId());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                devicesRecyclerViewAdapter.notifyItemChanged(position,b);
                Log.e(this.getClass().getSimpleName(), "InFragment stopping animation pin 111");
            }
        });
    }

    @Override
    public void stopToggleAnimationPin2(String chipId) {
        Bundle b = new Bundle();
        int position = findDevicePosition(chipId);
        b.putString("stopToggleAnimationPin2", "stopToggleAnimationPin2");
        b.putString("chipId", devicesRecyclerViewAdapter.getItem(position).getChipId());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                devicesRecyclerViewAdapter.notifyItemChanged(position,b);
                Log.e(this.getClass().getSimpleName(), "InFragment stopping animation pin 222");
            }
        });
    }

    public int findDevicePosition(String chipId){
        for (int a = 0;a<devices.size();a++)
            if (chipId.equals(devices.get(a).getChipId())) return a;
        return -1;
    }
    private ItemTouchHelper.Callback dragCallback = new ItemTouchHelper.Callback() {

        int dragFrom = -1;
        int dragTo = -1;

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT,
                    0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Log.e("///////////" , "onMove:new List:: " + devices);
            Log.e("///////////" , "onMove From: " + fromPosition+" To: " + toPosition);
            if(dragFrom == -1) {
                dragFrom =  fromPosition;
            }
            dragTo = toPosition;
            devicesRecyclerViewAdapter.onItemMove(fromPosition, toPosition);
            return true;
        }

        private void reallyMoved(int from, int to) {
            Log.e("///////////" , "reallyMoved From: " + from);
            Log.e("///////////" , "reallyMoved To: " + to);
            Log.e("oooooooooooo" , "reallyMoved: oldList:: " + devices);
            if (from < to) {
                for (int i = from; i < to; i++) {
                    Device d = devices.get(i);
                    d.setPosition(i+1);
                    Device d1 = devices.get(i+1);
                    d1.setPosition(i);
                    Collections.swap(devices, i, i + 1);
                    favoritesFragmentViewModel.updateDevice(d);
                    favoritesFragmentViewModel.updateDevice(d1);
                }
            } else {
                for (int i = from; i > to; i--) {
                    Device d = devices.get(i);
                    d.setPosition(i-1);
                    Device d2 = devices.get(i-1);
                    d2.setPosition(i);
                    Collections.swap(devices, i, i - 1);
                    favoritesFragmentViewModel.updateDevice(d);
                    favoritesFragmentViewModel.updateDevice(d2);
                }
            }
            Log.e("nnnnnnnnnnnnn" , "reallyMoved: NewList:: " + devices);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.e("///////////" , "////////onSwiped: " + direction);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            Log.e("///////////" , "isLongPressDragEnabled"+devices);
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            Log.e("///////////" , "isItemViewSwipeEnabled");
            return false;
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            Log.e("///////////" , "////////clearView: ");
            super.clearView(recyclerView, viewHolder);
            if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                reallyMoved(dragFrom, dragTo);
            }
            dragFrom = dragTo = -1;
        }

    };

    @Override
    public void onAccessPointChanged(Device item) {
//        ((RayanApplication)getActivity().getApplication()).getMtd().updateDevice(item);
    }
}
