package pe.edu.upc.wallpapeer.dtos;

import java.util.Date;

public class EngagePinchEvent {
    private String a1_eventCode;
    private String direction;
    private String deviceName;
    private String macAddress;
    private Float posPinchX;
    private Float posPinchY;
    private Float widthScreenPinch;
    private Float heightScreenPinch;
    private Date datePinch;
    private String originalSender;
    private String trueTargetDevice;

    public EngagePinchEvent() {}

    public EngagePinchEvent(String a1_eventCode, String direction, String deviceName, String macAddress, Float posPinchX, Float posPinchY, Float widthScreenPinch, Float heightScreenPinch, Date datePinch) {
        this.a1_eventCode = a1_eventCode;
        this.direction = direction;
        this.deviceName = deviceName;
        this.macAddress = macAddress;
        this.posPinchX = posPinchX;
        this.posPinchY = posPinchY;
        this.widthScreenPinch = widthScreenPinch;
        this.heightScreenPinch = heightScreenPinch;
        this.datePinch = datePinch;
    }

    public String getEventCode() {
        return a1_eventCode;
    }

    public void setEventCode(String eventCode) {
        this.a1_eventCode = eventCode;
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

    public Float getPosPinchX() {
        return posPinchX;
    }

    public void setPosPinchX(Float posPinchX) {
        this.posPinchX = posPinchX;
    }

    public Float getPosPinchY() {
        return posPinchY;
    }

    public void setPosPinchY(Float posPinchY) {
        this.posPinchY = posPinchY;
    }

    public Float getWidthScreenPinch() {
        return widthScreenPinch;
    }

    public void setWidthScreenPinch(Float widthScreenPinch) {
        this.widthScreenPinch = widthScreenPinch;
    }

    public Float getHeightScreenPinch() {
        return heightScreenPinch;
    }

    public void setHeightScreenPinch(Float heightScreenPinch) {
        this.heightScreenPinch = heightScreenPinch;
    }

    public Date getDatePinch() {
        return datePinch;
    }

    public void setDatePinch(Date datePinch) {
        this.datePinch = datePinch;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }

    public String getTrueTargetDevice() {
        return trueTargetDevice;
    }

    public void setTrueTargetDevice(String trueTargetDevice) {
        this.trueTargetDevice = trueTargetDevice;
    }
}
