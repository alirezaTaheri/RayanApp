package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Adapters.recyclerView.RemoteTypesRecyclerViewAdapter;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.Listeners.OnRemoteTypeClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.AddNewRemoteViewModel;


public class NewRemoteSelectLearnedFragment extends Fragment implements AddNewRemoteNavListener {


    private AddNewRemoteViewModel mViewModel;
    private AddNewRemoteActivity activity;
    private boolean learned = false;
    @BindView(R.id.learnedLayout)
    LinearLayout learnedWrapper;
    @BindView(R.id.staticLayout)
    LinearLayout notLearnedWrapper;
    public static NewRemoteSelectLearnedFragment newInstance() {
        return new NewRemoteSelectLearnedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_remote_select_learned_fragment, container, false);
        ButterKnife.bind(this, v);
        notLearnedWrapper.setBackground(ContextCompat.getDrawable(activity, R.drawable.new_remote_selected_type_backgrand));
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddNewRemoteViewModel.class);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddNewRemoteActivity) context;
    }

    @Override
    public void onBackClicked() {
        activity.onBackPressed();
    }

    @Override
    public void goToNextStep(Bundle data) {
        activity.doOnNext(data);
    }

    @Override
    public void verifyStatus() {
        Bundle data = new Bundle();
        data.putBoolean("learned", learned);
        goToNextStep(data);
    }

    @OnClick(R.id.learnedLayout)
    public void onLearnedClicked(){
        learnedWrapper.setBackground(ContextCompat.getDrawable(activity, R.drawable.new_remote_selected_type_backgrand));
        notLearnedWrapper.setBackground(null);
        learned = true;
    }
    @OnClick(R.id.staticLayout)
    public void onNotLearnedClicked(){
        notLearnedWrapper.setBackground(ContextCompat.getDrawable(activity, R.drawable.new_remote_selected_type_backgrand));
        learnedWrapper.setBackground(null);
        learned = false;
    }
}
