package rayan.rayanapp.Listeners;

public interface OnScenarioClicked<T> extends BaseRecyclerListener{
    void onScenarioClicked(T item, int position);
    void onExecuteClicked(T item, int position);
}
