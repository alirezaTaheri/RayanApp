package rayan.rayanapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;

public class YesNoDialog extends AlertDialog {

    YesNoDialogListener listener;
    String message;
    Bundle data;
    public YesNoDialog(@NonNull Context context, YesNoDialogListener listener, String message, Bundle data) {
        super(context);
        this.listener = listener;
        this.message = message;
        this.data = data;
    }

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.cancel)
    public TextView cancel;
    @BindView(R.id.ok)
    public TextView ok;
    @BindView(R.id.parent)
    RelativeLayout parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.setCancelable(false);
        View content = LayoutInflater.from(getContext()).inflate(R.layout.dialog_yes_no, null);
        setView(content);
        ButterKnife.bind(this, content);
        text.setText(message);
        ok.setOnClickListener(v -> listener.onYesClicked(this, data));
        cancel.setOnClickListener(v -> listener.onNoClicked(this, data));
        super.onCreate(savedInstanceState);
    }

}
