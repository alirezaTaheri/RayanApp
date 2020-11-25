package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Adapters.recyclerView.GroupsRecyclerViewAdapter;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
//<<<<<<< HEAD
//<<<<<<< HEAD
//=======
import rayan.rayanapp.Util.SnackBarSetup;
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
//=======

//>>>>>>> 61f7df95c05f5e7b5402a088a45aa1e4642821eb
import rayan.rayanapp.ViewModels.GroupsListFragmentViewModel;

public class ChangeGroupFragment extends BottomSheetDialogFragment implements OnGroupClicked<Group> , View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.selectedGroup)
    TextView selectedGroupTitle;
    @BindView(R.id.createGroup)
    TextView createGroup;
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
        viewModel = ViewModelProviders.of(this).get(GroupsListFragmentViewModel.class);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        groupsRecyclerViewAdapter = new GroupsRecyclerViewAdapter(getActivity(), new ArrayList<>());
        groupsRecyclerViewAdapter.setListener(this);
        recyclerView.setAdapter(groupsRecyclerViewAdapter);
        groupsRecyclerViewAdapter.setListener(this);
        createGroup.setOnClickListener(this);
        if (((AddNewDeviceActivity)getActivity()).getNewDevice().getGroup() != null)
            selectedGroupTitle.setText(((AddNewDeviceActivity)getActivity()).getNewDevice().getGroup().getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getAllGroupsLive().observe(this, groups -> {
            groupsRecyclerViewAdapter.setItems(groups);
        });
    }

    @Override
    public void onGroupClicked(Group item) {
        selectedGroupTitle.setText(item.getName());
        selectedGroupTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
        selectedGroup = item;
        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),""+item.getName());
    }



//    @OnClick(R.id.createGroup)
//    void createGroup(){
//        createGroupMode();
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.add(R.id.frameLayout, CreateGroupFragment.newInstance());
//        transaction.commit();
//    }

    @OnClick(R.id.confirm)
    void confirm(){
        if (selectedGroup != null){
            ((AddNewDeviceActivity)getActivity()).getNewDevice().setGroup(selectedGroup);
            ((NewDeviceSetConfigurationFragment)((AddNewDeviceActivity)getActivity()).getStepperAdapter().findStep(1)).setGroupTitle(selectedGroup.getName());

            dismiss();
        }
        else
          SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا یک گروه را انتخاب کنید");
    }

    @OnClick(R.id.cancel)
    void cancel(){
        dismiss();
    }

    private void createGroupMode(){
        mode = Mode.CREATE_GROUP;
        recyclerView.setVisibility(View.INVISIBLE);
        createGroup.setText("انتخاب گروه");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        transaction.add(R.id.frameLayout, CreateGroupFragment.newInstance(), "createGroup");
//        transaction.addToBackStack(null);
        transaction.commit();
    }



    public void selectGroupMode(){
        groupsRecyclerViewAdapter.setItems(viewModel.getAllGroups());
        mode = Mode.SELECT_GROUP;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        transaction.remove(getChildFragmentManager().findFragmentByTag("createGroup"));
        transaction.commit();
        recyclerView.setVisibility(View.VISIBLE);
        createGroup.setText("ایجاد گروه");
    }
    public Mode mode = Mode.SELECT_GROUP;
    private enum Mode {
        CREATE_GROUP,
        SELECT_GROUP

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.createGroup){
            if (mode.equals(Mode.CREATE_GROUP)){
                selectGroupMode();
            }else{
                createGroupMode();
            }
        }
    }
}
