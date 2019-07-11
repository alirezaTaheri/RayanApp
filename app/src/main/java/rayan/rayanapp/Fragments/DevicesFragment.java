package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
//<<<<<<< HEAD
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
//=======
import android.util.DisplayMetrics;
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.DeviceAnimator;
import rayan.rayanapp.Helper.DialogPresenter;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.ViewHolders.DeviceViewHolder1Bridge;
import rayan.rayanapp.ViewHolders.DeviceViewHolder2Bridges;
import rayan.rayanapp.ViewModels.DevicesFragmentViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class DevicesFragment extends Fragment implements OnToggleDeviceListener<Device>,ToggleDeviceAnimationProgress {
    public DevicesFragmentViewModel devicesFragmentViewModel;
    Activity activity;
    DialogPresenter dp;
    public DevicesFragment() {}
    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    RelativeLayout emptyView;
    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;
    public DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    public List<Device> devices = new ArrayList<>();
    List<Device> finalDevices = new ArrayList<>();
    LiveData<List<Device>> devicesObservable;
    MainActivity mainActivity;
    Observer<List<Device>> devicesObserver;
    private final String TAG = this.getClass().getSimpleName();
    public static List<String> subscribedDevices = new ArrayList<>();
    private DeviceAnimator deviceAnimator;
    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceAnimator = new DeviceAnimator();
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(getContext(), devices, this);
        devicesRecyclerViewAdapter.setListener(this);
        dp = new DialogPresenter(getActivity().getSupportFragmentManager());
        devicesFragmentViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DevicesFragmentViewModel.class);
//        sortDevicesByGroup(RayanApplication.getPref().getCurrentShowingGroup());
        devicesObservable = devicesFragmentViewModel.getAllDevicesLive();
        devicesObserver = new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable List<Device> devices) {
                DevicesFragment.this.devices = devices;
                finalDevices = new ArrayList<>();
                String currentGroup = RayanApplication.getPref().getCurrentShowingGroup();
                Log.e(TAG ,"All Devices: " + devices.subList(0, devices.size()/3));
                Log.e(TAG ,"All Devices: " + devices.subList(devices.size()/3,devices.size()/3*2));
                Log.e(TAG ,"All Devices: " + devices.subList(devices.size()/3*2, devices.size()));
                Log.e(TAG ,"currentGroup: " + currentGroup);
                if (currentGroup != null)
                    for (int a = 0; a<devices.size();a++){
                        if (devices.get(a).getGroupId().equals(currentGroup) && !devices.get(a).isHidden()){
                            finalDevices.add(devices.get(a));
                        }
                }
                        else
                    for (int a = 0; a<devices.size();a++){
                        if (!devices.get(a).isHidden()){
                            finalDevices.add(devices.get(a));
                        }
                    }
                    if (finalDevices.size() == 0){
                        lottieAnimationView.setMaxProgress(0.5f);
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        emptyView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
            Collections.sort(finalDevices, (obj1, obj2) -> Integer.compare(obj1.getPosition(), obj2.getPosition()));
                Log.e(TAG ,"ShowingDevices: " + finalDevices);
                devicesRecyclerViewAdapter.updateItems(finalDevices);
                DevicesFragment.this.devices = finalDevices;
            }
        };
        devicesObservable.observe(this, devicesObserver);
        activity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, view);
//        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
//            @Override
//            public void onViewRecycled(@NonNull RecyclerView.ViewHolder viewHolder) {
//                AppConstants.disableEnableControls(true, (ViewGroup) viewHolder.itemView);
//            }
//        });
        if (isTablet(getActivity())) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180)));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180)));
        }
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(devicesRecyclerViewAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    public void sortDevicesByGroup(String groupId){
        RayanApplication.getPref().saveCurrentShowingGroup(groupId);
        devicesObservable.removeObservers(this);
        devicesObservable.observe(this,devicesObserver);
    }

    @Override
    public void onPin1Clicked(Device device, int position) {
        Log.e("Pin1 Is Touching: " , "Device: " +device + position);
        Log.e(this.getClass().getSimpleName(), "Mqtt backup is On? " + RayanApplication.getPref().getIsNodeSoundOn());
        devicesFragmentViewModel.togglePin1(dp,this, position, ((RayanApplication) getActivity().getApplication()), device);
    }

    @Override
    public void onPin2Clicked(Device device, int position) {
        Log.e(this.getClass().getSimpleName(), "Mqtt backup is On? " + RayanApplication.getPref().getIsNodeSoundOn());
        Log.e("Pin2 Is Touching: " , "Device: " +device);
        devicesFragmentViewModel.togglePin2(dp,this,position, (RayanApplication) getActivity().getApplication(),device);
        }

    @Override
    public void onAccessPointChanged(Device item) {
//        ((RayanApplication)getActivity().getApplication()).getMtd().updateDevice(item);
    }

    @Override
    public int getDeviceItemWidth(int position){
//        if (!(finalDevices.get(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1) || finalDevices.get(position).getType().equals(AppConstants.DEVICE_TYPE_PLUG)))
//            width /= 2;
        return ((DeviceViewHolder1Bridge)recyclerView.findViewHolderForAdapterPosition(position)).getItemWidth();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public DeviceAnimator getDeviceAnimator() {
        return deviceAnimator;
    }

    @Override
    public void updateStripPin1(int position, int width){
        if (recyclerView.findViewHolderForAdapterPosition(position) != null)
            ((DeviceViewHolder1Bridge)recyclerView.findViewHolderForAdapterPosition(position)).updateBottomStripPin1(width);
    }
    @Override
    public void updateStripPin2(int position, int width){
        if (recyclerView.findViewHolderForAdapterPosition(position) != null)
            ((DeviceViewHolder2Bridges)recyclerView.findViewHolderForAdapterPosition(position)).updateBottomStripPin2(width);
    }
    @Override
    public void turnOnDeviceAnimationPin1(String chipID, int position, String type){
        deviceAnimator.deviceTurnedOnPin1(chipID, position, this, type);
    }
    @Override
    public void turnOffDeviceAnimationPin1(String chipID, int position, String type){
        deviceAnimator.deviceTurnedOffPin1(chipID, position, this, type);
    }
    @Override
    public void turnOnDeviceAnimationPin2(String chipID, int position, String type){
        deviceAnimator.deviceTurnedOnPin2(chipID, position, this, type);
    }
    @Override
    public void turnOffDeviceAnimationPin2(String chipID, int position, String type){
        deviceAnimator.deviceTurnedOffPin2(chipID, position, this, type);
    }
    @Override
    public void sendingMessageTimeoutPin1(String chipId, int position, String type){
        if (position<finalDevices.size())
        if (!deviceAnimator.isResponseReceivedPin1(chipId)){
            if (finalDevices.get(position).getPin1().equals(AppConstants.ON_STATUS))
                turnOnDeviceAnimationPin1(chipId, position, type);
            else
                turnOffDeviceAnimationPin1(chipId, position, type);
        }
    }
    @Override
    public void sendingMessageTimeoutPin2(String chipId, int position, String type){
        if (position<finalDevices.size())
        if (!deviceAnimator.isResponseReceivedPin2(chipId)){
            if (finalDevices.get(position).getPin2().equals(AppConstants.ON_STATUS))
                turnOnDeviceAnimationPin2(chipId, position, type);
            else
                turnOffDeviceAnimationPin2(chipId, position, type);
        }
    }
//    @Override
//    public void startToggleAnimationPin1(String chipId, int position) {
//        Bundle b = new Bundle();
//        b.putString("startTogglingPin1", "startTogglingPin1");
//        b.putString("chipId", chipId);
//        b.putString("status", devicesRecyclerViewAdapter.getItem(position).getPin1().equals(AppConstants.ON_STATUS)?AppConstants.ON_STATUS:AppConstants.OFF_STATUS);
//        devicesRecyclerViewAdapter.notifyItemChanged(position,b);
//    }
//
//    @Override
//    public void startToggleAnimationPin2(String chipId, int position) {
//        Bundle b = new Bundle();
//        b.putString("startTogglingPin2", "startTogglingPin2");
//        b.putString("chipId", chipId);
//        b.putString("status", devicesRecyclerViewAdapter.getItem(position).getPin2().equals(AppConstants.ON_STATUS)?AppConstants.ON_STATUS:AppConstants.OFF_STATUS);
//        devicesRecyclerViewAdapter.notifyItemChanged(position,b);
//    }
//
//    @Override
//    public void stopToggleAnimationPin1(String chipId) {
//            Bundle b = new Bundle();
//            int position = findDevicePosition(chipId);
//            if (position != -1) {
//                b.putString("stopToggleAnimationPin1", chipId + " stopToggleAnimationPin1 for position: " + position);
//                b.putString("chipId", devicesRecyclerViewAdapter.getItem(position).getChipId());
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        devicesRecyclerViewAdapter.notifyItemChanged(position, b);
//                        Log.e(this.getClass().getSimpleName(), "InFragment stopping animation pin 111");
//                    }
//                });
//            }else
//                Log.e(TAG+" Pinn1", "Can not find Device with: " +chipId+" among list Items...");
//    }
//
//    @Override
//    public void stopToggleAnimationPin2(String chipId) {
//        Bundle b = new Bundle();
//        int position = findDevicePosition(chipId);
//        if (position != -1) {
//            b.putString("stopToggleAnimationPin2", chipId + " stopToggleAnimationPin2 for position: " + position);
//            b.putString("chipId", devicesRecyclerViewAdapter.getItem(position).getChipId());
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    devicesRecyclerViewAdapter.notifyItemChanged(position, b);
//                    Log.e(this.getClass().getSimpleName(), "InFragment stopping animation pin 222");
//                }
//            });
//        }else
//            Log.e(TAG+" Pinn2", "Can not find Device with: " +chipId+" among list Items...");
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((RayanApplication)getActivity().getApplication()).getNetworkStatus().observe(getActivity(), networkConnection -> {
//            devicesRecyclerViewAdapter.updateItems(devices);
//        });
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
            Log.e("///////////" , "onMove From: " + fromPosition + " to: " + toPosition);
            if(dragFrom == -1) {
                dragFrom =  fromPosition;
            }
            dragTo = toPosition;

            devicesRecyclerViewAdapter.onItemMove(fromPosition, toPosition);

            return true;
        }

        private void reallyMoved(int from, int to) {
//            Log.e("///////////" , "reallyMoved From: " + from + " to: " + to);
//            Log.e("oooooooooooo" , "reallyMoved: oldList:: " + devices);
            Bundle b = new Bundle();
            b.putString("position", "position");
            if (from < to) {
                for (int i = from; i < to; i++) {
                    Device d = devices.get(i);
                    d.setPosition(i+1);
                    Device d1 = devices.get(i+1);
                    d1.setPosition(i);
                    Collections.swap(devices, i, i + 1);
                    List<Device> changed = new ArrayList<>();
                    changed.add(d);
                    changed.add(d1);
                    devicesRecyclerViewAdapter.notifyItemChanged(i,b);
                    devicesRecyclerViewAdapter.notifyItemChanged(i+1,b);
                    devicesFragmentViewModel.updateDevices(changed);
                }
            } else {
                for (int i = from; i > to; i--) {
                    Device d = devices.get(i);
                    d.setPosition(i-1);
                    Device d2 = devices.get(i-1);
                    d2.setPosition(i);
                    Collections.swap(devices, i, i - 1);
                    List<Device> changed = new ArrayList<>();
                    changed.add(d);
                    changed.add(d2);
                    devicesRecyclerViewAdapter.notifyItemChanged(i,b);
                    devicesRecyclerViewAdapter.notifyItemChanged(i-1,b);
                    devicesFragmentViewModel.updateDevices(changed);
                }
            }
//            Log.e("nnnnnnnnnnnnn" , "reallyMoved: NewList:: " + devices);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//            Log.e("///////////" , "////////onSwiped: " + direction);
        }

        @Override
        public boolean isLongPressDragEnabled() {
//            Log.e("///////////" , "isLongPressDragEnabled"+devices);
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
//            Log.e("///////////" , "isItemViewSwipeEnabled");
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

}
