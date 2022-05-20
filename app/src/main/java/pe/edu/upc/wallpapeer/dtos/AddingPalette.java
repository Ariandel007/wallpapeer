package pe.edu.upc.wallpapeer.dtos;

public class AddingPalette {
    private String a1_eventCode;
    private String deviceName;
    private String targetDeviceName;
    private String macAddress;
    private int selectedOption;
    private int subOption;
    private String originalSender;

    public AddingPalette(String a1_eventCode, String deviceName, String targetDeviceName, String macAddress, int selectedOption, int subOption) {
        this.a1_eventCode = a1_eventCode;
        this.deviceName = deviceName;
        this.targetDeviceName = targetDeviceName;
        this.macAddress = macAddress;
        this.selectedOption = selectedOption;
        this.subOption = subOption;
    }

    public String getTargetDeviceName() {
        return targetDeviceName;
    }

    public void setTargetDeviceName(String targetDeviceName) {
        this.targetDeviceName = targetDeviceName;
    }

    public AddingPalette() {
    }

    public String getA1_eventCode() {
        return a1_eventCode;
    }

    public void setA1_eventCode(String a1_eventCode) {
        this.a1_eventCode = a1_eventCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public int getSubOption() {
        return subOption;
    }

    public void setSubOption(int subOption) {
        this.subOption = subOption;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }
}
