package rayan.rayanapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.R;

public class YesNoButtomSheetFragment extends BottomSheetDialogFragment {
    private String submitTxt;
    private String cancelTxt;
    private String sheetTxt;
    private String groupId;
    private String userId;
   private String tag;
   private ArrayList<String> numbers=new ArrayList<>();
   private ArrayList<String> codelist=new ArrayList<>();
   private String cmd;
   private String deviceIp;
   private String name;
    @BindView(R.id.submitBtn)
    AppCompatButton submitBtn;
    @BindView(R.id.cancelBtn)
    AppCompatButton cancelBtn;
    @BindView(R.id.bottomSheetText)
    AppCompatTextView bottomSheetText;

    public static YesNoButtomSheetFragment groupListInstance(String tag, String submitTxt, String cancelTxt, String sheetTxt, String groupId) {
        final YesNoButtomSheetFragment fragment = new YesNoButtomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("TAG",tag);
        args.putString("SHEETTXT",sheetTxt);
        args.putString("SUBMITTXT",submitTxt);
        args.putString("CANCELTXT",cancelTxt);
        args.putString("GROUPID",groupId);
        fragment.setArguments(args);
        return fragment;
    }
    public static YesNoButtomSheetFragment createGroupInstance(String tag, String submitTxt, String cancelTxt, String sheetTxt, String name, ArrayList<String> numbers) {
        final YesNoButtomSheetFragment fragment = new YesNoButtomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("TAG",tag);
        args.putString("SHEETTXT",sheetTxt);
        args.putString("SUBMITTXT",submitTxt);
        args.putString("CANCELTXT",cancelTxt);
        args.putString("NAME",name);
        args.putStringArrayList("NUMBERS",numbers);

        fragment.setArguments(args);
        return fragment;
    }
    public static YesNoButtomSheetFragment editGroupInstance(String tag, String submitTxt, String cancelTxt, String sheetTxt, String groupId,String userId) {
        final YesNoButtomSheetFragment fragment = new YesNoButtomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("TAG",tag);
        args.putString("SHEETTXT",sheetTxt);
        args.putString("SUBMITTXT",submitTxt);
        args.putString("CANCELTXT",cancelTxt);
        args.putString("GROUPID",groupId);
        args.putString("USERID",userId);
        fragment.setArguments(args);
        return fragment;
    }
    public static YesNoButtomSheetFragment editDeviceInstance(String tag, String submitTxt, String cancelTxt, String sheetTxt, ArrayList<String> codelist, String cmd, String deviceIp) {
        final YesNoButtomSheetFragment fragment = new YesNoButtomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("TAG",tag);
        args.putString("SHEETTXT",sheetTxt);
        args.putString("SUBMITTXT",submitTxt);
        args.putString("CANCELTXT",cancelTxt);
        args.putString("COMMAND",cmd);
        args.putString("DEVICEIP",deviceIp);
       args.putStringArrayList("CODELIST",codelist);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_action_bottomsheet, container,
                false);
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
        switch (tag){
            case "GroupsListFragment":
                groupId=getArguments().getString("GROUPID");
                break;
            case "CreateGroupFragment":
                name=getArguments().getString("NAME");
                numbers=getArguments().getStringArrayList("NUMBERS");
                break;
            case "EditGroupFragment1":
                groupId=getArguments().getString("GROUPID");
                userId=getArguments().getString("USERID");
                break;
            case "EditGroupFragment2":
                groupId=getArguments().getString("GROUPID");
                userId=getArguments().getString("USERID");
                break;
            case "EditDeviceFragment":
                codelist=getArguments().getStringArrayList("CODELIST");
                cmd=getArguments().getString("COMMAND");
                deviceIp=getArguments().getString("DEVICEIP");
                break;
            default:
                break;

        }

    }

    @OnClick(R.id.submitBtn)
    void clickOnSubmit(){
        switch (tag){
            case "GroupsListFragment":
                GroupsListFragment.getInstance().clickOnSubmit(groupId);
                dismiss();
                break;
            case "CreateGroupFragment":
                CreateGroupFragment.getInstance().clickOnSubmit(name,numbers);
                dismiss();
                break;
            case "EditGroupFragment1":
               EditGroupFragment.getInstance().clickOnRemoveUserSubmit(userId,groupId);
                dismiss();
                break;
            case "EditGroupFragment2":
                EditGroupFragment.getInstance().clickOnRemoveAdminSubmit(userId,groupId);
                dismiss();
                break;
            case "EditDeviceFragment":
                EditDeviceFragment.getInstance().clickOnDeviceUpdateSubmit(codelist,cmd,deviceIp);
                dismiss();
                break;
            default:
                break;
        }

        }

    @OnClick(R.id.cancelBtn)
    void clickOnCancel(){
        dismiss();
    }
}
