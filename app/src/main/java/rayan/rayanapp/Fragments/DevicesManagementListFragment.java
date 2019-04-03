package rayan.rayanapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.DeviceManagementActivity;
import rayan.rayanapp.Adapters.recyclerView.DevicesManagementRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnDeviceClickListenerManagement;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.DevicesManagementListFragmentViewModel;

public class DevicesManagementListFragment extends BackHandledFragment implements OnDeviceClickListenerManagement<Device> {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.coordinateLayout)
    CoordinatorLayout coordinatorLayout;
    DevicesManagementRecyclerViewAdapter devicesRecyclerViewAdapterManagement;
    List<Device> devices = new ArrayList<>();
    DevicesManagementListFragmentViewModel devicesManagementListFragmentViewModel;
    ClickOnDevice sendDevice;
    List<String> waiting = new ArrayList<>();
    Device device;

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
        devicesManagementListFragmentViewModel.getAllDevices().observe(this, devices -> devicesRecyclerViewAdapterManagement.updateItems(devices));
        fragmentManager = getActivity().getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices_management_list, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(devicesRecyclerViewAdapterManagement);
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
    @Override
    public void onItemClick(Device item) {

//        if (item.getIp() != null) {
            waiting.add(item.getChipId());
            devicesRecyclerViewAdapterManagement.setItems(devicesManagementListFragmentViewModel.getDevices());
            devicesManagementListFragmentViewModel.setReadyForSettingsHttp(item).observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    Log.e("...........", "............" + s);
                    if (s.equals("YES")) {
                        waiting.remove(item.getChipId());
                        transaction = fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left, R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
                        EditDeviceFragment editGroupFragment = EditDeviceFragment.newInstance(item);
                        ((DeviceManagementActivity) Objects.requireNonNull(getActivity())).setActionBarTitle(item.getName1());
                        transaction.replace(R.id.frameLayout, editGroupFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }else if (s.equals(AppConstants.SOCKET_TIME_OUT)){
                        Log.e("ttttttttttt", "tttttttttttttt" +s);
                        waiting.remove(item.getChipId());
                        devicesRecyclerViewAdapterManagement.setItems(devicesManagementListFragmentViewModel.getDevices());
                    }
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
    }

    @Override
    public void onFavoriteIconClicked(Device item) {
        Device d = devicesManagementListFragmentViewModel.getDevice(item.getChipId());
        d.setFavorite(!item.isFavorite());
        devicesManagementListFragmentViewModel.updateDevice(d);
        if (!item.isFavorite())
            Snackbar.make(coordinatorLayout," " +item.getName1()+" به موردعلاقه ها اضافه شد", Snackbar.LENGTH_LONG)
//                    .setAction("بازگشت",v -> {
//                        d.setFavorite(item.isFavorite());
//                        devicesManagementListFragmentViewModel.updateDevice(d);
//                    })
                    .show();
        else Snackbar.make(coordinatorLayout," " +item.getName1()+" از موردعلاقه ها حذف شد", Snackbar.LENGTH_LONG)
//                .setAction("بازگشت",v -> {
//                    d.setFavorite(item.isFavorite());
//                    devicesManagementListFragmentViewModel.updateDevice(d);
//                })
                .show();
    }

    public interface ClickOnDevice {
        void clickOnDevice(Device device);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sendDevice = (ClickOnDevice) getActivity();
    }

}
