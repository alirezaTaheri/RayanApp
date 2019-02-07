package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.AddAdminRequest;
import rayan.rayanapp.Retrofit.Models.Requests.AddUserByMobileRequest;
import rayan.rayanapp.Retrofit.Models.Responses.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Requests.DeleteUserRequest;
import rayan.rayanapp.Retrofit.Models.Requests.EditGroupRequest;
import rayan.rayanapp.Retrofit.Models.Responses.User;

public class EditGroupFragmentViewModel extends DevicesFragmentViewModel {
    GroupDatabase groupDatabase;
    private final String TAG = EditGroupFragmentViewModel.class.getSimpleName();
    public EditGroupFragmentViewModel(@NonNull Application application) {
        super(application);
        groupDatabase = new GroupDatabase(application);
    }

    public List<User> getUsers(String id){
        return groupDatabase.getGroup(id).getHumanUsers();
    }

    public LiveData<BaseResponse> addUserByMobile(String phone, String groupId){
        final MutableLiveData<BaseResponse> results = new MutableLiveData<>();
        List<String> phones = new ArrayList<>();
        phones.add(phone);
        addUserByMobileObservable(new AddUserByMobileRequest(phones, groupId)).subscribe(addUserByMobileObserver(results));
        return results;
    }

    private Observable<BaseResponse> addUserByMobileObservable(AddUserByMobileRequest addUserByMobileRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .addUserByMobile(RayanApplication.getPref().getToken(), addUserByMobileRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> addUserByMobileObserver(MutableLiveData<BaseResponse> results){
        return new DisposableObserver<BaseResponse>() {

            @Override
            public void onNext(@NonNull BaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
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

    public LiveData<BaseResponse> addAdmin(Set<String> id, String groupId){
        List<String> id_s = new ArrayList<>();
        id_s.addAll(id);
        final MutableLiveData<BaseResponse> results = new MutableLiveData<>();
        addAdminObservable(new AddAdminRequest(id_s, groupId)).subscribe(addAdminObserver(results));
        return results;
    }

    private Observable<BaseResponse> addAdminObservable(AddAdminRequest addAdminRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .addAdmin(RayanApplication.getPref().getToken(), addAdminRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> addAdminObserver(MutableLiveData<BaseResponse> results){
        return new DisposableObserver<BaseResponse>() {

            @Override
            public void onNext(@NonNull BaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
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

    public LiveData<BaseResponse> deleteUser(String id, String groupId){
        final MutableLiveData<BaseResponse> results = new MutableLiveData<>();
        deleteUserObservable(new DeleteUserRequest(id, groupId)).subscribe(deleteUserObserver(results));
        return results;
    }

    private Observable<BaseResponse> deleteUserObservable(DeleteUserRequest deleteUserRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deleteUser(RayanApplication.getPref().getToken(), deleteUserRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> deleteUserObserver(MutableLiveData<BaseResponse> results){
        return new DisposableObserver<BaseResponse>() {

            @Override
            public void onNext(@NonNull BaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
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


    public LiveData<BaseResponse> editGroup(String id, String groupId){
        final MutableLiveData<BaseResponse> results = new MutableLiveData<>();
        editGroupObservable(new EditGroupRequest(id, groupId)).subscribe(editGroupObserver(results));
        return results;
    }

    private Observable<BaseResponse> editGroupObservable(EditGroupRequest editGroupRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .editGroup(RayanApplication.getPref().getToken(), editGroupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> editGroupObserver(MutableLiveData<BaseResponse> results){
        return new DisposableObserver<BaseResponse>() {

            @Override
            public void onNext(@NonNull BaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
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


//    public LiveData<List<Contact>> getContacts(){
//        return contacts;
//    }
//
//    public void openContacts(){
//        new getContactsAsync().execute();
//    }
//
//    private class getContactsAsync extends AsyncTask<Void, Void ,Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            List<Contact> contacts = new ArrayList<>();
//            List<String> numbers;
//            cursor = application.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
//            while (cursor.moveToNext()) {
//                numbers = new ArrayList<>();
//                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                phones = application.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);
//                if("1".equals(hasPhone) || Boolean.parseBoolean(hasPhone)) {
//                    // You know it has a number so now query it like this
//                    while (phones.moveToNext()) {
//                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        phoneNumber = phoneNumber.replace(" ","");
//                        phoneNumber = phoneNumber.replace("+98", "0");
//                        int itype = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//                        final boolean isMobile =
//                                itype == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE ||
//                                        itype == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE;
//                        numbers.add(phoneNumber);
//                    }
//                }
//                if (numbers.size() != 0)
//                contacts.add(new Contact(contactId, name, numbers));
//                phones.close();
//            }
//            EditGroupFragmentViewModel.this.contacts.postValue(contacts);
//            return null;
//        }
//    }
}