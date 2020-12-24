package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Data.Button;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Dialogs.RemoteLearnButtonDialog.RemoteLearnedButtonsListDialog;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.Listeners.RemoteLearnButtonDialogListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.AddNewRemoteViewModel;

import static android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID;


public class NewRemoteControlBase extends Fragment implements AddNewRemoteNavListener, RemoteLearnButtonDialogListener {

    AddNewRemoteActivity activity;
    AlertDialog alertDialog;
    AddNewRemoteViewModel viewModel;
    ArrayList<Button> buttons = new ArrayList<>();
    Map<String, Button> buttonMap = new HashMap<>();
    Remote remote;
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Bundle data = new Bundle();
        buttons = new ArrayList<>();
        buttons.addAll(buttonMap.values());
        data.putParcelableArrayList("buttons",buttons);
        goToNextStep(data);
    }

    public void nextModel(){

    }

    public void showButtons(){
        buttons = new ArrayList<>();
        buttons.addAll(buttonMap.values());
        RemoteLearnedButtonsListDialog buttonsListDialog = RemoteLearnedButtonsListDialog.newInstance(buttons);
        buttonsListDialog.show(getChildFragmentManager(), "ButtonsList");
    }

    @Override
    public void create_button(Button button) {

    }

    public void setButtonMap(Map<String, Button> buttonMap) {
        for (String key : this.buttonMap.keySet())
            if (Arrays.asList(remote.getType().equals(AppConstants.REMOTE_TYPE_AC)?AppConstants.AC_REMOTE_BUTTONS:AppConstants.TV_REMOTE_BUTTONS).contains(key)){
                removeBadge(key+"_learned", (ViewGroup) getView());
            }
        this.buttonMap = buttonMap;
        for (String key : this.buttonMap.keySet())
            if (Arrays.asList(remote.getType().equals(AppConstants.REMOTE_TYPE_AC)?AppConstants.AC_REMOTE_BUTTONS:AppConstants.TV_REMOTE_BUTTONS).contains(key))
                addLearnedBadge(getView(), key);
        activity.setBottomMessage("تعداد دکمه‌های ایجاد شده: "+buttonMap.size());
    }

    protected void addLearnedBadge(View v, String tag){
        ImageView learned = new ImageView(activity);
        learned.setTag(tag+"_learned");
        ConstraintLayout layout = v.findViewWithTag(tag);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.topToTop = PARENT_ID;
        lp.rightToRight = PARENT_ID;
        learned.setTranslationY(-10 * activity.getResources().getDisplayMetrics().density);
        learned.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_checked));
        layout.addView(learned, lp);
    }
    protected void removeBadge(String tag, ViewGroup parent){
        View badge = parent.findViewWithTag(tag);
        ((ViewGroup)badge.getParent()).removeView(parent.findViewWithTag(tag));
    }
}
