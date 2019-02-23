package rayan.rayanapp.Util;

import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class SnackBarSetup {
    public static void snackBarSetup(View view, String text){
        Snackbar mSnackBar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout)mSnackBar.getView();
        layout.setPadding(0, 0, 0, 0);
        layout.setMinimumWidth(view.getWidth());
        TextView mainTextView = (mSnackBar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mSnackBar.show();
    }
}
