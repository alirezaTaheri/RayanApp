package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.CreateGroupViewModel;

public class CreateGroupButtomSheetFragment extends BottomSheetDialogFragment {
    private String name;
    private static ArrayList<String> numbers = new ArrayList<>();
    private CreateGroupViewModel createGroupViewModel;
    @BindView(R.id.submitBtn)
    AppCompatButton submitBtn;
    @BindView(R.id.cancelBtn)
    AppCompatButton cancelBtn;
    @BindView(R.id.bottomSheetText)
    AppCompatTextView bottomSheetText;


    public static CreateGroupButtomSheetFragment newInstance(String namee, ArrayList<String> numbers ) {
        final CreateGroupButtomSheetFragment fragment = new CreateGroupButtomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("NAME", namee);
        args.putStringArrayList("NUMBERS",numbers);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_action_bottomsheet, container,
                false);
        ButterKnife.bind(this,view);
        submitBtn.setText("ایجاد گروه");
        cancelBtn.setText("بازگشت");
        bottomSheetText.setText("آیا مایل به ایجاد گروه هستید؟");
        createGroupViewModel = ViewModelProviders.of(this).get(CreateGroupViewModel.class);
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       name=getArguments().getString("NAME");
       numbers=getArguments().getStringArrayList("NUMBERS");
    }

    @OnClick(R.id.submitBtn)
    void clickOnSubmit(){
        createGroupViewModel.createGroup(name, numbers).observe(this, baseResponse -> {
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
                Toast.makeText(getActivity(), "کاربری با این شماره وجود ندارد", Toast.LENGTH_SHORT).show();
                dismiss();
            }
            else if (baseResponse.getStatus().getCode().equals("400") && baseResponse.getData().getMessage().equals("Repeated")){
                Toast.makeText(getActivity(), "این کاربر هم‌اکنون عضو گروه است", Toast.LENGTH_SHORT).show();
                dismiss();
            }
            else if (baseResponse.getStatus().getCode().equals("200")){
                Toast.makeText(getActivity(), "گروه با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                dismiss();
            }
            else
                Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
            dismiss();
        });

    }

    @OnClick(R.id.cancelBtn)
    void clickOnCancel(){
        dismiss();
    }
}