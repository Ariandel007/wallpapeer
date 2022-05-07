package pe.edu.upc.wallpapeer.dtos;

import pe.edu.upc.wallpapeer.entities.Project;

public class AcceptingPalette {
    private String a1_eventCode;
    private String linkedDevice;
    private String linkedIdDevice;
    private String paletteDeviceName;
    private String macAddress;
    private Project project;

    public String getPaletteDeviceName() {
        return paletteDeviceName;
    }

    public void setPaletteDeviceName(String paletteDeviceName) {
        this.paletteDeviceName = paletteDeviceName;
    }

    public String getLinkedIdDevice() {
        return linkedIdDevice;
    }

    public void setLinkedIdDevice(String linkedIdDevice) {
        this.linkedIdDevice = linkedIdDevice;
    }

    public AcceptingPalette(String a1_eventCode, String linkedDevice, String linkedIdDevice, String macAddress, Project project) {
        this.a1_eventCode = a1_eventCode;
        this.linkedDevice = linkedDevice;
        this.linkedIdDevice = linkedIdDevice;
        this.macAddress = macAddress;
        this.project = project;
    }

    public AcceptingPalette() {
    }

    public String getA1_eventCode() {
        return a1_eventCode;
    }

    public void setA1_eventCode(String a1_eventCode) {
        this.a1_eventCode = a1_eventCode;
    }

    public String getLinkedDevice() {
        return linkedDevice;
    }

    public void setLinkedDevice(String linkedDevice) {
        this.linkedDevice = linkedDevice;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
