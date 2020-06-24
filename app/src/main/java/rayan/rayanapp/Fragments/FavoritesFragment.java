package rayan.rayanapp.Fragments;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
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
import rayan.rayanapp.Listeners.DevicesAndFavoritesListener;
import rayan.rayanapp.Listeners.OnDeviceClickListener;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewHolders.BaseViewHolder;
import rayan.rayanapp.ViewHolders.DeviceViewHolder1Bridge;
import rayan.rayanapp.ViewHolders.DeviceViewHolder2Bridges;
import rayan.rayanapp.ViewModels.FavoritesFragmentViewModel;

import static rayan.rayanapp.Fragments.DevicesFragment.calculateNoOfColumns;
import static rayan.rayanapp.Fragments.DevicesFragment.isTablet;

public class FavoritesFragment extends Fragment implements OnDeviceClickListener<BaseDevice>, ToggleDeviceAnimationProgress {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private final String TAG = "FavoritesFragment";
    @BindView(R.id.empty_view)
    RelativeLayout emptyView;
    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;
    FavoritesFragmentViewModel favoritesFragmentViewModel;
    DevicesFragmentRecyclerViewAdapter devicesRecyclerViewAdapter;
    Activity activity;
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
    private DeviceAnimator deviceAnimator;
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
        deviceAnimator = new DeviceAnimator();
        dp = new DialogPresenter(getActivity().getSupportFragmentManager());
        favoritesFragmentViewModel = ViewModelProviders.of(getActivity()).get(FavoritesFragmentViewModel.class);
        devicesRecyclerViewAdapter = new DevicesFragmentRecyclerViewAdapter(getContext(), this, this);
        initObservablesObservers();
        devicesObservable.observe(this, devicesObserver);
        remoteHubsObservable.observe(this, remoteHubsObserver);
        remotesObservable.observe(this, remotesObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(devicesRecyclerViewAdapter);
        if (isTablet(getActivity())) {
            recyclerView.setLayoutManager(new CustomGridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180), CustomGridLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new CustomGridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180), CustomGridLayoutManager.VERTICAL, false));
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (devicesObservable ==null && remoteHubsObservable == null && remotesObservable == null)
            initObservablesObservers();
        devicesObservable.removeObservers(this);
        devicesObservable.observe(this,devicesObserver);
        remoteHubsObservable.removeObservers(this);
        remoteHubsObservable.observe(this,remoteHubsObserver);
        remotesObservable.removeObservers(this);
        remotesObservable.observe(this, remotesObserver);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            if (devicesObservable != null) {
                devicesObservable.removeObservers(this);
                devicesObservable.observe(this, devicesObserver);
                remoteHubsObservable.removeObservers(this);
                remoteHubsObservable.observe(this, remoteHubsObserver);
                remotesObservable.removeObservers(this);
                remotesObservable.observe(this, remotesObserver);
            }
        }
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

                    d.setFavoritePosition(i+1);

                    if (baseDevices.get(i+1) instanceof Device)
                        d1 = new Device((Device)baseDevices.get(i+1));
                    else if(baseDevices.get(i+1) instanceof RemoteHub)
                        d1 = new RemoteHub((RemoteHub)baseDevices.get(i+1));
                    else if (baseDevices.get(i+1) instanceof Remote)
                        d1 = new Remote((Remote) baseDevices.get(i+1));
                    d1.setFavoritePosition(i);
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
                    d.setFavoritePosition(i-1);
                    if (baseDevices.get(i-1) instanceof Device)
                        d2 = new Device((Device)baseDevices.get(i-1));
                    else if(baseDevices.get(i-1) instanceof RemoteHub)
                        d2 = new RemoteHub((RemoteHub)baseDevices.get(i-1));
                    else if (baseDevices.get(i-1) instanceof Remote)
                        d2 = new Remote((Remote) baseDevices.get(i-1));
                    d2.setFavoritePosition(i);
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

            favoritesFragmentViewModel.updateDevices(changed);
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


    @Override
    public int getDeviceItemWidth(int position){
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
    public void turnOnDevicePin1(String chipID, int position, String type) {
//        if (!devicesAndFavoritesListener.isInDevicesFragment(chipID))
//            favoritesFragmentViewModel.playOnSound();
        deviceAnimator.deviceTurnedOnPin1(chipID, position, this, type);
    }

    @Override
    public void turnOffDevicePin1(String chipID, int position, String type) {
//        if (!devicesAndFavoritesListener.isInDevicesFragment(chipID))
//            favoritesFragmentViewModel.playOffSound();
        deviceAnimator.deviceTurnedOffPin1(chipID, position, this, type);
    }

    @Override
    public void turnOnDevicePin2(String chipID, int position, String type) {
//        if (!devicesAndFavoritesListener.isInDevicesFragment(chipID))
//            favoritesFragmentViewModel.playOnSound();
        deviceAnimator.deviceTurnedOnPin2(chipID, position, this, type);
    }

    @Override
    public void turnOffDevicePin2(String chipID, int position, String type) {
//        if (!devicesAndFavoritesListener.isInDevicesFragment(chipID))
//            favoritesFragmentViewModel.playOffSound();
        deviceAnimator.deviceTurnedOffPin2(chipID, position, this, type);
    }

    @Override
    public void sendingMessageTimeoutPin1(String chipId, int position, String type){
        Log.e("wewewewe", "HAH? " + baseDevices.size()+baseDevices);
        if (!deviceAnimator.isResponseReceivedPin1(chipId)){
            if (((Device)baseDevices.get(position)).getPin1().equals(AppConstants.ON_STATUS))
                turnOnDeviceAnimationPin1(chipId, position, type);
            else
                turnOffDeviceAnimationPin1(chipId, position, type);
        }
    }
    @Override
    public void sendingMessageTimeoutPin2(String chipId, int position, String type){
        Log.e("wewewewe", "HAH? " + baseDevices.size()+baseDevices);
        if (!deviceAnimator.isResponseReceivedPin2(chipId)){
            if (((Device)baseDevices.get(position)).getPin2().equals(AppConstants.ON_STATUS))
                turnOnDeviceAnimationPin2(chipId, position, type);
            else
                turnOffDeviceAnimationPin2(chipId, position, type);
        }
    }

    DevicesAndFavoritesListener devicesAndFavoritesListener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DevicesAndFavoritesListener)
            devicesAndFavoritesListener = (DevicesAndFavoritesListener) context;
    }

    public boolean updateIfExists(BaseDevice d, List<BaseDevice> devices){
        for (BaseDevice device: devices)
            if (d.getBaseId().equals(device.getBaseId())){
                device.setFavoritePosition(d.getFavoritePosition());
                return true;
            }
        return false;
    }

    @Override
    public void onPin1Clicked(BaseDevice Item, int position) {
        Device device = (Device) Item;
        Log.e("Pin1 Is Touching: " , "Device: " +device + position);
        Log.e(this.getClass().getSimpleName(), "Mqtt backup is On? " + RayanApplication.getPref().getIsNodeSoundOn());
        favoritesFragmentViewModel.togglePin1(dp,this, position, ((RayanApplication) getActivity().getApplication()), device);
    }

    @Override
    public void onPin2Clicked(BaseDevice Item, int position) {
        Device device = (Device) Item;
        Log.e(this.getClass().getSimpleName(), "Mqtt backup is On? " + RayanApplication.getPref().getIsNodeSoundOn());
        Log.e("Pin2 Is Touching: " , "Device: " +device);
        favoritesFragmentViewModel.togglePin2(dp,this,position, (RayanApplication) getActivity().getApplication(),device);
    }

    @Override
    public void onAccessPointChanged(BaseDevice item) {

    }

    @Override
    public void onClick_RemoteHub(BaseDevice Item, int position) {
        RemoteHub remoteHub = (RemoteHub) Item;
        Log.e("onClick_RemoteHub", "Item:"+remoteHub + position);
        SelectRemoteBottomSheetFragment selectRemoteBottomSheetFragment = SelectRemoteBottomSheetFragment.instance(remoteHub.getName());
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

    public void initObservablesObservers(){
        devicesObservable = favoritesFragmentViewModel.getAllDevicesLive();
        remoteHubsObservable = favoritesFragmentViewModel.getAllRemoteHubsLive();
        remotesObservable = favoritesFragmentViewModel.getAllRemotesLive();
        remotesObserver = new Observer<List<Remote>>() {
            @Override
            public void onChanged(@Nullable List<Remote> remotes) {
                Log.e(TAG, "Remotes Updated " + remotes.size());
                FavoritesFragment.this.remotes = remotes;
                if (FavoritesFragment.this.remotes.size() == 0 && remotes.size() > 0)
                    deviceAnimator.setItemWidth(FavoritesFragment.this);
                finalRemotes = new ArrayList<>();
                for (int a = 0; a<remotes.size();a++){
                    if (remotes.get(a).isVisibility()){
                        finalRemotes.add(remotes.get(a));
                    }
                }
                FavoritesFragment.this.remotes = finalRemotes;
                synchronized (baseDevices) {
                    baseDevices.clear();
                    baseDevices.addAll(finalDevices);
                    baseDevices.addAll(finalRemoteHubs);
                    baseDevices.addAll(finalRemotes);
                    Collections.sort(baseDevices, (obj1, obj2) -> Integer.compare(obj1.getFavoritePosition(), obj2.getFavoritePosition()));
                    Log.e("bbbbbbbbbbb", "bascoint: " + baseDevices.size());
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
                FavoritesFragment.this.remoteHubs = remoteHubs;
                if (FavoritesFragment.this.finalRemoteHubs.size() == 0 && remoteHubs.size() > 0)
                    deviceAnimator.setItemWidth(FavoritesFragment.this);
                finalRemoteHubs = new ArrayList<>();
                for (int a = 0; a<remoteHubs.size();a++){
                    if (remoteHubs.get(a).isVisibility()){
                        finalRemoteHubs.add(remoteHubs.get(a));
                    }
                }
                FavoritesFragment.this.remoteHubs = finalRemoteHubs;
                synchronized (baseDevices) {
                    baseDevices.clear();
                    baseDevices.addAll(finalDevices);
                    baseDevices.addAll(finalRemoteHubs);
                    baseDevices.addAll(finalRemotes);
                    Collections.sort(baseDevices, (obj1, obj2) -> Integer.compare(obj1.getFavoritePosition(), obj2.getFavoritePosition()));
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
                if (FavoritesFragment.this.finalDevices.size() == 0 && devices.size() > 0)
                    deviceAnimator.setItemWidth(FavoritesFragment.this);
                FavoritesFragment.this.devices = devices;
                finalDevices = new ArrayList<>();
                String currentGroup = RayanApplication.getPref().getCurrentShowingGroup();
                for (int a = 0; a<devices.size();a++){
                    if (!devices.get(a).isHidden()){
                        finalDevices.add(devices.get(a));
                    }
                }
                FavoritesFragment.this.devices = finalDevices;
                synchronized (baseDevices) {
                    baseDevices.clear();
                    baseDevices.addAll(finalDevices);
                    baseDevices.addAll(finalRemoteHubs);
                    baseDevices.addAll(finalRemotes);
                    Collections.sort(baseDevices, (obj1, obj2) -> Integer.compare(obj1.getFavoritePosition(), obj2.getFavoritePosition()));
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
    }
}
