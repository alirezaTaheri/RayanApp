package rayan.rayanapp.Data;

public class DeviceStatus {
    private String chipId;
    private boolean pin1Enabled, pin2Enabled;
    private int pin1StripWidth,pin2StripWidth;

    public String getChipId() {
        return chipId;
    }

    public void setChipId(String chipId) {
        this.chipId = chipId;
    }

    public boolean isPin1Enabled() {
        return pin1Enabled;
    }

    public void setPin1Enabled(boolean pin1Enabled) {
        this.pin1Enabled = pin1Enabled;
    }

    public boolean isPin2Enabled() {
        return pin2Enabled;
    }

    public void setPin2Enabled(boolean pin2Enabled) {
        this.pin2Enabled = pin2Enabled;
    }

    public int getPin1StripWidth() {
        return pin1StripWidth;
    }

    public void setPin1StripWidth(int pin1StripWidth) {
        this.pin1StripWidth = pin1StripWidth;
    }

    public int getPin2StripWidth() {
        return pin2StripWidth;
    }

    public void setPin2StripWidth(int pin2StripWidth) {
        this.pin2StripWidth = pin2StripWidth;
    }
}
