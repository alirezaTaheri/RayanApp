package rayan.rayanapp.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;

public class PrivacyPolicyDialog extends AlertDialog {

    private String body;
    private String textFileName = "privacy_policy.html";
    private Context context;
    public PrivacyPolicyDialog(@NonNull Context context, String body) {
        super(context);
        this.body = body;
        this.context = context;
    }

    @BindView(R.id.text)
    WebView text;
    @BindView(R.id.title)
    public TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.dialog_privacy_policy, null);
        setView(content);
        ButterKnife.bind(this, content);
        setCanceledOnTouchOutside(false);
        text.loadUrl("file:///android_asset/" + textFileName);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        text.getLayoutParams().height = (int) (height*0.65);
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(context, "لطفا شرایط و سیاست خریم خصوصی را قبول کنید.", Toast.LENGTH_LONG).show();
                ((Activity)context).onBackPressed();
            }
        });
        super.onCreate(savedInstanceState);

    }

    @OnClick(R.id.confirm)
    public void confirmClicked(){
        RayanApplication.getPref().setPrivacyPolicyAccepted();
        dismiss();
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
