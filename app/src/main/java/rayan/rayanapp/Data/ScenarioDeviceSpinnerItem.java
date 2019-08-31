package rayan.rayanapp.Data;

public class ScenarioDeviceSpinnerItem {
    private String id,name;

    public ScenarioDeviceSpinnerItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
