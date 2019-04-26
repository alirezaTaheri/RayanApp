package rayan.rayanapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;

public class YesNoDialog extends Dialog {

    YesNoDialogListener listener;
    public YesNoDialog(@NonNull Context context, int themeResId, YesNoDialogListener listener) {
        super(context, themeResId);
        this.listener = listener;
    }

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.cancel)
    public TextView cancel;
    @BindView(R.id.ok)
    public TextView ok;
    @BindView(R.id.parent)
    RelativeLayout parent;
    private View.OnClickListener yesOnclick, noOnclick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.setCancelable(false);
        this.setContentView(R.layout.dialog_yes_no);
        ButterKnife.bind(this);
        text.setText("دسترسی شما تایید نشد"+"\nآیا مایل به تلاش دوباره هستید؟");
        ok.setOnClickListener(v -> listener.onYesClicked(this));
        cancel.setOnClickListener(v -> listener.onNoClicked(this));
    }

}
