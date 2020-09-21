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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


public class SelectRemoteHubFragment extends BottomSheetDialogFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.title)
    TextView title;
    DevicesRecyclerViewAdapter recyclerViewAdapter;
    DevicesFragmentViewModel viewModel;
    private RemoteHub selectedRemoteHub;
    private RemoteHubsAdapter adapter;
    List<RemoteHub> remoteHubs = new ArrayList<>();
    public static SelectRemoteHubFragment newInstance(RemoteHub remoteHub) {
        final SelectRemoteHubFragment fragment = new SelectRemoteHubFragment();
        final Bundle args = new Bundle();
        if (remoteHub != null)
            args.putParcelable("remoteHub",remoteHub);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_remote_hub, container, false);
        ButterKnife.bind(this, view);
        title.setText("لطفا ریموت هاب مورد نظر را انتخاب کنید");
        viewModel = ViewModelProviders.of(this).get(DevicesFragmentViewModel.class);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new RemoteHubsAdapter(remoteHubs, this);
        selectedRemoteHub = getArguments().getParcelable("remoteHub");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        viewModel.getAllRemoteHubsLive().observe(this, remoteHubs -> {
            Log.d("FFFFFFFFFFFFFFF", "All RemoteHubs: " + remoteHubs);
            this.remoteHubs = remoteHubs;
            adapter.updateItems(remoteHubs);
        });
        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

//        create.setOnClickListener(this);
//        if (((AddNewDeviceActivity)getActivity()).getNewDevice().getGroup() != null)
//            selectedGroupTitle.setText(((AddNewDeviceActivity)getActivity()).getNewDevice().getGroup().getName());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.confirm)
    void confirm(){
        activity.getSetConfigurationFragment().updateRemoteHub(selectedRemoteHub);
    }

    @OnClick(R.id.cancel)
    void cancel(){
        dismiss();
    }

    public void onRemoteHubClicked(RemoteHub remoteHub){
        selectedRemoteHub = remoteHub;
        title.setText(remoteHub.getName());
        adapter.updateItems(remoteHubs);
    }

    public class RemoteHubsAdapter extends RecyclerView.Adapter<RemoteHubViewHolder>{
        List<RemoteHub> items = new ArrayList<>();
        SelectRemoteHubFragment fragment;
        public RemoteHubsAdapter(List<RemoteHub> items, SelectRemoteHubFragment fragment){
            this.items = items;
            this.fragment = fragment;
        }
        public void updateItems(List<RemoteHub> items){
            this.items = items;
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public RemoteHubViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new RemoteHubViewHolder(getLayoutInflater().inflate(R.layout.item_remotehub_set_config_page, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RemoteHubViewHolder remoteHubViewHolder, int i) {
            Log.d("fffffffffffffff", "onBindViewHolder() called with: , i = [" + i + "]");
            remoteHubViewHolder.bind(items.get(i), fragment);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
    public class RemoteHubViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.strip)
        ImageView strip;
        public RemoteHubViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void bind(RemoteHub remoteHub, SelectRemoteHubFragment fragment){
            if (fragment.selectedRemoteHub != null && fragment.selectedRemoteHub.getId().equals(remoteHub.getId()))
                strip.setBackground(ContextCompat.getDrawable(fragment.activity,R.color.baseColor));
            else strip.setBackground(ContextCompat.getDrawable(fragment.activity,R.color.grey));
            name.setText(remoteHub.getName());
            itemView.setOnClickListener(v -> {
                fragment.onRemoteHubClicked(remoteHub);
            });
        }
    }

    AddNewRemoteActivity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddNewRemoteActivity) context;
    }
}
