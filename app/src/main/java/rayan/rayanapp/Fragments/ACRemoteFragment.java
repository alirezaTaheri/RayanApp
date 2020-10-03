package rayan.rayanapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;


public class ACRemoteFragment extends NewRemoteControlBase {

    String brand = null;
    private String currentModel = null;
    public static ACRemoteFragment newInstance(String brand) {
        ACRemoteFragment fragment = new ACRemoteFragment();
        Bundle b = new Bundle();
        b.putString("brand",brand);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.remote_ac, container, false);
        ButterKnife.bind(this, v);
        brand = getArguments().getString("brand");
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.power)
    public void onClick_power(){

    }
    @OnClick(R.id.mode)
    public void onClick_mode(){

    }
    @OnClick(R.id.airVolume)
    public void onClick_airVolume(){

    }
    @OnClick(R.id.more)
    public void onClick_more(){

    }
    @OnClick(R.id.tempDown)
    public void onClick_tempDown(){

    }
    @OnClick(R.id.power)
    public void onClick_tempUp(){

    }

    @Override
    public void nextModel() {
        Map<String, String> params = new HashMap<>();
        params.put("brand",brand);
        params.put("type", AppConstants.REMOTE_TYPE_AC);
        viewModel.getRemoteData(params);
    }
}
