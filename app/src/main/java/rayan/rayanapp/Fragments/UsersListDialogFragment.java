package rayan.rayanapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Adapters.recyclerView.ContactsRecyclerViewAdapter;
import rayan.rayanapp.Listeners.OnContactClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class UsersListDialogFragment extends BottomSheetDialogFragment implements OnContactClicked<User> {

    Set<String> candidates = new HashSet<>();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Group group;
    ContactsRecyclerViewAdapter contactsRecyclerViewAdapter;
    EditGroupFragmentViewModel editGroupFragmentViewModel;
    List<User> nonAdmins;
    public static UsersListDialogFragment newInstance(Group group) {
        final UsersListDialogFragment fragment = new UsersListDialogFragment();
        final Bundle args = new Bundle();
        args.putParcelable("group", group);
        args.putString("id", group.getId());
        fragment.setArguments(args);
        return fragment;
    }
    @OnClick(R.id.cancel)
    void cancel(){
        this.dismiss();
    }
    @OnClick(R.id.confirm)
    void confirm(){
        editGroupFragmentViewModel.addAdmin(candidates,group.getId()).observe(this, baseResponse -> {
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
                Toast.makeText(getActivity(), "کاربری با این شماره وجود ندارد", Toast.LENGTH_SHORT).show();
            }
            else if (baseResponse.getStatus().getCode().equals("400") && baseResponse.getData().getMessage().equals("Repeated")){
                Toast.makeText(getActivity(), "این کاربر هم‌اکنون عضو گروه است", Toast.LENGTH_SHORT).show();
            }
            else if (baseResponse.getStatus().getCode().equals("200")){
                Toast.makeText(getActivity(), "مدیران گروه با موفقیت اصلاح شدند", Toast.LENGTH_SHORT).show();
                editGroupFragmentViewModel.getGroups();
                dismiss();
            }
            else
                Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
        });
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list_dialog, container, false);
        ButterKnife.bind(this, view);
        contactsRecyclerViewAdapter = new ContactsRecyclerViewAdapter(getActivity());
        contactsRecyclerViewAdapter.setItems(nonAdmins);
        contactsRecyclerViewAdapter.setListener(this);
        recyclerView.setAdapter(contactsRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = getArguments().getParcelable("group");
        editGroupFragmentViewModel = ViewModelProviders.of(this).get(EditGroupFragmentViewModel.class);
        nonAdmins = new ArrayList<>();
        editGroupFragmentViewModel.getJustUsersInGroup(getArguments().getString("id")).observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                Log.e("justjustjust", "jus:" + users + getArguments().getString("id"));
                nonAdmins = users;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                    for (int i = 0; i <= users.size() - 1; i++) {
                        nonAdmins.get(i).setContactNameOnPhone(editGroupFragmentViewModel.getContactNameFromPhone(nonAdmins.get(i).getUsername(), getActivity()));
                        nonAdmins.get(i).setContactImageOnPhone(editGroupFragmentViewModel.getContactImageFromPhone(nonAdmins.get(i).getUsername(), getActivity()));
                    }
                    contactsRecyclerViewAdapter.setItems(nonAdmins);
                if (nonAdmins.size()==0){
                    Toast.makeText(getActivity(), "عضوی برای مدیریت وجود ندارد", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
//        for (int a= 0;a<group.getHumanUsers().size(); a++){
//            boolean flag = false;
//            for (int b = 0; b<group.getAdmins().size();b++){
//                if (group.getHumanUsers().get(a).getId().equals(group.getAdmins().get(b).getId()))
//                    flag = true;
//            }
//            if (!flag) nonAdmins.add(group.getHumanUsers().get(a));
//        }

    }

    Activity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onContactClicked(User item) {
        if (item.isSelected())
            candidates.add(item.getId());
        else
            candidates.remove(item.getId());
    }

}
