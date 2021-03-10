package rayan.rayanapp.Dialogs.RemoteLearnButtonDialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import rayan.rayanapp.Data.Button;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.RemoteLearnButtonDialogListener;
import rayan.rayanapp.Listeners.RemoteLearnNavigation;
import rayan.rayanapp.R;

public class RemoteLearnButtonDialog extends DialogFragment implements RemoteLearnNavigation {


    public static RemoteLearnButtonDialog newInstance(RemoteHub remoteHub, Remote remote, String button) {
        RemoteLearnButtonDialog dialog = new RemoteLearnButtonDialog();
        Bundle b = new Bundle();
        b.putParcelable("remoteHub", remoteHub);
        b.putParcelable("remote", remote);
        b.putString("button", button);
        dialog.setArguments(b);
        return dialog;
    }

    Disposable disposable;
    private final String TAG = "RemoteLearnButtonDialog";
    RemoteHub remoteHub;
    @BindView(R.id.confirm)
    TextView confirmButton;
    @BindView(R.id.cancel)
    TextView cancelButton;
    RemoteLearnButtonStep2 step2;
    RemoteLearnButtonStep1 step1;
    Button currentButton;
    Remote remote;
    RemoteLearnButtonDialogListener remoteFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        remoteFragment = (RemoteLearnButtonDialogListener) getParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@android.support.annotation.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.remote_learn_button_dialog, container, false);
        remoteHub = getArguments().getParcelable("remoteHub");
        remote = getArguments().getParcelable("remote");
        ButterKnife.bind(this, v);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        step1 = RemoteLearnButtonStep1.newInstance(remoteHub);
        transaction.replace(R.id.fragment_container, step1).commit();
        return v;
    }

    @Override
    public void go_next_step(String signal) {
        currentButton = new Button();
        currentButton.setName(getArguments().getString("button"));
        currentButton.setType(remote.getType());
        currentButton.setBrand(remote.getBrand());
        currentButton.setSignal(signal);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        step2 = RemoteLearnButtonStep2.newInstance(remoteHub, currentButton.getName(), remote.getType());
        transaction.replace(R.id.fragment_container, step2).commit();
        confirmButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void close() {
        dismiss();
    }

    @OnClick(R.id.confirm)
    public void onConfirmClicked(){
        String name = step2.onConfirmClicked();
        currentButton.setName(name);
        remoteFragment.create_button(currentButton);
        dismiss();
    }
    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick(R.id.cancel)
    public void cancel(){
        if(disposable!=null)
        disposable.dispose();
        dismiss();
    }
}
