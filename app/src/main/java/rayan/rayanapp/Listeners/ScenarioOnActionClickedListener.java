package rayan.rayanapp.Listeners;

public interface ScenarioOnActionClickedListener<T> extends BaseRecyclerListener{
    void pin1Clicked(T item, int position);
    void pin2Clicked(T item, int position);
    void removeClicked(T item, int position);
}
