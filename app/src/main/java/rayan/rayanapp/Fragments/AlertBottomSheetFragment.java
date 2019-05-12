package rayan.rayanapp.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.R;

public class AlertBottomSheetFragment extends BottomSheetDialogFragment {
    @BindView(R.id.message)
    AppCompatTextView message;

    public static AlertBottomSheetFragment instance(String message) {
        final AlertBottomSheetFragment fragment = new AlertBottomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("message",message);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_alert, container,
                false);
        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this,view);
        message.setText(getArguments().getString("message"));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
