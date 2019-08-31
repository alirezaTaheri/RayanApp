package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.Listeners.OnScenarioClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;

public class ScenarioViewHolder extends BaseViewHolder<Scenario, OnScenarioClicked<Scenario>> {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.executeButton)
    Button executeButton;
    @BindView(R.id.container)
    RelativeLayout container;
    public ScenarioViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBind(Scenario item, @Nullable OnScenarioClicked<Scenario> listener) {
        name.setText(item.getName());
        Log.e("OOOOOOOO", "" + item);
        executeButton.setOnClickListener(v -> {
            Log.e("OOOOOOOO", "clicked" + item);
            listener.onExecuteClicked(item, getAdapterPosition());
        });
        container.setOnClickListener(v -> listener.onScenarioClicked(item, getAdapterPosition()));
    }
}
