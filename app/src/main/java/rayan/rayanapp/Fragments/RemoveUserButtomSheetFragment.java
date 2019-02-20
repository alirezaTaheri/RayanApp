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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.EditGroupFragmentViewModel;

public class RemoveUserButtomSheetFragment extends BottomSheetDialogFragment {
    private String id;
    private String groupId;
    private EditGroupFragmentViewModel editGroupFragmentViewModel;
    @BindView(R.id.submitBtn)
    AppCompatButton submitBtn;
    @BindView(R.id.cancelBtn)
    AppCompatButton cancelBtn;
    @BindView(R.id.bottomSheetText)
    AppCompatTextView bottomSheetText;

    public static RemoveUserButtomSheetFragment newInstance(String idd,String groupIdd ) {
        final RemoveUserButtomSheetFragment fragment = new RemoveUserButtomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("ID", idd);
        args.putString("GROUPID",groupIdd);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_action_bottomsheet, container,
                false);
        ButterKnife.bind(this,view);
        submitBtn.setText("حذف کاربر");
        cancelBtn.setText("بازگشت");
        bottomSheetText.setText("آیا مایل به حذف کاربر هستید؟");
        editGroupFragmentViewModel = ViewModelProviders.of(this).get(EditGroupFragmentViewModel.class);
        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id=getArguments().getString("ID");
        groupId=getArguments().getString("GROUPID");
    }

    @OnClick(R.id.submitBtn)
    void clickOnSubmit(){
        editGroupFragmentViewModel.deleteUser(id, groupId).observe(this, baseResponse -> {
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
                Toast.makeText(getActivity(), "این گروه وجود ندارد", Toast.LENGTH_SHORT).show();
                dismiss();
            }
            else if (baseResponse.getStatus().getCode().equals("403") && baseResponse.getData().getMessage().equals("Repeated")){
                Toast.makeText(getActivity(), "شما قادر به حذف این کاربر نیستید", Toast.LENGTH_SHORT).show();
                dismiss();
            }
            else if (baseResponse.getStatus().getCode().equals("200")){
                Toast.makeText(getActivity(), "کاربر با موفقیت حذف شد", Toast.LENGTH_SHORT).show();
                editGroupFragmentViewModel.getGroups();
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