package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Adapters.recyclerView.RemoteTypesRecyclerViewAdapter;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.Listeners.OnRemoteTypeClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.AddNewRemoteViewModel;


public class NewRemoteSelectTypeFragment extends Fragment implements OnRemoteTypeClicked<String>, AddNewRemoteNavListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private RemoteTypesRecyclerViewAdapter recyclerViewAdapter;
    private AddNewRemoteViewModel mViewModel;
    private AddNewRemoteActivity activity;
    private Pair<String, Integer> selectedType = new Pair<>("", -1);
    public static NewRemoteSelectTypeFragment newInstance() {
        return new NewRemoteSelectTypeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_remote_select_type_fragment, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddNewRemoteViewModel.class);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL,false));
        recyclerViewAdapter = new RemoteTypesRecyclerViewAdapter(getActivity(), mViewModel.getAllTypes());
        recyclerViewAdapter.setListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddNewRemoteActivity) context;
    }

    @Override
    public void onRemoteTypeClicked(String item, int position) {
        recyclerViewAdapter.notifyItemChanged(selectedType.second);
        selectedType = new Pair<>(item, position);
        recyclerViewAdapter.notifyItemChanged(selectedType.second);
//        activity.goToSelectBrand(item);
    }

    public String getSelectedType() {
        return selectedType.first;
    }

    @Override
    public void onBackClicked() {
        activity.onBackPressed();
    }

    @Override
    public void goToNextStep(Map<String, String> data) {
        Map<String, String> map = new HashMap<>();
        map.put("type", selectedType.first);
        activity.doOnNext(map);
    }

    @Override
    public void verifyStatus() {
        if (!selectedType.first.equals(""))
            goToNextStep(null);
    }

}
