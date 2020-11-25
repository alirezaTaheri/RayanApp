package rayan.rayanapp.Adapters.recyclerView;

import android.content.Context;
import androidx.recyclerview.widget.DiffUtil;
import android.view.ViewGroup;

import java.util.List;

import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Listeners.OnScenarioClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewHolders.ScenarioViewHolder;

public class ScenariosRecyclerViewAdapter extends GenericRecyclerViewAdapter<Scenario,OnScenarioClicked<Scenario>, ScenarioViewHolder>  {


    public ScenariosRecyclerViewAdapter(Context context, List<Scenario> groups) {
        super(context);
        this.items = groups;
    }

    @Override
    public ScenarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScenarioViewHolder(inflate(R.layout.item_scenario, parent));
    }
}
