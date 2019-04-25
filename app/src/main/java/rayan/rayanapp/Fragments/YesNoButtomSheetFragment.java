package rayan.rayanapp.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.R;

public class YesNoButtomSheetFragment extends BottomSheetDialogFragment {
    private OnBottomSheetSubmitClicked onBottomSheetSubmitClicked;
    public void setOnBottomSheetSubmitClicked(OnBottomSheetSubmitClicked onBottomSheetSubmitClicked){
        this.onBottomSheetSubmitClicked=onBottomSheetSubmitClicked;
    }
    private String submitTxt;
    private String cancelTxt;
    private String sheetTxt;
   private String tag;
    @BindView(R.id.submitBtn)
    public AppCompatButton submitBtn;
    @BindView(R.id.cancelBtn)
    public AppCompatButton cancelBtn;
    @BindView(R.id.bottomSheetText)
    AppCompatTextView bottomSheetText;
    public static YesNoButtomSheetFragment instance(String tag, String submitTxt, String cancelTxt, String sheetTxt) {
        final YesNoButtomSheetFragment fragment = new YesNoButtomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("TAG",tag);
        args.putString("SHEETTXT",sheetTxt);
        args.putString("SUBMITTXT",submitTxt);
        args.putString("CANCELTXT",cancelTxt);
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
        tag=getArguments().getString("TAG");
        sheetTxt=getArguments().getString("SHEETTXT");
        submitTxt=getArguments().getString("SUBMITTXT");
        cancelTxt=getArguments().getString("CANCELTXT");
    }

    @OnClick(R.id.submitBtn)
    void clickOnSubmit(){
        onBottomSheetSubmitClicked.submitClicked(tag);
        dismiss();
        }

    @OnClick(R.id.cancelBtn)
    void clickOnCancel(){
        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBottomSheetSubmitClicked) {
            onBottomSheetSubmitClicked = (OnBottomSheetSubmitClicked) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        onBottomSheetSubmitClicked = null;
    }
}
