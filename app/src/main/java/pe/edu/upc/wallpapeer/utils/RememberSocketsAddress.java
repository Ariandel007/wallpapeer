package pe.edu.upc.wallpapeer.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import pe.edu.upc.wallpapeer.connections.ClientTask;

public class RememberSocketsAddress {
    private static RememberSocketsAddress INSTANCE;

    private HashSet<String> socketsAddress = new HashSet<>();
    private List<ClientTask> clientTasks = new ArrayList<>();

    public RememberSocketsAddress() {
    }

    public static RememberSocketsAddress getInstance() {
        if(INSTANCE == null) {
            synchronized (RememberSocketsAddress.class)
            {
                if(INSTANCE==null)
                {
                    INSTANCE = new RememberSocketsAddress();
                }

            }
        }

        return INSTANCE;
    }

    public HashSet<String> getSocketsAddress() {
        return socketsAddress;
    }

    public void setSocketsAddress(HashSet<String> socketsAddress) {
        this.socketsAddress = socketsAddress;
    }

    public void addAddressToSet(String ipSocket) {
//        String ipSocket = fullAddress.substring(0,fullAddress.indexOf(":")-1);
        socketsAddress.add(ipSocket);
    }

    public List<ClientTask> getClientTasks() {
        return clientTasks;
    }

    public void setClientTasks(List<ClientTask> clientTasks) {
        this.clientTasks = clientTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RememberSocketsAddress that = (RememberSocketsAddress) o;
        return Objects.equals(socketsAddress, that.socketsAddress) && Objects.equals(clientTasks, that.clientTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socketsAddress, clientTasks);
    }
}
