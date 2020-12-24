package rayan.rayanapp.Dialogs.RemoteLearnButtonDialog;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.RemoteLearnNavigation;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class RemoteLearnButtonStep1 extends Fragment {
    public static RemoteLearnButtonStep1 newInstance(RemoteHub remoteHub) {
        RemoteLearnButtonStep1 step1 = new RemoteLearnButtonStep1();
        Bundle b = new Bundle();
        b.putParcelable("remoteHub", remoteHub);
        step1.setArguments(b);
        return step1;
    }
    private final String TAG = "RemoteLearnButtonStep1";
    @BindView(R.id.title)
    public TextView title;
    RemoteHub remoteHub;
    AddNewRemoteActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.remote_learn_button_dialog_1, container, false);
        ButterKnife.bind(this, v);
        remoteHub = getArguments().getParcelable("remoteHub");
        activity.getViewModel().enter_learn(remoteHub).observe(this, s -> {
            Log.e(TAG, "Response Received Enter Learn: " + s);
            if (s.isSuccessful()) {
                    remoteHub.setState(AppConstants.REMOTE_HUB_STATE_LEARN);
                    Toast.makeText(activity, "دکمه دریافت شد", Toast.LENGTH_SHORT).show();
                    learnDialog.go_next_step(s.getMessage());
            }
            else{
                Toast.makeText(activity, s.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
}
