package rayan.rayanapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.R;

public class ProgressDialog extends Dialog {

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.cancel)
    public TextView cancel;
    @BindView(R.id.parent)
    RelativeLayout parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_progress_2);
        setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);
        text.setText("لطفا کمی صبرکنید...");
    }

}
