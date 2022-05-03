package pe.edu.upc.wallpapeer.utils;

import pe.edu.upc.wallpapeer.dtos.PinchEventResponse;

public class LastPinchEventResponse {
    private PinchEventResponse pinchEventResponse = null;

    private static LastPinchEventResponse INSTANCE;

    private LastPinchEventResponse() {
    }

    public static LastPinchEventResponse getInstance() {
        if(INSTANCE == null) {
            synchronized (LastPinchEventResponse.class)
            {
                if(INSTANCE==null)
                {
                    INSTANCE = new LastPinchEventResponse();
                }
            }
        }
        return INSTANCE;
    }

    public PinchEventResponse getPinchEventResponse() {
        return pinchEventResponse;
    }

    public void setPinchEventResponse(PinchEventResponse pinchEventResponse) {
        this.pinchEventResponse = pinchEventResponse;
    }
}
