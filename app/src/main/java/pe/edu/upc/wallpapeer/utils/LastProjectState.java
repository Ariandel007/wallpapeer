package pe.edu.upc.wallpapeer.utils;

public class LastProjectState {
    private static LastProjectState INSTANCE;

    private String projectId;
    private String canvaId;
    private String deviceName;


    public LastProjectState() {
    }

    public static LastProjectState getInstance() {
        if(INSTANCE == null) {
//            INSTANCE = new MyLastPinch();
            //remover overhead, asi solo la primera vez se bloqueara
            //IMPORTANTE PARA THREAD SAFE
            synchronized (LastProjectState.class)
            {
                if(INSTANCE==null)
                {
                    // Si la instancia es nula inicializar
                    INSTANCE = new LastProjectState();
                }

            }
        }

        return INSTANCE;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCanvaId() {
        return canvaId;
    }

    public void setCanvaId(String canvaId) {
        this.canvaId = canvaId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
