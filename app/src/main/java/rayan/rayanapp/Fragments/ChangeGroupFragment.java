package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Adapters.recyclerView.AccessPointsRecyclerViewAdapter;
import rayan.rayanapp.Adapters.recyclerView.GroupsRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.ChangeDeviceAccessPointFragmentViewModel;
import rayan.rayanapp.ViewModels.GroupsListFragmentViewModel;

public class ChangeGroupFragment extends BottomSheetDialogFragment implements OnGroupClicked<Group> {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.selectedGroup)
    TextView selectedGroupTitle;
    GroupsRecyclerViewAdapter groupsRecyclerViewAdapter;
    GroupsListFragmentViewModel viewModel;
    private Group selectedGroup;
    public static ChangeGroupFragment newInstance() {
        final ChangeGroupFragment fragment = new ChangeGroupFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_group, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        Log.e("///////" ,"?//////: " + getTag());
        viewModel = ViewModelProviders.of(this).get(GroupsListFragmentViewModel.class);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        groupsRecyclerViewAdapter = new GroupsRecyclerViewAdapter(getActivity(), new ArrayList<>());
        groupsRecyclerViewAdapter.setListener(this);
        recyclerView.setAdapter(groupsRecyclerViewAdapter);
        viewModel.getAllGroups().observe(this,  groups -> {
            groupsRecyclerViewAdapter.setItems(groups);
        });
        groupsRecyclerViewAdapter.setListener(this);
    }
    @Override
    public void onGroupClicked(Group item) {
        selectedGroupTitle.setText(item.getName());
        selectedGroupTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
        selectedGroup = item;
        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),""+item.getName());
    }

    @Override
    public void onGroupLongPress(Group Item) {

    }

    @OnClick(R.id.createGroup)
    void createGroup(){
        recyclerView.setVisibility(View.INVISIBLE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.frameLayout, CreateGroupFragment.newInstance());
        transaction.commit();
    }

    @OnClick(R.id.confirm)
    void confirm(){
        if (selectedGroup != null){
            ((AddNewDeviceActivity)getActivity()).getNewDevice().setGroupId(selectedGroup.getId());
            dismiss();
        }
        else
          SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا یک گروه را انتخاب کنید");
    }

    @OnClick(R.id.cancel)
    void cancel(){
        dismiss();
    }

}
