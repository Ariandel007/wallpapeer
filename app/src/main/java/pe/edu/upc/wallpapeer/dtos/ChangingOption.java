package pe.edu.upc.wallpapeer.dtos;

public class ChangingOption {
    private String a1_eventCode;
    private String deviceName;
    private String targetDeviceName;
    private String macAddress;
    private int selectedOption;
    private int subOption;
    private String textToInsert = "";
    private Integer color;
    private String originalSender;

    public String getTextToInsert() {
        return textToInsert;
    }

    public void setTextToInsert(String textToInsert) {
        this.textToInsert = textToInsert;
    }

    public ChangingOption(String a1_eventCode, String deviceName, String targetDeviceName, String macAddress, int selectedOption, int subOption, Integer color) {
        this.a1_eventCode = a1_eventCode;
        this.deviceName = deviceName;
        this.targetDeviceName = targetDeviceName;
        this.macAddress = macAddress;
        this.selectedOption = selectedOption;
        this.subOption = subOption;
        this.color = color;
    }

    public ChangingOption() {
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

    public String getTargetDeviceName() {
        return targetDeviceName;
    }

    public void setTargetDeviceName(String targetDeviceName) {
        this.targetDeviceName = targetDeviceName;
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

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }
}
