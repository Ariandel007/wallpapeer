package pe.edu.upc.wallpapeer.utils;

import java.util.Date;

import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Device;
import pe.edu.upc.wallpapeer.entities.Project;

public class MyLastPinch {

    private static MyLastPinch INSTANCE;

    private String projectId = null;
    private String canvaId = null;
    private Date date = null;
    private float pinchX = 0.0f;
    private float pinchY = 0.0f;
    private String direction = null;
    private Canva canva = null;
    private Project project = null;
//    private Device device = null;

    private MyLastPinch() {
    }

    public static MyLastPinch getInstance() {
        if(INSTANCE == null) {
//            INSTANCE = new MyLastPinch();
            //remover overhead, asi solo la primera vez se bloqueara
            //IMPORTANTE PARA THREAD SAFE
            synchronized (MyLastPinch.class)
            {
                if(INSTANCE==null)
                {
                    // Si la instancia es nula inicializar
                    INSTANCE = new MyLastPinch();
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getPinchX() {
        return pinchX;
    }

    public void setPinchX(float pinchX) {
        this.pinchX = pinchX;
    }

    public float getPinchY() {
        return pinchY;
    }

    public void setPinchY(float pinchY) {
        this.pinchY = pinchY;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Canva getCanva() {
        return canva;
    }

    public void setCanva(Canva canva) {
        this.canva = canva;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

//    public Device getDevice() {
//        return device;
//    }
//
//    public void setDevice(Device device) {
//        this.device = device;
//    }
}
