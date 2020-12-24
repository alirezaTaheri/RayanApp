package rayan.rayanapp.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Listeners.BottomSheetClickListener;
import rayan.rayanapp.R;

public class ConfirmBottomSheetFragment extends BottomSheetDialogFragment {
    private BottomSheetClickListener listener;

    private String submitTxt;
    private String cancelTxt;
    private String sheetTxt;
    private String tag;
    private Parcelable object;
    @BindView(R.id.submitBtn)
    public AppCompatButton submitBtn;
    @BindView(R.id.cancelBtn)
    public AppCompatButton cancelBtn;
    @BindView(R.id.bottomSheetText)
    AppCompatTextView bottomSheetText;
    int position = -1;
    public static ConfirmBottomSheetFragment instance(String tag, String submitTxt, String cancelTxt, String sheetTxt, Parcelable object, int position) {
        final ConfirmBottomSheetFragment fragment = new ConfirmBottomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("TAG",tag);
        args.putString("text",sheetTxt);
        args.putString("confirmText",submitTxt);
        args.putString("cancelText",cancelTxt);
        args.putParcelable("object", object);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_action_bottomsheet, container,
                false);
        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this,view);
        submitBtn.setText(submitTxt);
        cancelBtn.setText(cancelTxt);
        bottomSheetText.setText(sheetTxt);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = getArguments().getString("TAG");
        sheetTxt = getArguments().getString("text");
        submitTxt = getArguments().getString("confirmText");
        cancelTxt = getArguments().getString("cancelText");
        object = getArguments().getParcelable("object");
        position = getArguments().getInt("position");
        listener = (BottomSheetClickListener) getParentFragment();
    }

    @OnClick(R.id.submitBtn)
    void clickOnSubmit(){
        listener.submitClicked(tag, object, position);
        dismiss();
    }

    @OnClick(R.id.cancelBtn)
    void clickOnCancel(){
        dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
