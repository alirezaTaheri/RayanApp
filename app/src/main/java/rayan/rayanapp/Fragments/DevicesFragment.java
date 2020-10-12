package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
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
import rayan.rayanapp.Activities.RemoteActivity;
import rayan.rayanapp.Adapters.recyclerView.DevicesFragmentRecyclerViewAdapter;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Helper.DeviceAnimator;
import rayan.rayanapp.Helper.DialogPresenter;
import rayan.rayanapp.LayoutManagers.CustomGridLayoutManager;
import rayan.rayanapp.Listeners.OnDeviceClickListener;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewHolders.BaseViewHolder;
import rayan.rayanapp.ViewHolders.DeviceViewHolder1Bridge;
import rayan.rayanapp.ViewHolders.DeviceViewHolder2Bridges;
import rayan.rayanapp.ViewModels.DevicesFragmentViewModel;

public class DevicesFragment extends Fragment implements OnDeviceClickListener<BaseDevice>,ToggleDeviceAnimationProgress {
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
    public DevicesFragmentRecyclerViewAdapter devicesRecyclerViewAdapter;
    MainActivity mainActivity;
    public List<Device> devices = new ArrayList<>();
    public List<BaseDevice> baseDevices = new ArrayList<>();
    public List<RemoteHub> remoteHubs = new ArrayList<>();
    public List<Remote> remotes = new ArrayList<>();
    List<Device> finalDevices = new ArrayList<>();
    List<RemoteHub> finalRemoteHubs = new ArrayList<>();
    List<Remote> finalRemotes = new ArrayList<>();
    LiveData<List<Device>> devicesObservable;
    LiveData<List<RemoteHub>> remoteHubsObservable;
    LiveData<List<Remote>> remotesObservable;
    Observer<List<Device>> devicesObserver;
    Observer<List<RemoteHub>> remoteHubsObserver;
    Observer<List<Remote>> remotesObserver;
    private final String TAG = this.getClass().getSimpleName();
    public static List<String> subscribedDevices = new ArrayList<>();
    private DeviceAnimator deviceAnimator;
    MediatorLiveData mediatorLiveData = new MediatorLiveData();
    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceAnimator = new DeviceAnimator();
        devicesRecyclerViewAdapter = new DevicesFragmentRecyclerViewAdapter(getContext(), this, this);
        dp = new DialogPresenter(getActivity().getSupportFragmentManager());
        devicesFragmentViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DevicesFragmentViewModel.class);
//        sortDevicesByGroup(RayanApplication.getPref().getCurrentShowingGroup());
        activity = getActivity();
        devicesObservable = devicesFragmentViewModel.getAllDevicesLive();
        remoteHubsObservable = devicesFragmentViewModel.getAllRemoteHubsLive();
        remotesObservable = devicesFragmentViewModel.getAllRemotesLive();
        remotesObserver = new Observer<List<Remote>>() {
            @Override
            public void onChanged(@Nullable List<Remote> remotes) {
                Log.e(TAG, "Remotes Updated " + remotes.size());
                DevicesFragment.this.remotes = remotes;
                if (DevicesFragment.this.finalRemotes.size() == 0 && remotes.size() > 0)
                    deviceAnimator.setItemWidth(DevicesFragment.this);
                finalRemotes = new ArrayList<>();
                String currentGroup = RayanApplication.getPref().getCurrentShowingGroup();
                if (currentGroup != null)
                    for (int a = 0; a<remotes.size();a++){
                        if (remotes.get(a).getGroupId().equals(currentGroup) && remotes.get(a).isVisibility()){
                            finalRemotes.add(remotes.get(a));
                        }
                    }
                else
                    for (int a = 0; a<remotes.size();a++){
                        if (remotes.get(a).isVisibility()){
                            finalRemotes.add(remotes.get(a));
                        }
                    }

                DevicesFragment.this.remotes = finalRemotes;
                synchronized (baseDevices) {
                    baseDevices.clear();
                    baseDevices.addAll(finalDevices);
                    baseDevices.addAll(finalRemoteHubs);
                    baseDevices.addAll(finalRemotes);
                    if (RayanApplication.getPref().getCurrentShowingGroup() == null)
                        Collections.sort(baseDevices, (obj1, obj2) -> Integer.compare(obj1.getPosition(), obj2.getPosition()));
                    else Collections.sort(baseDevices, (obj1, obj2) -> Integer.compare(obj1.getInGroupPosition(), obj2.getInGroupPosition()));
                    if (baseDevices.size() == 0){
                        lottieAnimationView.setMaxProgress(0.5f);
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        emptyView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                devicesRecyclerViewAdapter.updateItems(baseDevices);
            }
        };
        remoteHubsObserver = new Observer<List<RemoteHub>>() {
            @Override
            public void onChanged(@Nullable List<RemoteHub> remoteHubs) {
                Log.e(TAG, "RemoteHubs Updated " + remoteHubs.size());
                DevicesFragment.this.remoteHubs = remoteHubs;
                if (DevicesFragment.this.finalRemoteHubs.size() == 0 && remoteHubs.size() > 0)
                    deviceAnimator.setItemWidth(DevicesFragment.this);
                finalRemoteHubs = new ArrayList<>();
                String currentGroup = RayanApplication.getPref().getCurrentShowingGroup();
                if (currentGroup != null)
                    for (int a = 0; a<remoteHubs.size();a++){
                        if (remoteHubs.get(a).getGroupId().equals(currentGroup) && remoteHubs.get(a).isVisibility()){
                            finalRemoteHubs.add(remoteHubs.get(a));
                        }
                    }
                else
                    for (int a = 0; a<remoteHubs.size();a++){
                        if (remoteHubs.get(a).isVisibility()){
                            finalRemoteHubs.add(remoteHubs.get(a));
                        }
                    }
                DevicesFragment.this.remoteHubs = finalRemoteHubs;
                synchronized (baseDevices) {
                    baseDevices.clear();
                    baseDevices.addAll(finalDevices);
                    baseDevices.addAll(finalRemoteHubs);
                    baseDevices.addAll(finalRemotes);
                    if (RayanApplication.getPref().getCurrentShowingGroup() == null)
                        Collections.sort(baseDevices, (obj1, obj2) -> Integer.compare(obj1.getPosition(), obj2.getPosition()));
                    else Collections.sort(baseDevices, (obj1, obj2) -> Integer.compare(obj1.getInGroupPosition(), obj2.getInGroupPosition()));
                    if (baseDevices.size() == 0){
                        lottieAnimationView.setMaxProgress(0.5f);
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        emptyView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                devicesRecyclerViewAdapter.updateItems(baseDevices);
            }
        };
        devicesObserver = new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable List<Device> devices) {
                Log.e(TAG, "Devices Updated " + devices.size());
                if (DevicesFragment.this.finalDevices.size() == 0 && devices.size() > 0)
                    deviceAnimator.setItemWidth(DevicesFragment.this);
                DevicesFragment.this.devices = devices;
                finalDevices = new ArrayList<>();
                String currentGroup = RayanApplication.getPref().getCurrentShowingGroup();
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
                DevicesFragment.this.devices = finalDevices;
                synchronized (baseDevices) {
                    baseDevices.clear();
                    baseDevices.addAll(finalDevices);
                    baseDevices.addAll(finalRemoteHubs);
                    baseDevices.addAll(finalRemotes);
                    if (RayanApplication.getPref().getCurrentShowingGroup() == null)
                        Collections.sort(baseDevices, (obj1, obj2) -> Integer.compare(obj1.getPosition(), obj2.getPosition()));
                    else Collections.sort(baseDevices, (obj1, obj2) -> Integer.compare(obj1.getInGroupPosition(), obj2.getInGroupPosition()));

                    if (baseDevices.size() == 0){
                        lottieAnimationView.setMaxProgress(0.5f);
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        emptyView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                devicesRecyclerViewAdapter.updateItems(baseDevices);
            }
        };
        devicesObservable.observe(this, devicesObserver);
        remoteHubsObservable.observe(this, remoteHubsObserver);
        remotesObservable.observe(this, remotesObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setItemViewCacheSize(100);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemViewCacheSize(20);
//        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
//            @Override
//            public void onViewRecycled(@NonNull RecyclerView.ViewHolder viewHolder) {
//                AppConstants.disableEnableControls(true, (ViewGroup) viewHolder.itemView);
//            }
//        });
        if (isTablet(getActivity())) {
            recyclerView.setLayoutManager(new CustomGridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180), CustomGridLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new CustomGridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180), CustomGridLayoutManager.VERTICAL, false));
        }
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(devicesRecyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    public void sortDevicesByGroup(String groupId){
        RayanApplication.getPref().saveCurrentShowingGroup(groupId);
        devicesObservable.removeObservers(this);
        devicesObservable.observe(this,devicesObserver);
        remoteHubsObservable.removeObservers(this);
        remoteHubsObservable.observe(this,remoteHubsObserver);
        remotesObservable.removeObservers(this);
        remotesObservable.observe(this, remotesObserver);
    }

    @Override
    public int getDeviceItemWidth(int position){
//        if (!(finalDevices.get(position).getType().equals(AppConstants.DEVICE_TYPE_SWITCH_1) || finalDevices.get(position).getType().equals(AppConstants.DEVICE_TYPE_PLUG)))
//            width /= 2;
        if (recyclerView.findViewHolderForAdapterPosition(position) != null)
            return ((BaseViewHolder)recyclerView.findViewHolderForAdapterPosition(position)).getItemWidth();
        else return -1;
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
        if (recyclerView.findViewHolderForAdapterPosition(position) != null){
            ((DeviceViewHolder1Bridge)recyclerView.findViewHolderForAdapterPosition(position)).updateBottomStripPin1(width);
        }
    }
    @Override
    public void updateStripPin2(int position, int width){
        if (recyclerView.findViewHolderForAdapterPosition(position) != null ){
            ((DeviceViewHolder2Bridges)recyclerView.findViewHolderForAdapterPosition(position)).updateBottomStripPin2(width);
        }
    }

    @Override
    public void turnOnDeviceAnimationPin1(String chipID, int position, String type){
        deviceAnimator.deviceTurnedOnPin1(chipID, position, this, type);
    }
    public void turnOnDevicePin1(String chipId, int position, String type){
//        devicesFragmentViewModel.playOnSound();
        turnOnDeviceAnimationPin1(chipId, position, type);
    }
    @Override
    public void turnOffDeviceAnimationPin1(String chipID, int position, String type){
        deviceAnimator.deviceTurnedOffPin1(chipID, position, this, type);
    }
    public void turnOffDevicePin1(String chipId, int position, String type){
//        devicesFragmentViewModel.playOffSound();
        turnOffDeviceAnimationPin1(chipId, position, type);
    }
    @Override
    public void turnOnDeviceAnimationPin2(String chipID, int position, String type){
        deviceAnimator.deviceTurnedOnPin2(chipID, position, this, type);
    }
    public void turnOnDevicePin2(String chipId, int position, String type){
//        devicesFragmentViewModel.playOnSound();
        turnOnDeviceAnimationPin2(chipId, position, type);
    }
    @Override
    public void turnOffDeviceAnimationPin2(String chipID, int position, String type){
        deviceAnimator.deviceTurnedOffPin2(chipID, position, this, type);
    }
    public void turnOffDevicePin2(String chipId, int position, String type){
//        devicesFragmentViewModel.playOffSound();
        turnOffDeviceAnimationPin2(chipId, position, type);
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
    public int findAdapterPositionByRealPosition(int realPosition){
        for (int a = 0;a<finalDevices.size();a++)
            if (finalDevices.get(a).getPosition() == realPosition)
                return a;
        return -1;
    }
//    @Override
//    public void startToggleAnimationPin1(String chipId, int position) {
//        Bundle b = new Bundle();
//        b.putString("startTogglingPin1", "startTogglingPin1");
//        b.putString("chipId", chipId);
//        b.putString("status", devicesRecyclerViewAdapter.getItem(position).getPort1().equals(AppConstants.ON_STATUS)?AppConstants.ON_STATUS:AppConstants.OFF_STATUS);
//        devicesRecyclerViewAdapter.notifyItemChanged(position,b);
//    }
//
//    @Override
//    public void startToggleAnimationPin2(String chipId, int position) {
//        Bundle b = new Bundle();
//        b.putString("startTogglingPin2", "startTogglingPin2");
//        b.putString("chipId", chipId);
//        b.putString("status", devicesRecyclerViewAdapter.getItem(position).getPort2().equals(AppConstants.ON_STATUS)?AppConstants.ON_STATUS:AppConstants.OFF_STATUS);
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
            if(dragFrom == -1) {
                dragFrom =  fromPosition;
            }
            dragTo = toPosition;

            devicesRecyclerViewAdapter.onItemMove(fromPosition, toPosition);

            return true;
        }

        private void reallyMoved(int from, int to) {
            List<BaseDevice> changed = new ArrayList<>();
            if (from < to) {
                for (int i = from; i < to; i++) {
                    BaseDevice d=null;
                    BaseDevice d1=null;
                    if (baseDevices.get(i) instanceof Device)
                        d = new Device((Device)baseDevices.get(i));
                    else if(baseDevices.get(i) instanceof RemoteHub)
                        d = new RemoteHub((RemoteHub)baseDevices.get(i));
                    else if (baseDevices.get(i) instanceof Remote)
                        d = new Remote((Remote) baseDevices.get(i));
                    if (RayanApplication.getPref().getCurrentShowingGroup() == null)
                        d.setPosition(i+1);
                    else d.setInGroupPosition(i+1);
                    if (baseDevices.get(i+1) instanceof Device)
                        d1 = new Device((Device)baseDevices.get(i+1));
                    else if(baseDevices.get(i+1) instanceof RemoteHub)
                        d1 = new RemoteHub((RemoteHub)baseDevices.get(i+1));
                    else if (baseDevices.get(i+1) instanceof Remote)
                        d1 = new Remote((Remote) baseDevices.get(i+1));
                    if (RayanApplication.getPref().getCurrentShowingGroup() == null)
                        d1.setPosition(i);
                    else d1.setInGroupPosition(i);
                    Collections.swap(baseDevices, i, i + 1);
                        changed.add(d);
                        changed.add(d1);
                }
            } else {
                for (int i = from; i > to; i--) {
                    BaseDevice d=null;
                    BaseDevice d2=null;
                    if (baseDevices.get(i) instanceof Device)
                        d = new Device((Device)baseDevices.get(i));
                    else if(baseDevices.get(i) instanceof RemoteHub)
                        d = new RemoteHub((RemoteHub)baseDevices.get(i));
                    else if (baseDevices.get(i) instanceof Remote)
                        d = new Remote((Remote) baseDevices.get(i));
                    if (RayanApplication.getPref().getCurrentShowingGroup() == null)
                        d.setPosition(i-1);
                    else d.setInGroupPosition(i-1);
                    if (baseDevices.get(i-1) instanceof Device)
                        d2 = new Device((Device)baseDevices.get(i-1));
                    else if(baseDevices.get(i-1) instanceof RemoteHub)
                        d2 = new RemoteHub((RemoteHub)baseDevices.get(i-1));
                    else if (baseDevices.get(i-1) instanceof Remote)
                        d2 = new Remote((Remote) baseDevices.get(i-1));
                    if (RayanApplication.getPref().getCurrentShowingGroup() == null)
                        d2.setPosition(i);
                    else d2.setInGroupPosition(i);
                    Collections.swap(baseDevices, i, i - 1);
                        changed.add(d);
                        changed.add(d2);
                }
            }
            for (int a=0;a<changed.size();a++){
                BaseDevice changedBaseDevices = changed.get(a);
                if (changedBaseDevices instanceof Device)
                    for (int b = 0;b<devices.size();b++){
                        Device currentDevice = devices.get(b);
                        if (currentDevice.getBaseId().equals(changedBaseDevices.getBaseId()))
                            devices.set(b,(Device)changedBaseDevices);
                    }
                else if (changedBaseDevices instanceof RemoteHub)
                    for (int b = 0;b<remoteHubs.size();b++){
                        RemoteHub currentRemoteHub= remoteHubs.get(b);
                        if (currentRemoteHub.getBaseId().equals(changedBaseDevices.getBaseId()))
                            remoteHubs.set(b,(RemoteHub) changedBaseDevices);
                    }
                else if (changedBaseDevices instanceof Remote)
                    for (int b = 0;b<remotes.size();b++){
                        Remote currentRemote= remotes.get(b);
                        if (currentRemote.getBaseId().equals(changedBaseDevices.getBaseId()))
                            remotes.set(b,(Remote) changedBaseDevices);
                    }
            }

            devicesFragmentViewModel.updateDevices(changed);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
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

    public List<Device> getFinalDevices(){
        return finalDevices;
    }

    public boolean updateIfExists(BaseDevice d, List<BaseDevice> devices, String selectedGroup){
        for (BaseDevice device: devices)
            if (d.getBaseId().equals(device.getBaseId())){
            if (selectedGroup == null)
                device.setPosition(d.getPosition());
            else device.setInGroupPosition(d.getInGroupPosition());
                return true;
            }
        return false;
    }

    @Override
    public void onPin1Clicked(BaseDevice Item, int position) {
        Device device = (Device) Item;
        Log.e("Pin1 Is Touching: " , "Device: " +device + position);
        Log.e(this.getClass().getSimpleName(), "Mqtt backup is On? " + RayanApplication.getPref().getIsNodeSoundOn());
        devicesFragmentViewModel.togglePin1(dp,this, position, ((RayanApplication) getActivity().getApplication()), device);
    }

    @Override
    public void onPin2Clicked(BaseDevice Item, int position) {
        Device device = (Device) Item;
        Log.e(this.getClass().getSimpleName(), "Mqtt backup is On? " + RayanApplication.getPref().getIsNodeSoundOn());
        Log.e("Pin2 Is Touching: " , "Device: " +device);
        devicesFragmentViewModel.togglePin2(dp,this,position, (RayanApplication) getActivity().getApplication(),device);
    }

    @Override
    public void onAccessPointChanged(BaseDevice item) {

    }

    @Override
    public void onClick_RemoteHub(BaseDevice Item, int position) {
        RemoteHub remoteHub = (RemoteHub) Item;
        Log.e("onClick_RemoteHub", "Item:"+remoteHub + position);
        SelectRemoteBottomSheetFragment selectRemoteBottomSheetFragment = SelectRemoteBottomSheetFragment.instance(remoteHub);
        selectRemoteBottomSheetFragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onClick_Remote(BaseDevice item, int position) {
        Remote remote = (Remote) item;
        Log.e("onClick_Remote", "Item:"+remote+ position);
        Intent intent = new Intent(activity, RemoteActivity.class);
        intent.putExtra("type", remote.getType());
        startActivity(intent);
    }
}
