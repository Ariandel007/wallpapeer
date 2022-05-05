package pe.edu.upc.wallpapeer.connections;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.LocalDevice;
import pe.edu.upc.wallpapeer.dtos.EngagePinchEvent;
import pe.edu.upc.wallpapeer.dtos.NewElementInserted;
import pe.edu.upc.wallpapeer.dtos.PinchEventResponse;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Device;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
import pe.edu.upc.wallpapeer.utils.CodeEvent;
import pe.edu.upc.wallpapeer.utils.JsonConverter;
import pe.edu.upc.wallpapeer.utils.LastProjectState;
import pe.edu.upc.wallpapeer.utils.MyLastPinch;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;

public class Server extends IMessenger {
    private Socket socket;
    private ServerSocket serverSocket;
    private String peerName;
    private MutableLiveData<Boolean> isConnected;
    private ConnectionPeerToPeerViewModel model;

    public Server(ConnectionPeerToPeerViewModel model, MutableLiveData<Boolean> isConnected) {
        this.model = model;
        this.isConnected = isConnected;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8888);
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Tras conectar, enviamos el nombre de nuestro dispositivo como primer mensaje
        send(LocalDevice.getInstance().getDevice().deviceName, false);

        // Ya he leído el nombre del compañero
        boolean isAddresseeSet = false;

        while (socket != null) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                String messageText = (String) inputStream.readObject();
                if (messageText != null) {
                    if (isAddresseeSet) {
                        String eventCode = messageText.substring(17,22);
                        deserializeBasedOnEventCode(eventCode,messageText);
                        //EJEMPLO, Tomar con pinzas uwu
//                        String obtenerEvent = messageText.substring(7,14);
//                        switch (obtenerEvent) {
//                            case CodeEvent.PINCH_EVENT:
//                                //haz cosas
//                                break;
//                        }
                        // Si llega un nuevo mensaje lo guardamos directamente en la base de datos
                        // Es el objeto de esta base de datos el que es activado por la actividad correspondiente al objeto leído
                        // Ya no tenemos que enviar a Active
                        Date c = Calendar.getInstance().getTime();
//                        MessageEntity message = new MessageEntity(messageText, c, peerName, false);
//                        MessageRepository.getInstance().insert(message);
                    } else {
                        // El primer mensaje que leemos es el nombre del par y luego chateamos.
                        isAddresseeSet = true;
                        peerName = messageText;
                        model.setAddressee(messageText);
                        isConnected.postValue(true);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // Si el socket está cerrado desde el otro lado, también cerramos la ventana de chat.
                model.closeChat();
            }
        }

    }

    @Override
    public void send(final String text, final boolean isMessage) {

        new Thread() {
            @Override
            public void run() {
                if (socket == null) return;
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(text);
                    outputStream.flush();
                    if (isMessage) {
                        // Si no es el primer mensaje, es decir no enviamos el nombre
                        // Luego tenemos que almacenarlo en la base de datos también
                        Date c = Calendar.getInstance().getTime();
//                        MessageEntity message = new MessageEntity(text, c, peerName, true);
//                        MessageRepository.getInstance().insert(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public void DestroySocket() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("CheckResult")
    private void deserializeBasedOnEventCode(String eventCode, String jsonMessage) {

        switch(eventCode) {
            case CodeEvent.PINCH_EVENT:
                Log.i("EVENT", "PINCH_EVENT");
                if(MyLastPinch.getInstance().getProjectId() != null && MyLastPinch.getInstance().getCanva() != null && MyLastPinch.getInstance().getDate() != null) {
                    EngagePinchEvent engagePinchEvent = JsonConverter.getGson().fromJson(jsonMessage, EngagePinchEvent.class);
                    //TODO: Comprobar si entra en el rango de tiempo

                    if(MyLastPinch.getInstance().getDirection().equals("RIGHT") && engagePinchEvent.getDirection().equals("LEFT")) {
                        float posXnewCanva = MyLastPinch.getInstance().getPinchX() + MyLastPinch.getInstance().getCanva().getPosX();
                        float posYnewCanva = MyLastPinch.getInstance().getPinchY() + MyLastPinch.getInstance().getCanva().getPosY() - engagePinchEvent.getPosPinchY();

                        Device newDevice = new Device();
                        newDevice.setId(UUID.randomUUID().toString());
                        newDevice.setWidthScreen(engagePinchEvent.getWidthScreenPinch());
                        newDevice.setHeightScreen(engagePinchEvent.getHeightScreenPinch());
                        newDevice.setDeviceName(engagePinchEvent.getDeviceName());
                        newDevice.setMacAddress("");
                        newDevice.setId_project(MyLastPinch.getInstance().getProjectId());


                        Canva newCanva = new Canva();
                        newCanva.setId(UUID.randomUUID().toString());
                        newCanva.setMain(false);
                        newCanva.setHeightCanvas(engagePinchEvent.getHeightScreenPinch());
                        newCanva.setWidthCanvas(engagePinchEvent.getWidthScreenPinch());
                        newCanva.setPosX(posXnewCanva);
                        newCanva.setPosY(posYnewCanva);
                        newCanva.setId_device(newDevice.getId());

                        PinchEventResponse pinchEventResponse = new PinchEventResponse();
                        pinchEventResponse.setA1_eventCode(CodeEvent.PINCH_EVENT_RESPONSE);
                        pinchEventResponse.setDirection("");
                        pinchEventResponse.setDeviceName(engagePinchEvent.getDeviceName());
                        pinchEventResponse.setMacAddress("");
                        pinchEventResponse.setProject(MyLastPinch.getInstance().getProject());
                        pinchEventResponse.setDevice(newDevice);
                        pinchEventResponse.setCanva(newCanva);

                        AppDatabase.getInstance().elementDAO().getAllByProject(MyLastPinch.getInstance().getProjectId())
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                elements -> {
                                    pinchEventResponse.setElements(elements);
                                    AppDatabase.getInstance().deviceDAO().getDeviceByDeviceNameAndProject(newDevice.getDeviceName(), newDevice.getId_project())
                                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                            device -> {
                                                device.setWidthScreen(engagePinchEvent.getWidthScreenPinch());
                                                device.setHeightScreen(engagePinchEvent.getHeightScreenPinch());
                                                device.setDeviceName(engagePinchEvent.getDeviceName());
                                                device.setMacAddress("");
                                                pinchEventResponse.setDevice(device);
                                                pinchEventResponse.getCanva().setId_device(device.getId());
                                                AppDatabase.getInstance().deviceDAO().update(device)
                                                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                                                    //Canva
                                                    AppDatabase.getInstance().canvaDAO().getCanvaByIdDevice(device.getId())
                                                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((canva)->{
                                                        canva.setMain(false);
                                                        canva.setHeightCanvas(engagePinchEvent.getHeightScreenPinch());
                                                        canva.setWidthCanvas(engagePinchEvent.getWidthScreenPinch());
                                                        canva.setPosX(posXnewCanva);
                                                        canva.setPosY(posYnewCanva);

                                                        canva.setMod_date(new Date().getTime());

                                                        pinchEventResponse.setCanva(canva);

                                                        //Agregar a Last Pinch Response
//                                            LastPinchEventResponse.getInstance().setPinchEventResponse(pinchEventResponse);
                                                        send(JsonConverter.getGson().toJson(pinchEventResponse), true);

                                                        AppDatabase.getInstance().canvaDAO().update(canva)
                                                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                                            Log.i("Actualizacion Canvas","Canvas actualizado");
                                                        });
                                                    }, throwable3 -> {
                                                        //QUERY VACIO
                                                        Log.e("Error",throwable3.getMessage());
                                                        //SIGNIFICA QUE ES NUEVO
                                                        //Agregar a Last Pinch Response
                                                        pinchEventResponse.getCanva().setMod_date(new Date().getTime());
//                                        LastPinchEventResponse.getInstance().setPinchEventResponse(pinchEventResponse);
                                                        send(JsonConverter.getGson().toJson(pinchEventResponse), true);

                                                        AppDatabase.getInstance().canvaDAO().insert(pinchEventResponse.getCanva())
                                                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                                            Log.i("Creacion Canvas","Canvas creado");
                                                        });
                                                    });
                                                },throwable -> {
                                                    Log.e("Error",throwable.getMessage());
                                                });
                                            },
                                            throwable -> {
                                                //QUERY VACIO
                                                Log.e("Error",throwable.getMessage());
                                                //SIGNIFICA QUE ES NUEVO
                                                AppDatabase.getInstance().deviceDAO().insert(pinchEventResponse.getDevice())
                                                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                                    //Agregar a Last Pinch Response
                                                    pinchEventResponse.getCanva().setMod_date(new Date().getTime());
//                                    LastPinchEventResponse.getInstance().setPinchEventResponse(pinchEventResponse);
                                                    send(JsonConverter.getGson().toJson(pinchEventResponse), true);

                                                    AppDatabase.getInstance().canvaDAO().insert(pinchEventResponse.getCanva())
                                                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                                        Log.i("Creacion Canvas","Canvas creado");
                                                    });
                                                }, throwable2 -> {
                                                    Log.e("Error",throwable2.getMessage());
                                                });
                                            });
                                }, throwable -> {
                                    Log.e("Error",throwable.getMessage());
                                });


                    }

                    if(MyLastPinch.getInstance().getDirection().equals("LEFT") && engagePinchEvent.getDirection().equals("RIGHT")) {
                        if(MyLastPinch.getInstance().getCanva().getPosX() == 0.0f || MyLastPinch.getInstance().getCanva().getPosX() == 0) {
                            //No hay izquierda
                            return;
                        }
                        //TODO: Comprobar si entra en el rango de tiempo

                        float posXnewCanva = MyLastPinch.getInstance().getPinchX() + MyLastPinch.getInstance().getCanva().getPosX() - engagePinchEvent.getWidthScreenPinch();
                        float posYnewCanva = MyLastPinch.getInstance().getPinchY() + MyLastPinch.getInstance().getCanva().getPosY() - engagePinchEvent.getPosPinchY();

                        Device newDevice = new Device();
                        newDevice.setId(UUID.randomUUID().toString());
                        newDevice.setWidthScreen(engagePinchEvent.getWidthScreenPinch());
                        newDevice.setHeightScreen(engagePinchEvent.getHeightScreenPinch());
                        newDevice.setDeviceName(engagePinchEvent.getDeviceName());
                        newDevice.setMacAddress("");
                        newDevice.setId_project(MyLastPinch.getInstance().getProjectId());


                        Canva newCanva = new Canva();
                        newCanva.setId(UUID.randomUUID().toString());
                        newCanva.setMain(false);
                        newCanva.setHeightCanvas(engagePinchEvent.getHeightScreenPinch());
                        newCanva.setWidthCanvas(engagePinchEvent.getWidthScreenPinch());
                        newCanva.setPosX(posXnewCanva);
                        newCanva.setPosY(posYnewCanva);
                        newCanva.setId_device(newDevice.getId());

                        PinchEventResponse pinchEventResponse = new PinchEventResponse();
                        pinchEventResponse.setA1_eventCode(CodeEvent.PINCH_EVENT_RESPONSE);
                        pinchEventResponse.setDirection("");
                        pinchEventResponse.setDeviceName(engagePinchEvent.getDeviceName());
                        pinchEventResponse.setMacAddress("");
                        pinchEventResponse.setProject(MyLastPinch.getInstance().getProject());
                        pinchEventResponse.setDevice(newDevice);
                        pinchEventResponse.setCanva(newCanva);

                        AppDatabase.getInstance().elementDAO().getAllByProject(MyLastPinch.getInstance().getProjectId())
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                elements -> {
                                    pinchEventResponse.setElements(elements);
                                    AppDatabase.getInstance().deviceDAO().getDeviceByDeviceNameAndProject(newDevice.getDeviceName(), newDevice.getId_project())
                                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                            device -> {
                                                device.setWidthScreen(engagePinchEvent.getWidthScreenPinch());
                                                device.setHeightScreen(engagePinchEvent.getHeightScreenPinch());
                                                device.setDeviceName(engagePinchEvent.getDeviceName());
                                                device.setMacAddress("");
                                                pinchEventResponse.setDevice(device);
                                                pinchEventResponse.getCanva().setId_device(device.getId());
                                                AppDatabase.getInstance().deviceDAO().update(device)
                                                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                                                    //Canva
                                                    AppDatabase.getInstance().canvaDAO().getCanvaByIdDevice(device.getId())
                                                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((canva)->{
                                                        canva.setMain(false);
                                                        canva.setHeightCanvas(engagePinchEvent.getHeightScreenPinch());
                                                        canva.setWidthCanvas(engagePinchEvent.getWidthScreenPinch());
                                                        canva.setPosX(posXnewCanva);
                                                        canva.setPosY(posYnewCanva);

                                                        canva.setMod_date(new Date().getTime());

                                                        pinchEventResponse.setCanva(canva);

                                                        //Agregar a Last Pinch Response
//                                            LastPinchEventResponse.getInstance().setPinchEventResponse(pinchEventResponse);
                                                        send(JsonConverter.getGson().toJson(pinchEventResponse), true);

                                                        AppDatabase.getInstance().canvaDAO().update(canva)
                                                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                                            Log.i("Actualizacion Canvas","Canvas actualizado");
                                                        });
                                                    }, throwable3 -> {
                                                        //QUERY VACIO
                                                        Log.e("Error",throwable3.getMessage());
                                                        //SIGNIFICA QUE ES NUEVO
                                                        //Agregar a Last Pinch Response
                                                        pinchEventResponse.getCanva().setMod_date(new Date().getTime());
//                                        LastPinchEventResponse.getInstance().setPinchEventResponse(pinchEventResponse);
                                                        send(JsonConverter.getGson().toJson(pinchEventResponse), true);

                                                        AppDatabase.getInstance().canvaDAO().insert(pinchEventResponse.getCanva())
                                                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                                            Log.i("Creacion Canvas","Canvas creado");
                                                        });
                                                    });
                                                },throwable -> {
                                                    Log.e("Error",throwable.getMessage());
                                                });
                                            },
                                            throwable -> {
                                                //QUERY VACIO
                                                Log.e("Error",throwable.getMessage());
                                                //SIGNIFICA QUE ES NUEVO
                                                AppDatabase.getInstance().deviceDAO().insert(pinchEventResponse.getDevice())
                                                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                                    //Agregar a Last Pinch Response
                                                    pinchEventResponse.getCanva().setMod_date(new Date().getTime());
//                                    LastPinchEventResponse.getInstance().setPinchEventResponse(pinchEventResponse);
                                                    send(JsonConverter.getGson().toJson(pinchEventResponse), true);

                                                    AppDatabase.getInstance().canvaDAO().insert(pinchEventResponse.getCanva())
                                                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                                        Log.i("Creacion Canvas","Canvas creado");
                                                    });
                                                }, throwable2 -> {
                                                    Log.e("Error",throwable2.getMessage());
                                                });
                                            });
                                }, throwable -> {
                                    Log.e("Error",throwable.getMessage());
                                });


                    }

                }

                break;
            case CodeEvent.ADDING_PALLETE_TO_DEVICE:
                Log.i("EVENT", "ADDING_PALLETE_TO_DEVICE");
                break;
            case CodeEvent.SELECT_OPTION_PALLETE:
                Log.i("EVENT", "SELECT_OPTION_PALLETE");
                break;
            case CodeEvent.INSERT_NEW_ELEMENT:
                Log.i("EVENT", "INSERT_NEW_ELEMENT");
                if(LastProjectState.getInstance().getProjectId() == null) {
                    return;
                }

                NewElementInserted newElementInserted = JsonConverter.getGson().fromJson(jsonMessage, NewElementInserted.class);
                if(LastProjectState.getInstance().getProjectId().equals(newElementInserted.getElement().getId_project())) {

                    AppDatabase.getInstance().elementDAO().insert(newElementInserted.getElement())
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                            subscribe(()->{
                                Log.i("Insercion por socket","Se isnerto cone exito");
                            });
                }

                break;
            case CodeEvent.PINCH_EVENT_RESPONSE:
                Log.i("EVENT", "PINCH_EVENT_RESPONSE");
                PinchEventResponse pinchEventResponse = JsonConverter.getGson().fromJson(jsonMessage, PinchEventResponse.class);
                //Comprobar si somos el target device
                if(!LastProjectState.getInstance().getDeviceName().equals(pinchEventResponse.getDeviceName())) {
                    return;
                }
                Project project = pinchEventResponse.getProject();
                Device device = pinchEventResponse.getDevice();
                Canva canva = pinchEventResponse.getCanva();
                List<Element> elements = pinchEventResponse.getElements();

                AppDatabase.getInstance().projectDAO().getProject(project.getId())
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((projectInBD)->{
                    AppDatabase.getInstance().deviceDAO().getDeviceByDeviceNameAndProject(device.getDeviceName(), device.getId_project())
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((deviceInBD)->{
                        //QUE HACER SI YA EXISTE EL DEVICE
                        AppDatabase.getInstance().elementDAO().insertMany(elements).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                            AppDatabase.getInstance().canvaDAO().insert(canva).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                LastProjectState.getInstance().setProjectId(project.getId());
                                LastProjectState.getInstance().setCanvaId(canva.getId());

                            });
                        });
                    }, throwableDevice -> {
                        //QUE HACER SI NO EXISTE EL DEVICE
                        AppDatabase.getInstance().deviceDAO().insert(device).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                            AppDatabase.getInstance().elementDAO().insertMany(elements).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                AppDatabase.getInstance().canvaDAO().insert(canva).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                    LastProjectState.getInstance().setProjectId(project.getId());
                                    LastProjectState.getInstance().setCanvaId(canva.getId());

                                });
                            });
                        });
                    });
                }, throwable1 -> {
                    //ERROR PORQUE NO EXISTE PROYECTO
                    AppDatabase.getInstance().projectDAO().insert(project)
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                        //
                        AppDatabase.getInstance().deviceDAO().insert(device).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                            AppDatabase.getInstance().elementDAO().insertMany(elements).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                AppDatabase.getInstance().canvaDAO().insert(canva).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                    LastProjectState.getInstance().setProjectId(project.getId());
                                    LastProjectState.getInstance().setCanvaId(canva.getId());

                                });
                            });
                        });
                    });

                });


                break;
            default:
                Log.i("EVENT", "Default");
                break;
        }
    }

}
