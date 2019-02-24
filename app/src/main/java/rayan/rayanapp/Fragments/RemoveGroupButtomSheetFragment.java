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
import rayan.rayanapp.ViewModels.GroupsListFragmentViewModel;

public class RemoveGroupButtomSheetFragment extends BottomSheetDialogFragment {
    private String itemId;
    private GroupsListFragmentViewModel groupsListFragmentViewModel;
    @BindView(R.id.submitBtn)
    AppCompatButton submitBtn;
    @BindView(R.id.cancelBtn)
    AppCompatButton cancelBtn;
    @BindView(R.id.bottomSheetText)
    AppCompatTextView bottomSheetText;

    public static RemoveGroupButtomSheetFragment newInstance(String itemIdd) {
        final RemoveGroupButtomSheetFragment fragment = new RemoveGroupButtomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("ITEMID", itemIdd);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_action_bottomsheet, container,
                false);
        ButterKnife.bind(this,view);
        submitBtn.setText("حذف گروه");
        cancelBtn.setText("بازگشت");
        bottomSheetText.setText("آیا مایل به حذف این گروه هستید؟");
        groupsListFragmentViewModel = ViewModelProviders.of(this).get(GroupsListFragmentViewModel.class);
        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemId=getArguments().getString("ITEMID");
    }

    @OnClick(R.id.submitBtn)
    void clickOnSubmit(){
        groupsListFragmentViewModel.deleteGroup(itemId).observe(this, baseResponse -> {
            if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
                Toast.makeText(getActivity(), "این گروه وجود ندارد", Toast.LENGTH_SHORT).show();
                dismiss();
            }
            else if (baseResponse.getStatus().getCode().equals("403") && baseResponse.getData().getMessage().equals("Repeated")){
                Toast.makeText(getActivity(), "شما قادر به حذف این گروه نیستید", Toast.LENGTH_SHORT).show();
                dismiss();
            }
            else if (baseResponse.getStatus().getCode().equals("204")){
                Toast.makeText(getActivity(), "گروه با موفقیت حذف شد", Toast.LENGTH_SHORT).show();
                groupsListFragmentViewModel.getGroups();
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


