package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Adapters.recyclerView.DevicesRecyclerViewAdapter;
import rayan.rayanapp.Adapters.recyclerView.GroupsRecyclerViewAdapter;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.DevicesFragmentViewModel;
import rayan.rayanapp.ViewModels.GroupsListFragmentViewModel;


public class SelectRemoteHubFragment extends BottomSheetDialogFragment implements OnGroupClicked<Group> , View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.create)
    TextView create;
    DevicesRecyclerViewAdapter recyclerViewAdapter;
    DevicesFragmentViewModel viewModel;
    private RemoteHub selectedGroup;
    private RemoteHubsAdapter adapter;

    public static SelectRemoteHubFragment newInstance() {
        final SelectRemoteHubFragment fragment = new SelectRemoteHubFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_remote_hub, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        viewModel = ViewModelProviders.of(this).get(DevicesFragmentViewModel.class);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new RemoteHubsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
//        create.setOnClickListener(this);
//        if (((AddNewDeviceActivity)getActivity()).getNewDevice().getGroup() != null)
//            selectedGroupTitle.setText(((AddNewDeviceActivity)getActivity()).getNewDevice().getGroup().getName());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onGroupClicked(Group item) {
//        selectedGroupTitle.setText(item.getName());
//        selectedGroupTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
//        selectedGroup = item;
//        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),""+item.getName());
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
//        if (selectedGroup != null){
//            ((AddNewDeviceActivity)getActivity()).getNewDevice().setGroup(selectedGroup);
//            ((NewDeviceSetConfigurationFragment)((AddNewDeviceActivity)getActivity()).getStepperAdapter().findStep(1)).setGroupTitle(selectedGroup.getName());
//
//            dismiss();
//        }
//        else
//          SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا یک گروه را انتخاب کنید");
    }

    @OnClick(R.id.cancel)
    void cancel(){
        dismiss();
    }

    private void createGroupMode(){
        mode = Mode.CREATE_GROUP;
        recyclerView.setVisibility(View.INVISIBLE);
//        createGroup.setText("انتخاب گروه");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        transaction.add(R.id.frameLayout, CreateGroupFragment.newInstance(), "createGroup");
//        transaction.addToBackStack(null);
        transaction.commit();
    }



    public void selectGroupMode(){
//        groupsRecyclerViewAdapter.setItems(viewModel.getAllGroups());
        mode = Mode.SELECT_GROUP;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        transaction.remove(getChildFragmentManager().findFragmentByTag("createGroup"));
        transaction.commit();
        recyclerView.setVisibility(View.VISIBLE);
//        createGroup.setText("ایجاد گروه");
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


    public class RemoteHubsAdapter extends RecyclerView.Adapter<RemoteHubViewHolder>{
        List<RemoteHub> items = new ArrayList<>();
        public RemoteHubsAdapter(List<RemoteHub> items){
            this.items = items;
        }

        @NonNull
        @Override
        public RemoteHubViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new RemoteHubViewHolder(getLayoutInflater().inflate(R.layout.item_remotehub_set_config_page, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RemoteHubViewHolder remoteHubViewHolder, int i) {
            remoteHubViewHolder.bind(items.get(i));
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
    public class RemoteHubViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.name)
        TextView name;
        public RemoteHubViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void bind(RemoteHub remoteHub){
            name.setText(remoteHub.getName());
        }
    }

    Activity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddNewRemoteActivity) context;
    }
}
