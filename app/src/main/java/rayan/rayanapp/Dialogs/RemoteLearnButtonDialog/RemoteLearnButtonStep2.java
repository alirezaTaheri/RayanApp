package rayan.rayanapp.Dialogs.RemoteLearnButtonDialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.RemoteLearnNavigation;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class RemoteLearnButtonStep2 extends Fragment {
    public static RemoteLearnButtonStep2 newInstance(RemoteHub remoteHub, String buttonName, String remoteType) {
        RemoteLearnButtonStep2 step2 = new RemoteLearnButtonStep2();
        Bundle b = new Bundle();
        b.putParcelable("remoteHub", remoteHub);
        b.putString("buttonName", buttonName);
        b.putString("remoteType", remoteType);
        step2.setArguments(b);
        return step2;
    }
    private final String TAG = "RemoteLearnButtonStep2";
    @BindView(R.id.title)
    public TextView title;
    RemoteHub remoteHub;
    AddNewRemoteActivity activity;
    @BindView(R.id.name)
    Spinner name;
    String buttonName, remoteType;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.remote_learn_button_dialog_2, container, false);
        ButterKnife.bind(this, v);
        remoteHub = getArguments().getParcelable("remoteHub");
        buttonName = getArguments().getString("buttonName");
        remoteType = getArguments().getString("remoteType");
        String[] buttons = null;
        Log.e(TAG, ""+remoteType);
        if (remoteType.equals(AppConstants.REMOTE_TYPE_TV))
            buttons = AppConstants.TV_REMOTE_BUTTONS;
        else if (remoteType.equals(AppConstants.REMOTE_TYPE_AC))
            buttons = AppConstants.AC_REMOTE_BUTTONS;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, buttons);
        name.setAdapter(adapter);
        for (int a= 0;a<buttons.length;a++) {
            if (buttons[a].replace(" ","").toLowerCase().equals(buttonName.toLowerCase())) {
                name.setSelection(a);
                break;
            }
        }
        return v;
    }


    RemoteLearnNavigation learnDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        learnDialog = (RemoteLearnNavigation) getParentFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddNewRemoteActivity) context;
    }

    public String onConfirmClicked(){
        return name.getSelectedItem().toString();
    }
}
