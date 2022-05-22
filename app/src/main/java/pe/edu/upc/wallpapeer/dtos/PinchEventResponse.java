package pe.edu.upc.wallpapeer.dtos;

import java.util.Date;
import java.util.List;

import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Device;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Project;

public class PinchEventResponse {
    private String a1_eventCode;
    private String direction;
    private String deviceName;
    private String macAddress;
    private Project project;
    private Device device;
    private Canva canva;
    private List<Element> elements;
    private String originalSender;

    public PinchEventResponse(String a1_eventCode, String direction, String deviceName, String macAddress, Project project, Device device, Canva canva, List<Element> elements) {
        this.a1_eventCode = a1_eventCode;
        this.direction = direction;
        this.deviceName = deviceName;
        this.macAddress = macAddress;
        this.project = project;
        this.device = device;
        this.canva = canva;
        this.elements = elements;
    }
    public PinchEventResponse(){}

    public String getA1_eventCode() {
        return a1_eventCode;
    }

    public void setA1_eventCode(String a1_eventCode) {
        this.a1_eventCode = a1_eventCode;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Canva getCanva() {
        return canva;
    }

    public void setCanva(Canva canva) {
        this.canva = canva;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }
}
