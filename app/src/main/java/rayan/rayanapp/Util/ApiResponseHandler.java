package rayan.rayanapp.Util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Data.UserMembership;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Persistance.database.RemoteDatabase;
import rayan.rayanapp.Persistance.database.RemoteHubDatabase;
import rayan.rayanapp.Persistance.database.UserDatabase;
import rayan.rayanapp.Persistance.database.UserMembershipDatabase;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;

public class ApiResponseHandler {
    public void syncGroups(DeviceDatabase deviceDatabase, GroupDatabase groupDatabase, UserDatabase userDatabase,
                           UserMembershipDatabase membershipDatabase, RemoteHubDatabase remoteHubDatabase,
                           RemoteDatabase remoteDatabase, RayanApplication rayanApplication, List<Group> groups,
                           List<RemoteHub> remoteHubs, List<Remote> remotes){

        int nOd = 0;
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
                        deviceUser.setDeviceType(u.getType());
                        deviceUser.setBaseId(u.getId());
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
                        existing.setSecret(g.getSecret());
                        existing.setName1(u.getName1());
                        existing.setType(u.getType());
                        existing.setDeviceType(u.getType());
                        existing.setBaseId(u.getId());
                        existing.setTopic(u.getTopic());
                        tempTopics.add(u.getTopic().getTopic());
                        existing.setGroupId(g.getId());
                        devices.add(existing);
                        nOd++;
                    }
                }
            g.setDevices(devices);
            for (int z = 0;z<remoteHubs.size();z++) {
                if (remoteHubs.get(z).getGroupId().equals(g.getId())) {
                    remoteHubs.get(z).setPosition(nOd);
                    remoteHubs.get(z).setInGroupPosition(devices.size() + z);
                    nOd++;
                    for (int i = 0; i < remotes.size(); i++) {
                        if (remotes.get(i).getRemoteHubId().equals(remoteHubs.get(z).getId())) {
                            remotes.get(i).setPosition(nOd);
                            remotes.get(i).setType(nOd % 2 == 0 ? "AC" : "TV");
                            remotes.get(i).setInGroupPosition(devices.size() + z + i);
                            remotes.get(i).setGroupId(g.getId());
                            nOd++;
                        }
                    }
                }
            }
            for (int c = 0;c<g.getHumanUsers().size();c++){
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
            newDevices.addAll(devices);
        }
        List<Device> oldDevices = deviceDatabase.getAllDevices();
        List<Group> oldGroups = groupDatabase.getAllGroups();
        List<RemoteHub> oldRemoteHubs = remoteHubDatabase.getAllRemoteHubs();
        List<Remote> oldRemotes = remoteDatabase.getAllRemotes();
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
        if (remoteHubs != null && oldRemoteHubs != null){
            for (int a = 0;a<oldRemoteHubs.size();a++){
                String cId = oldRemoteHubs.get(a).getId();
                boolean exist = false;
                for (int b = 0;b<remoteHubs.size();b++){
                    if (cId.equals(remoteHubs.get(b).getId())) {
                        exist = true;
                        remoteHubs.get(b).setFavorite(oldRemoteHubs.get(a).isFavorite());
                        remoteHubs.get(b).setPosition(oldRemoteHubs.get(a).getPosition());
                        remoteHubs.get(b).setInGroupPosition(oldRemoteHubs.get(a).getInGroupPosition());
                        remoteHubs.get(b).setFavoritePosition(oldRemoteHubs.get(a).getFavoritePosition());
                    }
                }
                if (!exist) remoteHubDatabase.deleteRemoteHub(oldRemoteHubs.get(a));
            }
        }
        if (remoteHubs != null)
            for (RemoteHub remoteHub: remoteHubs){
                remoteHub.setChipId(remoteHub.getId());
                remoteHub.setDeviceType(AppConstants.BaseDeviceType_REMOTE_HUB);
                remoteHub.setBaseId(remoteHub.getId());
                tempTopics.add(remoteHub.getTopic());
            }
        if (remotes != null && oldRemotes != null){
            for (int a = 0;a<oldRemotes.size();a++){
                String cId = oldRemotes.get(a).getId();
                boolean exist = false;
                for (int b = 0;b<remotes.size();b++){
                    if (cId.equals(remotes.get(b).getId())){
                        exist = true;
                        remotes.get(b).setFavorite(oldRemotes.get(a).isFavorite());
                        remotes.get(b).setPosition(oldRemotes.get(a).getPosition());
                        remotes.get(b).setInGroupPosition(oldRemotes.get(a).getInGroupPosition());
                        remotes.get(b).setFavoritePosition(oldRemotes.get(a).getFavoritePosition());
                    }
                }
                if (!exist) remoteDatabase.deleteRemote(oldRemotes.get(a));
            }
        }
        if (remotes != null)
            for (Remote remote : remotes){
                tempTopics.add(remote.getTopic());
                remote.setBaseId(remote.getId());
                remote.setDeviceType(AppConstants.BaseDeviceType_REMOTE);
            }
        rayanApplication.getMsc().setNewArrivedTopics(tempTopics);
        groupDatabase.addGroups(serverGroups);
        userDatabase.addUsers(newUsers);
        remoteHubDatabase.addRemoteHubs(remoteHubs);
        remoteDatabase.addRemotes(remotes);
        membershipDatabase.addUserMemberships(memberships);
    }
}
/*

            for (int c = 0;c<remoteHubs.size();c++){
                RemoteHub remoteHub = remoteHubs.get(c);
                if (remoteHub.getGroupId().equals(g.getId())){
                Device existingDevice = deviceDatabase.getDevice(remoteHub.getChipId());
                    if (existingDevice == null){
                    Device device = new Device(remoteHub.getId(),remoteHub.getName(), remoteHub.getId(), AppConstants.DEVICE_TYPE_RemoteHub, "",new Topic(),remoteHub.getGroupId(), g.getSecret());
                        device.setSsid(remoteHub.getSsid() != null? remoteHub.getSsid():AppConstants.UNKNOWN_SSID);
                        device.setIp(AppConstants.UNKNOWN_IP);
                        device.setPosition(nOd);
//                        device.setFavoritePosition(nOd);
                        device.setInGroupPosition(c + nOd);
                        tempTopics.add(device.getTopic().getTopic());
                        if (device.getType()!= null && device.getName1() != null){
                            devices.add(device);
                            nOd++;
                        }
                    }else{
                        existingDevice.setSsid(remoteHub.getSsid() != null? remoteHub.getSsid():AppConstants.UNKNOWN_SSID);
                        existingDevice.setSecret(g.getSecret());
                        existingDevice.setName1(remoteHub.getName());
                        existingDevice.setType(AppConstants.DEVICE_TYPE_RemoteHub);
                        existingDevice.setTopic(new Topic(remoteHub.getTopic()));
                        tempTopics.add(remoteHub.getTopic());
                        existingDevice.setGroupId(g.getId());
                        devices.add(existingDevice);
                        nOd++;
                    }

                }
            }
 */