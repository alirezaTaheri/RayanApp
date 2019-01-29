package rayan.rayanapp.MainActivity.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.util.Log;

import com.google.gson.Gson;
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
import rayan.rayanapp.Retrofit.Models.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Group;
import rayan.rayanapp.Retrofit.Models.ResponseUser;
import rayan.rayanapp.Retrofit.Models.User;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.DevicesDiffCallBack;

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

    private Observable<BaseResponse> getGroupObservable(){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .getGroups(RayanApplication.getPref().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> getGroupObserver(){
        return new DisposableObserver<BaseResponse>() {

            @Override
            public void onNext(@NonNull BaseResponse baseResponse) {
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
            this.serverGroups = groups;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            List<Group> newGroups = new ArrayList<>();
            List<Device> newDevices = new ArrayList<>();
            List<User> newUsers = new ArrayList<>();
            for (int a = 0;a<serverGroups.size();a++){
                Group g = serverGroups.get(a);
                List<Device> devices = new ArrayList<>();
                List<User> users = new ArrayList<>();
                for (int b = 0;b<g.getAllUsers().size();b++){
                    ResponseUser u = g.getAllUsers().get(b);
                    if (u.getType().equals("human")){
                        User humanUser = new User(u.getId(), u.getUsername(), u.getRegistered(), u.getUserInfo(), g.getId(), u.getRole());
                        users.add(humanUser);
                    }
                    else{
                        Device deviceUser = new Device(u.getChip_id(), u.getDevice_name(), u.getId(), u.getDevice_type(), u.getUsername(), u.getTopic(), g.getId());
                        deviceUser.setPin1("off");
                        deviceUser.setPin2("off");
                        deviceUser.setFavorite(b % 2 == 0);
                        devices.add(deviceUser);
                    }
                }
                g.setDevices(devices);
                g.setHumanUsers(users);
                newDevices.addAll(devices);
                newUsers.addAll(users);
                newGroups.add(g);
            }
            List<Device> database = deviceDatabase.getAllDevices();
            DevicesDiffCallBack devicesDiffCallBack = new DevicesDiffCallBack(newDevices, database);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(devicesDiffCallBack);
            diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
                @Override
                public void onInserted(int i, int i1) {
                    addDevices(newDevices.subList(i, i+i1));
                }

                @Override
                public void onRemoved(int i, int i1) {
                    deviceDatabase.deleteDevices(database.subList(i, i + i1));
                }

                @Override
                public void onMoved(int i, int i1) {
                }

                @Override
                public void onChanged(int i, int i1, @Nullable Object o) {
                    deviceDatabase.updateDevices(database.subList(i, i + i1));
                }
            });
            groupDatabase.addGroups(newGroups);
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

