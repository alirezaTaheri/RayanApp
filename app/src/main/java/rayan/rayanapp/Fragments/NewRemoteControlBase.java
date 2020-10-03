package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.AddNewRemoteViewModel;


public class NewRemoteControlBase extends Fragment implements AddNewRemoteNavListener {

    AddNewRemoteActivity activity;
    AlertDialog alertDialog;
    AddNewRemoteViewModel viewModel;
    public static NewRemoteControlBase newInstance(String type) {
        NewRemoteControlBase newRemoteFragment = new NewRemoteControlBase();
        Bundle b = new Bundle();
        b.putString("type", type);
        newRemoteFragment.setArguments(b);
        return newRemoteFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AddNewRemoteViewModel.class);
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
        goToNextStep(new Bundle());
    }

    public void nextModel(){

    }
}
