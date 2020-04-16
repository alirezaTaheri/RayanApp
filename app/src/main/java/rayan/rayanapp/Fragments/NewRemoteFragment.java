package rayan.rayanapp.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.R;


public class NewRemoteFragment extends Fragment implements AddNewRemoteNavListener {

    AddNewRemoteActivity activity;
    AlertDialog alertDialog;
    public static NewRemoteFragment newInstance(String type) {
        NewRemoteFragment newRemoteFragment = new NewRemoteFragment();
        Bundle b = new Bundle();
        b.putString("type", type);
        newRemoteFragment.setArguments(b);
        return newRemoteFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v;
        String remoteType = getArguments().getString("type");
        if (remoteType.equals("AC"))
            v = inflater.inflate(R.layout.remote_ac, container, false);
        else if (remoteType.equals("TV"))
            v = inflater.inflate(R.layout.remote_tv, container, false);
        else v = null;
        ButterKnife.bind(this, v);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getActivity().getWindow();
//            window.setStatusBarColor(Color.BLACK);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
////            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        }
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    public void goToNextStep(Map<String, String> data) {
        activity.doOnNext(data);
    }

    @Override
    public void verifyStatus() {
        alertDialog = new AlertDialog.Builder(activity)
                .setTitle("افزدون ریموت")
                .setMessage("آیا مطمئن هستید که این ریموت مناسب دستگاه شما است؟؟")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, String> data = new HashMap<>();
                        goToNextStep(data);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
