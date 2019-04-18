package rayan.rayanapp.Fragments;

import android.animation.ValueAnimator;
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
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Services.mqtt.model.Subscription;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;
import rayan.rayanapp.ViewModels.DevicesFragmentViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class DevicesFragment extends Fragment implements OnToggleDeviceListener<Device>,ToggleDeviceAnimationProgress {
    public DevicesFragmentViewModel devicesFragmentViewModel;
    Activity activity;

    public DevicesFragment() {}
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    List<Device> devices = new ArrayList<>();
    LiveData<List<Device>> devicesObservable;
    Observer<List<Device>> devicesObserver;
    private final String TAG = this.getClass().getSimpleName();
    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(getContext(), devices);
        devicesRecyclerViewAdapter.setListener(this);
        devicesFragmentViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DevicesFragmentViewModel.class);
//        sortDevicesByGroup(RayanApplication.getPref().getCurrentShowingGroup());
        devicesObservable = devicesFragmentViewModel.getAllDevices();
        devicesObserver = new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable List<Device> devices) {
                List<Device> finalDevices = new ArrayList<>();
                String currentGroup = RayanApplication.getPref().getCurrentShowingGroup();
                Log.e(TAG ,"All Devices: " + devices.subList(0, devices.size()/3));
                Log.e(TAG ,"All Devices: " + devices.subList(devices.size()/3,devices.size()/3*2));
                Log.e(TAG ,"All Devices: " + devices.subList(devices.size()/3*2, devices.size()));
                Log.e(TAG ,"currentGroup: " + currentGroup);
                if (currentGroup != null)
                    for (int a = 0; a<devices.size();a++){
                        if (devices.get(a).getGroupId().equals(currentGroup)){
                            finalDevices.add(devices.get(a));
                        }
                }
                        else finalDevices = devices;
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
//                DevicesDiffCallBack c = new DevicesDiffCallBack(finalDevices, DevicesFragment.this.devices);
//                DiffUtil.DiffResult d = DiffUtil.calculateDiff(c);
//                List<Device> finalDevices1 = finalDevices;
//                d.dispatchUpdatesTo(new ListUpdateCallback() {
//                    @Override
//                    public void onInserted(int i, int i1) {
//
//                    }
//
//                    @Override
//                    public void onRemoved(int i, int i1) {
//
//                    }
//
//                    @Override
//                    public void onMoved(int i, int i1) {
//
//                    }
//
//                    @Override
//                    public void onChanged(int i, int i1, @Nullable Object o) {
//                        ((RayanApplication)getActivity().getApplication()).getMtd().updateDevices(finalDevices1.subList(i, i + i1));
//                    }
//                });
                ((RayanApplication)getActivity().getApplication()).getMtd().setDevices(finalDevices);
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
        if (isTablet(getActivity())) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180)));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(getActivity(),180)));
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
    }

    @Override
    public void onPin1Clicked(Device device, int position) {
        if (!((RayanApplication)getActivity().getApplication()).getDevicesAccessibilityBus().isWaitingPin1(device.getChipId()))
            Log.e("Pin1 Is Touching: " , "Device: " +device);
//        if (RayanApplication.getPref().getProtocol().equals(AppConstants.UDP)) {
//            if (device.getIp() != null)
                devicesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, true);
//            else{
////                devicesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, true);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else {
//            if (device.getTopic() != null){
//                devicesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, false);
//            }
//            else{
////                devicesFragmentViewModel.togglePin1(this, position, ((RayanApplication) getActivity().getApplication()), device, false);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
//            }
//        }

    }

    @Override
    public void onPin2Clicked(Device device, int position) {
//        Log.e("Pin2 Is Touching: " , "Device: " +device);
////        ((RayanApplication)getActivity().getApplication()).getDevicesAccessibilityBus().registerForAnimation(this, device.getType().equals(AppConstants.DEVICE_TYPE_SWITCH_2)? recyclerView.getLayoutManager().findViewByPosition(position).getWidth()/2:recyclerView.getLayoutManager().findViewByPosition(position).getWidth());
//        if (RayanApplication.getPref().getProtocol().equals(AppConstants.UDP)) {
//            if (device.getIp()!= null)
                devicesFragmentViewModel.togglePin2(this,position, (RayanApplication) getActivity().getApplication(),device, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
//            else{
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
////                devicesFragmentViewModel.togglePin2(this, position, (RayanApplication) getActivity().getApplication(),device, RayanApplication.getPref().getProtocol().equals(AppConstants.UDP));
//            }
//        }
//        else {
//            if (device.getTopic() != null){
//                devicesFragmentViewModel.togglePin2(this,position, ((RayanApplication) getActivity().getApplication()), device, false);
//            }
//            else{
////                devicesFragmentViewModel.togglePin2(this, position, ((RayanApplication) getActivity().getApplication()), device, false);
//                Toast.makeText(getActivity(), "دستگاه در دسترس نمی‌باشد", Toast.LENGTH_SHORT).show();
//            }
//        }

        }

    @Override
    public void onAccessPointChanged(Device item) {
        ((RayanApplication)getActivity().getApplication()).getMtd().updateDevice(item);
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
            if (position != -1) {
                b.putString("stopToggleAnimationPin1", "stopToggleAnimationPin1");
                b.putString("chipId", devicesRecyclerViewAdapter.getItem(position).getChipId());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        devicesRecyclerViewAdapter.notifyItemChanged(position, b);
                        Log.e(this.getClass().getSimpleName(), "InFragment stopping animation pin 111");
                    }
                });
            }
    }

    @Override
    public void stopToggleAnimationPin2(String chipId) {
        Bundle b = new Bundle();
        int position = findDevicePosition(chipId);
        if (position != -1) {
            b.putString("stopToggleAnimationPin2", "stopToggleAnimationPin2");
            b.putString("chipId", devicesRecyclerViewAdapter.getItem(position).getChipId());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    devicesRecyclerViewAdapter.notifyItemChanged(position, b);
                    Log.e(this.getClass().getSimpleName(), "InFragment stopping animation pin 222");
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        devicesFragmentViewModel.getGroups();
        ((RayanApplication)getActivity().getApplication()).getNetworkStatus().observe(getActivity(), networkConnection -> {
            devicesRecyclerViewAdapter.updateItems(devices);
        });
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

            Log.e("///////////" , "onMove : " + viewHolder);
            Log.e("///////////" , "onMove : " + target);
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Log.e("///////////" , "onMove:new List:: " + devices);

            Log.e("///////////" , "onMove From: " + fromPosition);
            Log.e("///////////" , "onMove to: " + toPosition);


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
                    devicesFragmentViewModel.updateDevice(d);
                    devicesFragmentViewModel.updateDevice(d1);
                }
            } else {
                for (int i = from; i > to; i--) {
                    Device d = devices.get(i);
                    d.setPosition(i-1);
                    Device d2 = devices.get(i-1);
                    d2.setPosition(i);
                    Collections.swap(devices, i, i - 1);
                    devicesFragmentViewModel.updateDevice(d);
                    devicesFragmentViewModel.updateDevice(d2);
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

}
