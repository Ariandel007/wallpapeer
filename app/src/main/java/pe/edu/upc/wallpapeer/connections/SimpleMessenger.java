package pe.edu.upc.wallpapeer.connections;

public abstract class SimpleMessenger {
    public abstract void send(String text, boolean isMessage);

    public abstract void DestroySocket();
}
