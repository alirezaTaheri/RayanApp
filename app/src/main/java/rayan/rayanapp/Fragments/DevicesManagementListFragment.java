package rayan.rayanapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import rayan.rayanapp.Activities.DeviceManagementActivity;
import rayan.rayanapp.Adapters.recyclerView.DevicesManagementRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.ConnectionStatusModel;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Dialogs.DeviceManagementPopupDialog;
import rayan.rayanapp.Listeners.DeviceManagementOptionsClickListener;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;
import rayan.rayanapp.Receivers.ConnectionLiveData;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.NetworkUtil;
import rayan.rayanapp.ViewModels.DevicesManagementListFragmentViewModel;

public class DevicesManagementListFragment extends BackHandledFragment implements OnDeviceClickListenerManagement<Device>,
        DeviceManagementOptionsClickListener<Device> {
    private final String TAG = "DevicesManagementListFragment";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.coordinateLayout)
    CoordinatorLayout coordinatorLayout;
    DevicesManagementRecyclerViewAdapter devicesRecyclerViewAdapterManagement;
    List<Device> devices = new ArrayList<>();
    List<Device> favoriteDevices = new ArrayList<>();
    DevicesManagementListFragmentViewModel devicesManagementListFragmentViewModel;
    ClickOnDevice sendDevice;
    List<String> waiting = new ArrayList<>();
    Device device;
    ConnectionStatusModel connectionStatus;

    public DevicesManagementListFragment() {
    }

    public static DevicesManagementListFragment newInstance() {
        return new DevicesManagementListFragment();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    Disposable disposable;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicesRecyclerViewAdapterManagement = new DevicesManagementRecyclerViewAdapter(getActivity(), devices, waiting);
        devicesRecyclerViewAdapterManagement.setListener(this);
        devicesManagementListFragmentViewModel = ViewModelProviders.of(this).get(DevicesManagementListFragmentViewModel.class);
        devicesManagementListFragmentViewModel.getAllDevicesLive().observe(this, devices -> {
            Log.e("devdevdevdev: ", ": " + devices);
//            for (Device d: devices)
//                if (d.getChipId().equals("222222"))
//                    d.setIp("192.168.137.1");
            devicesRecyclerViewAdapterManagement.updateItems(devices);
            for (Device d: devices)
                if (d.isFavorite())
                    favoriteDevices.add(d);
        });
        fragmentManager = getActivity().getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices_management_list, container, false);
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(getContext(),((RayanApplication) (activity.getApplication())).getNetworkBus());
        connectionLiveData.observe(this, getConnectionStatusObserver());
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(devicesRecyclerViewAdapterManagement);
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    public Observer<ConnectionStatusModel> getConnectionStatusObserver() {
        return new Observer<ConnectionStatusModel>() {
            @Override
            public void onChanged(@Nullable ConnectionStatusModel connectionStatusModel) {
                Log.d(TAG, "onChanged() called with: connectionStatusModel = [" + connectionStatusModel + "]");
                connectionStatus = new ConnectionStatusModel();
                if (connectionStatusModel == null){
                    connectionStatus.setTypeName(AppConstants.NOT_CONNECTED);
                }
                else if (connectionStatusModel.getType() == AppConstants.WIFI_NETWORK) {
                    String extraInfo = connectionStatusModel.getSsid();
                    if (extraInfo != null && connectionStatusModel.getSsid().charAt(connectionStatusModel.getSsid().length()-1) == connectionStatusModel.getSsid().charAt(0) && String.valueOf(connectionStatusModel.getSsid().charAt(0)).equals("\"")) {
                        extraInfo = connectionStatusModel.getSsid().substring(1, extraInfo.length() - 1);
                        connectionStatus.setTypeName(AppConstants.WIFI);
                        connectionStatus.setSsid(extraInfo);
                    }else if (extraInfo != null && !String.valueOf(connectionStatusModel.getSsid().charAt(connectionStatusModel.getSsid().length()-1)).equals("\"") && !String.valueOf(connectionStatusModel.getSsid().charAt(0)).equals("\"")){
                        connectionStatus.setTypeName(AppConstants.WIFI);
                        connectionStatus.setSsid(extraInfo);
                    }
                } else if (connectionStatusModel.getType() == AppConstants.MOBILE_DATA) {
                    connectionStatus.setTypeName(AppConstants.MOBILE);
                }
            }
        };
    }

    @Override
    public void onItemClick(Device item) {
        Log.e(TAG, connectionStatus.toString() + item);
        if (connectionStatus == null){
            ConnectionLiveData connectionLiveData = new ConnectionLiveData(getContext(),((RayanApplication) (activity.getApplication())).getNetworkBus());
            connectionLiveData.observe(this, getConnectionStatusObserver());
        }else {
            if (connectionStatus.getTypeName().equals(AppConstants.WIFI) && item.getSsid().equals(connectionStatus.getSsid())) {
//        if (item.getIp() != null) {
                waiting.add(item.getChipId());
                devicesRecyclerViewAdapterManagement.setItems(devicesManagementListFragmentViewModel.getDevices());
                devicesManagementListFragmentViewModel.setReadyForSettingsHttp(item).observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Log.e("...........", "............" + s);
                        if (s.equals(AppConstants.SETTINGS)) {
                            transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.animation_transition_enter_from_right, R.anim.animation_transition_ext_to_left, R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_from_right);
                            EditDeviceFragment editGroupFragment = EditDeviceFragment.newInstance(item);
                            ((DeviceManagementActivity) getActivity()).editDeviceFragment = editGroupFragment;
                            ((DeviceManagementActivity) Objects.requireNonNull(getActivity())).setActionBarTitle(item.getName1());
                            transaction.replace(R.id.frameLayout, editGroupFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else if (s.equals(AppConstants.SOCKET_TIME_OUT)) {
                            Log.e("ttttttttttt", "tttttttttttttt" + s);
                            Toast.makeText(getActivity(), "اتصال به دستگاه ناموفق بود", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG,"What is the unknown problem? "+s);
                            Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), ""+s, Toast.LENGTH_SHORT).show();
                        }
                        waiting.remove(item.getChipId());
                        devicesRecyclerViewAdapterManagement.setItems(devicesManagementListFragmentViewModel.getDevices());
                    }
                });
//        }else SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"دستگاه در دسترس نیست");


//        if (disposable != null)
//            disposable.dispose();
//        disposable = ((RayanApplication)getActivity().getApplication()).getBus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
//            if (o.getString("cmd").equals(AppConstants.SETTINGS)){
//                SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"Message: " + o);
//                waiting.remove(device.getChipId());
//                transaction = fragmentManager.beginTransaction();
//                transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
//                EditDeviceFragment editGroupFragment = EditDeviceFragment.newInstance(device);
//                ((DeviceManagementActivity)Objects.requireNonNull(getActivity())).setActionBarTitle(item.getName1());
//                transaction.replace(R.id.frameLayout, editGroupFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//                disposable.dispose();
//            }
//        });
//        this.device = item;
//        if (device.getIp() == null)
//            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"دستگاه در دسترس نیست");
//        else {
//            waiting.add(item.getChipId());
//            devicesRecyclerViewAdapterManagement.setItems(devicesManagementListFragmentViewModel.getDevices());
//            devicesManagementListFragmentViewModel.setReadyForSettings(device).observe(this, s -> {
//                if (waiting.contains(s)){
//                    waiting.remove(s);
//                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"دستگاه در دسترس نیست");
//                }
//                devicesRecyclerViewAdapterManagement.setItems(devicesManagementListFragmentViewModel.getDevices());
//            });
//        }
            } else {
                Toast.makeText(getContext(), "لطفا به شبکه " + "\"" + item.getSsid() + "\"" + " متصل شوید", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFavoriteIconClicked(Device item) {
        Device deviceToUpdate = new Device(item);
        deviceToUpdate.setFavorite(!item.isFavorite());
        if (deviceToUpdate.isFavorite())
            deviceToUpdate.setFavoritePosition(favoriteDevices.size());
        else if (!deviceToUpdate.isFavorite())
            deviceToUpdate.setFavoritePosition(-1);
        devicesManagementListFragmentViewModel.updateDevice(deviceToUpdate);
        if (deviceToUpdate.isFavorite()){
            Toast.makeText(getActivity(),item.getName1().concat(" به موردعلاقه ها اضافه شد"), Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(getActivity(),item.getName1().concat(" از موردعلاقه ها حذف شد"), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onVisibilityIconClicked(Device item) {
        Device deviceToUpdate = new Device(item);
        deviceToUpdate.setHidden(!item.isHidden());
        devicesManagementListFragmentViewModel.updateDevice(deviceToUpdate);
        if (deviceToUpdate.isHidden())
            Toast.makeText(getActivity(),item.getName1().concat(" مخفی شد"), Toast.LENGTH_SHORT).show();
        else Toast.makeText(getActivity(),item.getName1().concat(" قابل رویت شد"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteClicked(Device item) {
//        Device d = devicesManagementListFragmentViewModel.getDevice(item.getChipId());

    }

    @Override
    public void onVisibilityClicked(Device item) {
//        Device d = devicesManagementListFragmentViewModel.getDevice(item.getChipId());
//        d.setHidden(!d.isHidden());

    }

    public interface ClickOnDevice {
        void clickOnDevice(Device device);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sendDevice = (ClickOnDevice) getActivity();
        activity = (DeviceManagementActivity) context;
    }

    DeviceManagementActivity activity;
}
