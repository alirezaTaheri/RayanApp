package rayan.rayanapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Activities.RemoteActivity;
import rayan.rayanapp.Adapters.recyclerView.RemotesRecyclerViewAdapter;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.Listeners.OnRemoteClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.DevicesFragmentViewModel;

public class SelectRemoteBottomSheetFragment extends BottomSheetDialogFragment implements OnRemoteClicked<Remote> {
    private OnBottomSheetSubmitClicked onBottomSheetSubmitClicked;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.addWrapper)
    LinearLayout addNewRemote;
    @BindView(R.id.name)
    TextView name;
    RemotesRecyclerViewAdapter recyclerViewAdapter;
    RemoteHub remoteHub;
    List<Remote> remotes = new ArrayList<>();
    DevicesFragmentViewModel viewModel;
    public static SelectRemoteBottomSheetFragment instance(RemoteHub remoteHub) {
        final SelectRemoteBottomSheetFragment fragment = new SelectRemoteBottomSheetFragment();
        final Bundle args = new Bundle();
        args.putParcelable("remoteHub",remoteHub);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_select_remote, container,
                false);
//        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this,view);
        addNewRemote.setOnClickListener((view1)->{
            Intent intent = new Intent(getActivity(), AddNewRemoteActivity.class);
            Bundle data = new Bundle();
            data.putParcelable("remoteHub", remoteHub);
            intent.putExtras(data);
            startActivity(intent);
            dismiss();
        });
        remoteHub = getArguments().getParcelable("remoteHub");
        name.setText(remoteHub.getName());
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DevicesFragmentViewModel.class);
        viewModel.getRemotesOfRemoteHub(remoteHub.getId()).observe(this, new Observer<List<Remote>>() {
            @Override
            public void onChanged(@Nullable List<Remote> remotes) {
                SelectRemoteBottomSheetFragment.this.remotes = remotes;
                recyclerViewAdapter.updateItems(remotes);
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerViewAdapter = new RemotesRecyclerViewAdapter(getContext(),new ArrayList<>());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        onBottomSheetSubmitClicked = null;
    }

    @Override
    public void onRemoteClicked(Remote item) {
        Intent intent = new Intent(getActivity(), RemoteActivity.class);
        intent.putExtra("type", item.getType());
        dismiss();
        startActivity(intent);
    }

}
