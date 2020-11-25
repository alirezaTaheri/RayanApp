package rayan.rayanapp.Dialogs;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.R;

public class ScenarioPopupMenuDialog extends AlertDialog {

    ScenarioMenuListener listener;
    int id;
    public ScenarioPopupMenuDialog(@NonNull Context context, ScenarioMenuListener listener, int id) {
        super(context);
        this.listener = listener;
        this.id = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.scenario_popup_menu_dialog, null);
        setView(content);
        ButterKnife.bind(this, content);
        this.setCancelable(true);
        super.onCreate(savedInstanceState);
    }
    @OnClick(R.id.deleteContainer)
    public void onDeleteClicked(){
        listener.onDeleteClicked(id);
    }

    @OnClick(R.id.editContainer)
    public void onEditClicked(){
        listener.onEditClicked(id);
    }

    public interface ScenarioMenuListener {
        void onDeleteClicked(int id);
        void onEditClicked(int id);
    }
}
