package rayan.rayanapp.ViewModels;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.DeviceMinimalSSIDIP;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Data.UserMembership;
import rayan.rayanapp.Fragments.DevicesFragment;
import rayan.rayanapp.Helper.DialogPresenter;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Helper.SendMessageToDevice;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Persistance.database.RemoteDatabase;
import rayan.rayanapp.Persistance.database.RemoteHubDatabase;
import rayan.rayanapp.Persistance.database.UserDatabase;
import rayan.rayanapp.Persistance.database.UserMembershipDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.GroupsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.RemoteHubsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.api.StartupApiRequests;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;
import rayan.rayanapp.Util.diffUtil.GroupsDiffCallBack;

public class DevicesFragmentViewModel extends AndroidViewModel {
    protected DeviceDatabase deviceDatabase;
    private GroupDatabase groupDatabase;
    private ExecutorService executorService;
    private UserDatabase userDatabase;
    private UserMembershipDatabase membershipDatabase;
    protected RemoteHubDatabase remoteHubDatabase;
    protected RemoteDatabase remoteDatabase;
    private ApiService apiService;
    private StartupApiRequests startupApiRequests;
    public DevicesFragmentViewModel(@NonNull Application application) {
        super(application);
        deviceDatabase = new DeviceDatabase(application);
        groupDatabase = new GroupDatabase(application);
        userDatabase = new UserDatabase(application);
        remoteHubDatabase = new RemoteHubDatabase(application);
        remoteDatabase = new RemoteDatabase(application);
        membershipDatabase = new UserMembershipDatabase(application);
        executorService= Executors.newSingleThreadExecutor();
        apiService = ApiUtils.getApiService();
        if (startupApiRequests == null)
            startupApiRequests = new StartupApiRequests(apiService, deviceDatabase, groupDatabase,groupDatabase, userDatabase, membershipDatabase,
                    new RemoteHubDatabase(application), new RemoteDatabase(application),(RayanApplication) getApplication());
//        initSounds();
    }

    public void addDevices(List<Device> devices){
        executorService.execute( () -> deviceDatabase.addDevices(devices));
    }

    public Device getDevice(String id){
        return deviceDatabase.getDevice(id);
    }

    public LiveData<List<Device>> getAllDevicesLive(){
        try {
            return new GetAllDevices().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public LiveData<List<RemoteHub>> getAllRemoteHubsLive(){
        return remoteHubDatabase.getAllRemoteHubsLive();
    }
    public LiveData<List<Remote>> getAllRemotesLive(){
        return remoteDatabase.getAllRemotesLive();
    }
    public Flowable<List<RemoteHub>> getAllRemoteHubsFlowable(){
        return remoteHubDatabase.getAllRemoteHubsFlowable();
    }

    public List<Device> getAllDevicesInGroup(String groupId){
            return deviceDatabase.getAllInGroup(groupId);
    }
    public Single<List<Device>> getAllDevicesInGroupSingle(String groupId){
            return deviceDatabase.getAllInGroupSingle(groupId);
    }
    public LiveData<List<Device>> getAllDevicesInGroupLive(String groupId){
            return deviceDatabase.getAllInGroupLive(groupId);
    }

    public void updateDevice(Device device){
        deviceDatabase.updateDevice(device);
    }
    public void updateDevices(List<BaseDevice> baseDevices){
        List<Device> devices = new ArrayList<>();
        List<RemoteHub> remoteHubs = new ArrayList<>();
        List<Remote> remotes = new ArrayList<>();
        for (BaseDevice baseDevice:baseDevices) {
            if (baseDevice.getDeviceType().equals(AppConstants.BaseDeviceType_SWITCH_1) || baseDevice.getDeviceType().equals(AppConstants.BaseDeviceType_SWITCH_2)||
                    baseDevice.getDeviceType().equals(AppConstants.BaseDeviceType_TOUCH_2)||baseDevice.getDeviceType().equals(AppConstants.BaseDeviceType_PLUG))
                devices.add((Device) baseDevice);
            else if (baseDevice.getDeviceType().equals(AppConstants.BaseDeviceType_REMOTE_HUB))
                remoteHubs.add((RemoteHub) baseDevice);
            else if (baseDevice.getDeviceType().equals(AppConstants.BaseDeviceType_REMOTE))
                remotes.add((Remote) baseDevice);
        }
        deviceDatabase.updateDevices(devices);
        remoteHubDatabase.updateRemoteHubs(remoteHubs);
        remoteDatabase.updateRemotes(remotes);
    }

    private class GetAllDevices extends AsyncTask<Void, Void,LiveData<List<Device>>> {
        @Override
        protected LiveData<List<Device>> doInBackground(Void... voids) {
            return deviceDatabase.getAllDevicesLive();
        }
    }

    private final String TAG = DevicesFragmentViewModel.class.getSimpleName();

    public LiveData<StartupApiRequests.requestStatus> getGroups() {
        return startupApiRequests.getGroups();
    }
    public LiveData<StartupApiRequests.requestStatus> getGroups1() {
        return startupApiRequests.getGroups1();
//        getGroupObservable().flatMap(new Function<GroupsResponse, Observable<RemoteHubsResponse>>() {
//            @Override
//            public Observable<RemoteHubsResponse> apply(GroupsResponse groupsResponse) throws Exception {
//                Log.e("<<<<<<<<<","<<<<<<<<<<<<<<<<<<<<<<STARTSTART");
//                if(groupsResponse.getData().getGroups() != null){
//                    syncGroups(groupsResponse.getData().getGroups());
//                }
//                Log.e("<<<<<<<<<","<<<<<<<<<<<<<<<<<<<<<< ENDENDEND");
//                ApiService apiService = ApiUtils.getApiService();
//                Log.e("@#@#","Accept:"+groupsResponse);
//                Map<String , String> params = new HashMap<>();
//                params.put("limit","20");
//                params.put("skip","0");
//                return apiService.getRemoteHubs(RayanApplication.getPref().getToken(), params);
//            }
//        }).subscribe(new Observer<RemoteHubsResponse>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.e(TAG, "STARTING MAN");
//            }
//
//            @Override
//            public void onNext(RemoteHubsResponse remoteHubsResponse) {
//                Log.e(TAG, "On Next " + remoteHubsResponse);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e(TAG, "Error darim dada" + e);
//            }
//
//            @Override
//            public void onComplete() {
//                Log.e(TAG, "Hale tamoom shod");
//            }
//        });
    }

    public void login(){
        loginObservable().subscribe(loginObserver());
    }

    private Observable<GroupsResponse> getGroupObservable(){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .getGroups(RayanApplication.getPref().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    private DisposableObserver<GroupsResponse> getGroupObserver(){
        return new DisposableObserver<GroupsResponse>() {

            @Override
            public void onNext(@NonNull GroupsResponse baseResponse) {
                Log.d(TAG,"OnNext "+baseResponse);

//		if(baseResponse.getData().getGroups() != null){
//                new SyncGroups(baseResponse.getData().getGroups()).execute();
//		}
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

    private int nOd = 0;
    public void syncGroups(List<Group> groups){
        List<Group> serverGroups = new ArrayList<>();
        List<String> tempTopics = new ArrayList<>();
            serverGroups.addAll(groups);
            List<Device> newDevices = new ArrayList<>();
            List<User> newUsers = new ArrayList<>();
            List<UserMembership> memberships = new ArrayList<>();
            for (int a = 0;a<serverGroups.size();a++){
                Group g = serverGroups.get(a);
                List<User> admins = g.getAdmins();
                List<Device> devices = new ArrayList<>();
//                if (g.getDevices() != null)
//                devices.addAll(g.getDevices());
                if (g.getHumanUsers() != null)
                    for (int b = 0;b<g.getDevices().size();b++){
                        Device u = g.getDevices().get(b);
                        Device existing = deviceDatabase.getDevice(u.getChipId());
                        if (existing == null){
                            Device deviceUser = new Device(u.getChipId(), u.getName1(), u.getId(), u.getType(), u.getUsername(), u.getTopic(), g.getId(), g.getSecret());
                            deviceUser.setSsid(u.getSsid() != null? u.getSsid():AppConstants.UNKNOWN_SSID);
                            deviceUser.setIp(AppConstants.UNKNOWN_IP);
                            deviceUser.setPosition(nOd);
//                        deviceUser.setFavoritePosition(nOd);
                            deviceUser.setInGroupPosition(b);
                            tempTopics.add(deviceUser.getTopic().getTopic());
                            if (deviceUser.getType()!= null && deviceUser.getName1() != null){
                                devices.add(deviceUser);
                                nOd++;
                            }
                            Log.e("isDeviceHasEmptyPar" , "really?? " + (deviceUser.getType()!= null) + (deviceUser.getName1() != null));
                        }
                        else {
                            existing.setSsid(u.getSsid() != null? u.getSsid():AppConstants.UNKNOWN_SSID);
                            Log.e("wwwwwwwwwwwwwwwwww", u.getName1()+"u.ssid"+u.getSsid()+existing.getSsid());

                            existing.setSecret(g.getSecret());
                            existing.setName1(u.getName1());
                            existing.setType(u.getType());
                            existing.setTopic(u.getTopic());
                            tempTopics.add(u.getTopic().getTopic());
                            existing.setGroupId(g.getId());
                            devices.add(existing);
                            nOd++;
                        }
                    } for (int c = 0;c<g.getHumanUsers().size();c++){
                    User user = g.getHumanUsers().get(c);
                    UserMembership userMembership = new UserMembership(g.getId(), user.getId());
                    User existingUser = userDatabase.getUser(user.getId());
                    boolean admin = false;
                    if (existingUser != null){
                        for (User u: admins) {
                            if (existingUser.getId().equals(u.getId()))
                                admin = true;
                        }
                        if (admin){
                            userMembership.setUserType(AppConstants.ADMIN_TYPE);
                        }else{
                            userMembership.setUserType(AppConstants.USER_TYPE);
                        }
                        existingUser.setUserInfo(user.getUserInfo());
                        newUsers.add(existingUser);
                    }else{
                        for (User u: admins) {
                            if (user.getId().equals(u.getId()))
                                admin = true;
                        }
                        if (admin)
                            userMembership.setUserType(AppConstants.ADMIN_TYPE);
                        else userMembership.setUserType(AppConstants.USER_TYPE);
                        newUsers.add(user);
                    }
                    memberships.add(userMembership);
                }
                g.setDevices(devices);
                newDevices.addAll(devices);
            }
            ((RayanApplication)getApplication()).getMsc().setNewArrivedTopics(tempTopics);
            List<Device> oldDevices = deviceDatabase.getAllDevices();
            List<Group> oldGroups = groupDatabase.getAllGroups();
            List<User> oldUsers = userDatabase.getAllUsers();
            List<UserMembership> oldMemberships = membershipDatabase.getAllUserMemberships();
//            deviceDatabase.addDevices(newDevices);
//            ((RayanApplication)getApplication()).getMtd().setDevices(newDevices);
            if (oldDevices != null){
                for (int a = 0;a<oldDevices.size();a++){
                    String cId = oldDevices.get(a).getChipId();
                    boolean exist = false;
                    for (int b = 0;b<newDevices.size();b++){
                        if (cId.equals(newDevices.get(b).getChipId())) exist = true;
                    }
                    if (!exist) deviceDatabase.deleteDevice(oldDevices.get(a));
                }
            }
            for (int a = 0; a<newDevices.size();a++){
                Device newDevice = newDevices.get(a);
                boolean exists = false;
                for (int b = 0; b<oldDevices.size(); b++){
                    if (newDevice.getChipId().equals(oldDevices.get(b).getChipId()))
                        exists = true;
                }
                if (!exists)
                    deviceDatabase.addDevice(newDevice);
                else deviceDatabase.updateDevice(newDevice);
            }
            if (oldUsers != null){
                for (int a = 0;a<oldUsers.size();a++){
                    String uId = oldUsers.get(a).getId();
                    boolean exist = false;
                    for (int b = 0;b<newUsers.size();b++){
                        if (uId.equals(newUsers.get(b).getId())) exist = true;
                    }
                    if (!exist) userDatabase.deleteUser(oldUsers.get(a));
                }
            }
            if (oldMemberships != null){
                for (int a = 0;a<oldMemberships.size();a++){
                    String mid = oldMemberships.get(a).getMembershipId();
                    boolean exist = false;
                    for (int b = 0;b<memberships.size();b++){
                        if (mid.equals(memberships.get(b).getMembershipId())) exist = true;
                    }
                    if (!exist) membershipDatabase.deleteUserMembership(oldMemberships.get(a));
                }
            }
            if (serverGroups != null && oldGroups != null){
                for (int a = 0;a<oldGroups.size();a++){
                    String cId = oldGroups.get(a).getId();
                    boolean exist = false;
                    for (int b = 0;b<serverGroups.size();b++){
                        if (cId.equals(serverGroups.get(b).getId())) exist = true;
                    }
                    if (!exist) groupDatabase.deleteGroup(oldGroups.get(a));
                }
            }
            groupDatabase.addGroups(serverGroups);
            Log.e("?>?>?>?>?>", "Adding users: " + newUsers.size() + newUsers);
            Log.e("?>?>?>?>?>", "Adding memberships: " + memberships.size() + memberships);
            userDatabase.addUsers(newUsers);
            membershipDatabase.addUserMemberships(memberships);
            nOd = 0;
//            try {
//            if (MainActivityViewModel.connection.getValue() != null && MainActivityViewModel.connection.getValue().getClient()!= null && MainActivityViewModel.connection.getValue().getClient().isConnected()) {
//                Log.e(TAG, "After getting Groups mqtt connection is: " + MainActivityViewModel.connection.getValue().getClient().isConnected());
//                subscribeToAll(newDevices);
//            }
//            }catch (Exception e){
//                Log.e(TAG, AppConstants.ERROR_OCCURRED + e);
//            }

    }

    public void subscribeToTopic(String topic){
        try {
            if (!DevicesFragment.subscribedDevices.contains(topic)){
                Log.e(TAG, "Not Subscribed Yet to: " + topic);
                MainActivityViewModel.connection.getValue().getClient().subscribe(topic, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
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


    public Single<List<Device>> getAllDevicesSingle(){
        return deviceDatabase.getAllDevicesSingle();
    }

    MediaPlayer switchOnSound, switchOffSound;
    Uri switchOnPath, switchOffPath;

    public void playOnSound(){
        switchOnSound.start();
    }

    public void playOffSound(){
        switchOffSound.start();
    }
    public void initSounds(){
        switchOnSound = new MediaPlayer();
        switchOnPath = Uri.parse("android.resource://"+getApplication().getPackageName()+"/raw/sound_switch_on_");

        switchOffSound = new MediaPlayer();
        switchOffPath = Uri.parse("android.resource://"+getApplication().getPackageName()+"/raw/sound_switch_off_");
        try {
            switchOnSound.setDataSource(getApplication(), switchOnPath);
            switchOnSound.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            switchOnSound.prepareAsync();

            switchOffSound.setDataSource(getApplication(), switchOffPath);
            switchOffSound.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            switchOffSound.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

