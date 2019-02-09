package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.util.Log;

import com.google.gson.JsonObject;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Responses.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.Group;
import rayan.rayanapp.Retrofit.Models.Responses.GroupsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.User;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.udp.SendUDPMessage;
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
                new SyncGroups(baseResponse.getData().getGroups()).execute();
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
                        Device deviceUser = new Device(u.getChipId(), u.getName1(), u.getId(), u.getType(), u.getUsername(), u.getTopic(), g.getId());
                        devices.add(deviceUser);
                    }
                    else devices.add(existing);

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
                    groupDatabase.updateGroups(serverGroups.subList(i, i+1));
                }
            });
//            groupDatabase.addGroups(newGroups);
//            myViewModel.addUsers(newUsers);
            return null;
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

    public void togglePin1(Device device, boolean local){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd",device.getPin1().equals("on")? "turn_off_1" : "turn_on_1");
        jsonObject.addProperty("src",RayanApplication.getPref().getId());
        if (local)
            sendUDPMessage.sendUdpMessage(device.getIp(),jsonObject.toString());
        else if (MainActivityViewModel.connection != null && MainActivityViewModel.connection.getValue().isConnected())
            publish(MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), jsonObject.toString(), 0, false);
    }

    public void togglePin2(Device device, boolean local){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd",device.getPin2().equals("on")? "turn_off_2" : "turn_on_2");
        jsonObject.addProperty("src",RayanApplication.getPref().getId());
        if (local)
            sendUDPMessage.sendUdpMessage(device.getIp(),jsonObject.toString());
        else if (MainActivityViewModel.connection != null && MainActivityViewModel.connection.getValue().isConnected())
            publish(MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), jsonObject.toString(), 0, false);
    }


}

