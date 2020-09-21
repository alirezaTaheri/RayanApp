package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.AddNewRemoteViewModel;


public class NewRemoteSetConfigurationFragment extends Fragment implements AddNewRemoteNavListener {


    private AddNewRemoteViewModel viewModel;
    @BindView(R.id.name)
    EditText nameEditText;
    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.group)
    TextView groupName;
    @BindView(R.id.remoteHub)
    TextView remoteHubName;
    @BindView(R.id.favorite)
    ImageView favorite;
    @BindView(R.id.visibility)
    ImageView visibility;
    @BindView(R.id.changeRemoteHub)
    ImageView changeRemoteHub;
    AddNewRemoteActivity activity;
    private Remote remote;
    private RemoteHub remoteHub;
    public static NewRemoteSetConfigurationFragment newInstance(Remote remote) {
        Bundle b = new Bundle();
        b.putParcelable("remote", remote);
        NewRemoteSetConfigurationFragment setConfigurationFragment = new NewRemoteSetConfigurationFragment();
        setConfigurationFragment.setArguments(b);
        return setConfigurationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_remote_set_configuration_fragment, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.grey_light_4));
        }
        viewModel = ViewModelProviders.of(this).get(AddNewRemoteViewModel.class);
        ButterKnife.bind(this, v);
        remote = getArguments().getParcelable("remote");
//        nameEditText.setText("");

        Log.e("FRTFRTFRT", "Adding New Remote " + remote.getRemoteHubId()+remote);
        remoteHub = viewModel.getRemoteHub(remote.getRemoteHubId());
        remoteHubName.setText(remoteHub != null ? remoteHub.getName(): "انتخاب کنید");
        Group group = viewModel.getGroup(remote.getGroupId());
        groupName.setText(group != null ? group.getName(): "---");
        switch (remote.getType()){
            case AppConstants.REMOTE_TYPE_TV:
                type.setText("ریموت تلویزیون");
                break;
            case AppConstants.REMOTE_TYPE_AC:
                type.setText("ریموت کولرگازی");
                break;
        }
        favorite.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_star_empty));
        visibility.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_visibility_on));
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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
    public void goToNextStep(Bundle data) {
        activity.doOnNext(data);
    }


    @Override
    public void verifyStatus() {
        if (!nameEditText.getText().toString().trim().equals("")){
            Bundle data = new Bundle();
            data.putBoolean("favorite", remote.isFavorite());
            data.putBoolean("visibility", remote.isVisibility());
            data.putString("name", nameEditText.getText().toString());
            data.putString("remoteHubId", remoteHub.getId());
            goToNextStep(data);

        }
        else Toast.makeText(activity, "لطفا نام ریموت را وارد کنید", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.favorite)
    public void onFavoriteClicked(){
        remote.setFavorite(!remote.isFavorite());
        favorite.setImageDrawable(ContextCompat.getDrawable(activity,remote.isFavorite()?R.drawable.ic_star_full:R.drawable.ic_star_empty));
    }
    @OnClick(R.id.visibility)
    public void onVisibility(){
        remote.setVisibility(!remote.isVisibility());
        visibility.setImageDrawable(ContextCompat.getDrawable(activity,remote.isVisibility()?R.drawable.ic_visibility_on:R.drawable.ic_visibility_off));
    }
    SelectRemoteHubFragment selectRemoteHubFragment;
    @OnClick({R.id.changeRemoteHub, R.id.remoteHub})
    public void onChangeRemoteHubClicked(){
        selectRemoteHubFragment = SelectRemoteHubFragment.newInstance(remoteHub);
        selectRemoteHubFragment.show(activity.getSupportFragmentManager(), "selectRemoteHub");
    }

    public void updateRemoteHub(RemoteHub remoteHub){
        selectRemoteHubFragment.dismiss();
        this.remoteHub = remoteHub;
        remoteHubName.setText(remoteHub.getName());
        remote.setRemoteHubId(remoteHub.getId());
        remote.setGroupId(remoteHub.getGroupId());
        Group group = viewModel.getGroup(remote.getGroupId());
        groupName.setText(group != null ? group.getName(): "---");
    }
}
