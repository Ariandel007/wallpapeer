package pe.edu.upc.wallpapeer.utils;

public class QrMessage {
    private String ownername;
    private String myName;

    public QrMessage(String ownername, String myName) {
        this.ownername = ownername;
        this.myName = myName;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }
}
