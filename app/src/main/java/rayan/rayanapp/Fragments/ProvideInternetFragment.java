package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.R;

public class ProvideInternetFragment extends BottomSheetDialogFragment {


    @BindView(R.id.title)
    TextView title;
    public static ProvideInternetFragment newInstance() {
        final ProvideInternetFragment fragment = new ProvideInternetFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provide_internet, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        title.setText("لطفا اینترنت را از یکی راه‌های زیر برقرار کنید" + "\nسپس دوباره تلاش کنید");
    }

    @OnClick(R.id.wifi)
    void goToWifi(){
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @OnClick(R.id.mobile)
    void goToMobile(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
        intent.setComponent(new ComponentName("com.android.settings",
                "com.android.settings.Settings$DataUsageSummaryActivity"));
        startActivity(intent);
    }

}
