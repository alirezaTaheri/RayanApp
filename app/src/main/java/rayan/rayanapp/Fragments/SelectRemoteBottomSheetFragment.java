package rayan.rayanapp.Fragments;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Activities.RemoteActivity;
import rayan.rayanapp.Adapters.recyclerView.RemotesRecyclerViewAdapter;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.Listeners.OnRemoteClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class SelectRemoteBottomSheetFragment extends BottomSheetDialogFragment implements OnRemoteClicked<Remote> {
    private OnBottomSheetSubmitClicked onBottomSheetSubmitClicked;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.addWrapper)
    LinearLayout addNewRemote;
    @BindView(R.id.name)
    TextView name;
    RemotesRecyclerViewAdapter recyclerViewAdapter;
    public static SelectRemoteBottomSheetFragment instance(String name) {
        final SelectRemoteBottomSheetFragment fragment = new SelectRemoteBottomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("name",name);
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
            startActivity(new Intent(getActivity(), AddNewRemoteActivity.class));
            dismiss();
        });
        name.setText(getArguments().getString("name"));
        ArrayList<Remote> remotes = new ArrayList<>();
        Remote r = new Remote();
        Remote r2 = new Remote();
        r.setName("remote Test");
        r2.setName("Ac Test");
        r2.setType(AppConstants.REMOTE_TYPE_AC);
        r.setType(AppConstants.REMOTE_TYPE_TV);
        remotes.add(r);
        remotes.add(r2);
        remotes.add(r);
        recyclerViewAdapter.updateItems(remotes);
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
