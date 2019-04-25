package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.text.TextUtils;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.DialogPresenter;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Helper.SendMessageToDevice;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.GroupsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;
import rayan.rayanapp.Util.diffUtil.GroupsDiffCallBack;

public class DevicesFragmentViewModel extends AndroidViewModel {
    protected DeviceDatabase deviceDatabase;
    private GroupDatabase groupDatabase;
    private ExecutorService executorService;
    private SendUDPMessage sendUDPMessage;
    public DevicesFragmentViewModel(@NonNull Application application) {
        super(application);
        deviceDatabase = new DeviceDatabase(application);
        groupDatabase = new GroupDatabase(application);
        sendUDPMessage = new SendUDPMessage();
        executorService= Executors.newSingleThreadExecutor();
    }

    public void addDevices(List<Device> devices){
        executorService.execute( () -> deviceDatabase.addDevices(devices));
    }

    public Device getDevice(String id){
        return deviceDatabase.getDevice(id);
    }

    public LiveData<List<Device>> getAllDevices(){
        try {
            return new GetAllDevices().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Device> getAllDevicesInGroup(String groupId){
            return deviceDatabase.getAllInGroup(groupId);
    }
    public LiveData<List<Device>> getAllDevicesInGroupLive(String groupId){
            return deviceDatabase.getAllInGroupLive(groupId);
    }

    public void updateDevice(Device device){
        deviceDatabase.updateDevice(device);
    }

    private class GetAllDevices extends AsyncTask<Void, Void,LiveData<List<Device>>> {
        @Override
        protected LiveData<List<Device>> doInBackground(Void... voids) {
            return deviceDatabase.getAllDevicesLive();
        }
    }

    private final String TAG = DevicesFragmentViewModel.class.getSimpleName();

    public void getGroups() {
        getGroupObservable().subscribe(getGroupObserver());
    }

    public void login(){
        loginObservable().subscribe(loginObserver());
    }

    private Observable<GroupsResponse> getGroupObservable(){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .getGroups(RayanApplication.getPref().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<GroupsResponse> getGroupObserver(){
        return new DisposableObserver<GroupsResponse>() {

            @Override
            public void onNext(@NonNull GroupsResponse baseResponse) {
                Log.d(TAG,"OnNext "+baseResponse);

		if(baseResponse.getData().getGroups() != null){
                new SyncGroups(baseResponse.getData().getGroups()).execute();
		}
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e.toString().contains("Unauthorized"))
                    login();

            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    private Observable<BaseResponse> loginObservable(){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .login(RayanApplication.getPref().getUsername(), RayanApplication.getPref().getPassword())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> loginObserver(){
        return new DisposableObserver<BaseResponse>() {
            @Override
            public void onNext(@NonNull BaseResponse baseResponse) {
                Log.d(TAG,"OnNext "+baseResponse);
                RayanApplication.getPref().saveToken(baseResponse.getData().getToken());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
                getGroups();
            }
        };
    }

    int nOd = 0;
    private class SyncGroups extends AsyncTask<Void, Void, Void>{
        private List<Group> serverGroups = new ArrayList<>();
        public SyncGroups(List<Group> groups){
            this.serverGroups.addAll(groups);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            List<Device> newDevices = new ArrayList<>();
            List<User> newUsers = new ArrayList<>();
            for (int a = 0;a<serverGroups.size();a++){
                Group g = serverGroups.get(a);
                List<Device> devices = new ArrayList<>();
                List<User> users = new ArrayList<>();
//                if (g.getDevices() != null)
//                devices.addAll(g.getDevices());
                if (g.getHumanUsers() != null)
                users.addAll(g.getHumanUsers());
                for (int b = 0;b<g.getDevices().size();b++){
                    Device u = g.getDevices().get(b);
                    Device existing = deviceDatabase.getDevice(u.getChipId());
                    if (existing == null){
                        Device deviceUser = new Device(u.getChipId(), u.getName1(), u.getId(), u.getType(), u.getUsername(), u.getTopic(), g.getId(), g.getSecret());
                        deviceUser.setSsid(u.getSsid() != null? u.getSsid():AppConstants.UNKNOWN_SSID);
                        deviceUser.setPosition(nOd);
                        if (deviceUser.getType()!= null && deviceUser.getName1() != null)
                            devices.add(deviceUser);
                    }
                    else {
                        existing.setSsid(u.getSsid() != null? u.getSsid():AppConstants.UNKNOWN_SSID);
                        existing.setSecret(g.getSecret());
                        existing.setName1(u.getName1());
                        existing.setType(u.getType());
                        existing.setTopic(u.getTopic());
                        existing.setGroupId(g.getId());
                        devices.add(existing);
                    }
                    nOd++;
                }
                g.setDevices(devices);
                g.setHumanUsers(users);
                newDevices.addAll(devices);
                newUsers.addAll(users);
            }
            List<Device> oldDevices = deviceDatabase.getAllDevices();
            List<Group> oldGroups = groupDatabase.getAllGroups();
            DevicesDiffCallBack devicesDiffCallBack = new DevicesDiffCallBack(newDevices, oldDevices);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(devicesDiffCallBack);
            diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
                @Override
                public void onInserted(int i, int i1) {
//                    Log.e("ABC", "OnInserted: from: "+i+" To: " + i+i1 + " Count: " + i1);
                    addDevices(newDevices.subList(i, i+i1));
//                    ((RayanApplication)getApplication()).getMtd().addDevices(newDevices.subList(i, i+i1));
                }

                @Override
                public void onRemoved(int i, int i1) {
//                    Log.e("ABC", "OnRemoved: from: "+i+" To: " + (i1 +i) + " Count: " + i1);
                    deviceDatabase.deleteDevices(oldDevices.subList(i, i + i1));
                }

                @Override
                public void onMoved(int i, int i1) {
//                    Log.e("ABC", "onMoved: from: "+i+" To: " + i1);
                }

                @Override
                public void onChanged(int i, int i1, @Nullable Object o) {
//                    Log.e("ABC", "onChanged: from: "+i+" To: " + i1 +1 +"Count: " + i1);
                    deviceDatabase.updateDevices(newDevices.subList(i, i + i1));
                }
            });
            GroupsDiffCallBack groupsDiffCallBack = new GroupsDiffCallBack(serverGroups, oldGroups);
            DiffUtil.DiffResult diffResult1 = DiffUtil.calculateDiff(groupsDiffCallBack);
            diffResult1.dispatchUpdatesTo(new ListUpdateCallback() {
                @Override
                public void onInserted(int i, int i1) {
                    groupDatabase.addGroups(serverGroups.subList(i, i+i1));
//                    Log.e("DEF", "OnInserted: from: "+i+" To: " + i+i1 + " Count: " + i1);
                }

                @Override
                public void onRemoved(int i, int i1) {
//                    Log.e("DEF", "OnRemoved: from: "+i+" To: " + (i1 +i) + " Count: " + i1);
                    groupDatabase.deleteGroups(oldGroups.subList(i, i+1));
                }

                @Override
                public void onMoved(int i, int i1) {
//                    Log.e("DEF", "onMoved: from: "+i+" To: " + i1);

                }

                @Override
                public void onChanged(int i, int i1, @Nullable Object o) {
//                    Log.e("DEF", "onChanged: from: "+i+" To: " + (i1+i) +"Count: " + i1);
                    groupDatabase.updateGroups(serverGroups.subList(i, i+i1));
                }
            });
            nOd = 0;
            if (MainActivityViewModel.connection.getValue() != null && MainActivityViewModel.connection.getValue().getClient()!= null && MainActivityViewModel.connection.getValue().getClient().isConnected()) {
                Log.e(TAG, "After getting Groups mqtt connection is: " + MainActivityViewModel.connection.getValue().getClient().isConnected());
                subscribeToAll(newDevices);
            }
//            MainActivityViewModel.connection.getValue().getClient().subs
//            groupDatabase.addGroups(newGroups);
//            myViewModel.addUsers(newUsers);
            return null;
        }
    }

    private void subscribeToAll(List<Device> devices){
            try {
                for (int a = 0;a<devices.size();a++){
                    Log.e(TAG, "Subscribing to: " + devices.get(a));
                    MainActivityViewModel.connection.getValue().getClient().subscribe(devices.get(a).getTopic().getTopic(), 0);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in Subscribing: " + e);
                e.printStackTrace();
            }
    }

    private void publish(Connection connection, String topic, String message, int qos, boolean retain){
        try {
            String[] actionArgs = new String[2];
            actionArgs[0] = message;
            actionArgs[1] = topic;
//            final ActionListener callback = new ActionListener(context,
//                    ActionListener.Action.PUBLISH, connection, actionArgs);
            IMqttActionListener iMqttActionListener = new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(TAG, "onSuccess Publish message" + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "onFailure Publish message");
                }
            };
            connection.getClient().publish(topic, message.getBytes(), qos, retain, null, iMqttActionListener);
        } catch( MqttException ex){
            Log.e(TAG, "Exception occurred during publish: " + ex.getMessage());
        }
    }

    public void togglePin1(DialogPresenter dp, ToggleDeviceAnimationProgress fragment, int position, RayanApplication rayanApplication, Device device){
        rayanApplication.getSendMessageToDevice().toggleDevicePin1(dp, fragment, device, position, rayanApplication);
    }

    public void togglePin2(DialogPresenter dp,ToggleDeviceAnimationProgress fragment, int position, RayanApplication rayanApplication, Device device){
        rayanApplication.getSendMessageToDevice().toggleDevicePin2(dp, fragment, device, position, rayanApplication);
    }

    public Single<Boolean> internetProvided(){
        return Single.fromCallable(() -> {
            try {
                Socket sock = new Socket();
                SocketAddress address = new InetSocketAddress("8.8.8.8", 53);
                sock.connect(address, 1000);
                sock.close();
                return true;
            }catch (IOException e){
                return false;
            }
        });
    }


}

