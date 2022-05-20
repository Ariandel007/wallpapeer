package pe.edu.upc.wallpapeer.connections;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.App;
import pe.edu.upc.wallpapeer.LocalDevice;
import pe.edu.upc.wallpapeer.dtos.AcceptingPalette;
import pe.edu.upc.wallpapeer.dtos.AddingPalette;
import pe.edu.upc.wallpapeer.dtos.ChangingOption;
import pe.edu.upc.wallpapeer.dtos.EngagePinchEvent;
import pe.edu.upc.wallpapeer.dtos.NewElementInserted;
import pe.edu.upc.wallpapeer.dtos.PinchEventResponse;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Device;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Palette;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
import pe.edu.upc.wallpapeer.utils.CanvaStateForPalette;
import pe.edu.upc.wallpapeer.utils.CodeEvent;
import pe.edu.upc.wallpapeer.utils.JsonConverter;
import pe.edu.upc.wallpapeer.utils.LastProjectState;
import pe.edu.upc.wallpapeer.utils.MyLastPinch;
import pe.edu.upc.wallpapeer.utils.PaletteState;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;

public class Client extends IMessenger {
    private Socket socket;
    private String peerName;
    private String host;
    private ConnectionPeerToPeerViewModel model;
    private MutableLiveData<Boolean> isConnected;

    public Client(String host, ConnectionPeerToPeerViewModel model, MutableLiveData<Boolean> isConnected) {
        this.host = host;
        this.isConnected = isConnected;
        this.model = model;
    }

    @Override
    public void run() {
        this.socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, 8888), 5000);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // Tras conectar, enviamos el nombre de nuestro dispositivo como primer mensaje
        send(LocalDevice.getInstance().getDevice().deviceName, false);

        // Ya he leído el nombre del par
        boolean isAddresseeSet = false;

        while (socket != null) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                String messageText = (String) inputStream.readObject();
                if (messageText != null) {
                    if (isAddresseeSet && messageText.length() >= 22) {
                        String eventCode = messageText.substring(17,22);
                        deserializeBasedOnEventCode(eventCode,messageText);
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
    }


    @SuppressLint("CheckResult")
    private void deserializeBasedOnEventCode(String eventCode, String jsonMessage) {
        long currentMls = new Date().getTime();

        switch(eventCode) {
            case CodeEvent.PINCH_EVENT:
                Log.i("EVENT", "PINCH_EVENT");
                if(MyLastPinch.getInstance().getProjectId() != null && MyLastPinch.getInstance().getCanva() != null && MyLastPinch.getInstance().getDate() != null) {
                    EngagePinchEvent engagePinchEvent = JsonConverter.getGson().fromJson(jsonMessage, EngagePinchEvent.class);
                    if(engagePinchEvent.getOriginalSender().equals(LastProjectState.getInstance().getDeviceName())) {
                        return;
                    }
                    if(!engagePinchEvent.getTrueTargetDevice().equals(LastProjectState.getInstance().getDeviceName())) {
                        return;
                    }
                    //Comprobar si entra en el rango de tiempo
                    if(currentMls - engagePinchEvent.getDatePinch().getTime() > 5000) {
                        return;
                    }

                    if(MyLastPinch.getInstance().getDirection().equals("RIGHT") && engagePinchEvent.getDirection().equals("LEFT")) {
                        float posXnewCanva = MyLastPinch.getInstance().getPinchX() + MyLastPinch.getInstance().getCanva().getPosX();
                        float posYnewCanva = MyLastPinch.getInstance().getPinchY() + MyLastPinch.getInstance().getCanva().getPosY() - engagePinchEvent.getPosPinchY();

                        if(posXnewCanva < 0 || posYnewCanva < 0) {
                            //Se sale de los limites
                            return;
                        }

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
                        pinchEventResponse.setOriginalSender(LastProjectState.getInstance().getDeviceName());

                        onPinchEvent( pinchEventResponse,  engagePinchEvent,  newCanva,  newDevice,  posXnewCanva,  posYnewCanva);
                    }

                    if(MyLastPinch.getInstance().getDirection().equals("LEFT") && engagePinchEvent.getDirection().equals("RIGHT")) {


                        float posXnewCanva = MyLastPinch.getInstance().getPinchX() + MyLastPinch.getInstance().getCanva().getPosX() - engagePinchEvent.getWidthScreenPinch();
                        float posYnewCanva = MyLastPinch.getInstance().getPinchY() + MyLastPinch.getInstance().getCanva().getPosY() - engagePinchEvent.getPosPinchY();

                        if(posXnewCanva < 0 || posYnewCanva < 0) {
                            //Se sale de los limites
                            return;
                        }

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
                        pinchEventResponse.setOriginalSender(LastProjectState.getInstance().getDeviceName());

                        onPinchEvent( pinchEventResponse,  engagePinchEvent,  newCanva,  newDevice,  posXnewCanva,  posYnewCanva);
                    }

                    if(MyLastPinch.getInstance().getDirection().equals("UP") && engagePinchEvent.getDirection().equals("DOWN")) {

                        float posXnewCanva = MyLastPinch.getInstance().getPinchX() + MyLastPinch.getInstance().getCanva().getPosX() - engagePinchEvent.getPosPinchX();
                        float posYnewCanva = MyLastPinch.getInstance().getPinchY() + MyLastPinch.getInstance().getCanva().getPosY() - engagePinchEvent.getHeightScreenPinch();

                        if(posXnewCanva < 0 || posYnewCanva < 0) {
                            //Se sale de los limites
                            return;
                        }

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
                        pinchEventResponse.setOriginalSender(LastProjectState.getInstance().getDeviceName());

                        onPinchEvent( pinchEventResponse,  engagePinchEvent,  newCanva,  newDevice,  posXnewCanva,  posYnewCanva);
                    }

                    if(MyLastPinch.getInstance().getDirection().equals("DOWN") && engagePinchEvent.getDirection().equals("UP")) {
                        float posXnewCanva = MyLastPinch.getInstance().getPinchX() + MyLastPinch.getInstance().getCanva().getPosX() - engagePinchEvent.getPosPinchX();
                        float posYnewCanva = MyLastPinch.getInstance().getPinchY() + MyLastPinch.getInstance().getCanva().getPosY();

                        if(posXnewCanva < 0 || posYnewCanva < 0) {
                            //Se sale de los limites
                            return;
                        }

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
                        pinchEventResponse.setOriginalSender(LastProjectState.getInstance().getDeviceName());

                        onPinchEvent( pinchEventResponse,  engagePinchEvent,  newCanva,  newDevice,  posXnewCanva,  posYnewCanva);
                    }

                }

                break;
            case CodeEvent.ADDING_PALLETE_TO_DEVICE:
                if(LastProjectState.getInstance().getDeviceName() == null){
                    return;
                }
                Log.i("EVENT", "ADDING_PALLETE_TO_DEVICE");
                AddingPalette addingPalette = JsonConverter.getGson().fromJson(jsonMessage, AddingPalette.class);
                if(addingPalette.getOriginalSender().equals(LastProjectState.getInstance().getDeviceName())) {
                    return;
                }
                if(LastProjectState.getInstance().getProjectId() == null){
                    return;
                }
                if(!addingPalette.getTargetDeviceName().equals(LastProjectState.getInstance().getDeviceName())){
                    return;
                }
                Palette palette = new Palette();
                palette.setId(UUID.randomUUID().toString());
                palette.setName(addingPalette.getDeviceName());
                palette.setSelectedOption(addingPalette.getSelectedOption());
                palette.setSubOption(addingPalette.getSubOption());
                palette.setPaletteDeviceName(addingPalette.getTargetDeviceName());

                //Toast.makeText(App.getContext(), addingPalette.getDeviceName() + " se unió como paleta", Toast.LENGTH_SHORT).show();
                AppDatabase.getInstance().deviceDAO().getDeviceByDeviceNameAndProject(LastProjectState.getInstance().getDeviceName(), LastProjectState.getInstance().getProjectId())
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                        subscribe((myDevice)->{
                            String myId = myDevice.getId();
                            palette.setId_device(myId);
                            AppDatabase.getInstance().paletteDAO().getPaletteByProjectIdDeviceId(LastProjectState.getInstance().getProjectId(), myId)
                                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                                    subscribe((myPalette)->{
                                        myPalette.setName(addingPalette.getDeviceName());
                                        myPalette.setSelectedOption(addingPalette.getSelectedOption());
                                        myPalette.setSubOption(addingPalette.getSubOption());
                                        myPalette.setPaletteDeviceName(addingPalette.getTargetDeviceName());

                                        AppDatabase.getInstance().paletteDAO().update(myPalette).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                                                subscribe(()->{
                                                    Log.i("Update Palette", "Se actualizó la paleta");
                                                    AcceptingPalette acceptingPalette = new AcceptingPalette();
                                                    acceptingPalette.setA1_eventCode(CodeEvent.ACCEPTED_PALETTE);
                                                    acceptingPalette.setMacAddress("");
                                                    acceptingPalette.setLinkedDevice(myPalette.getPaletteDeviceName());
                                                    acceptingPalette.setLinkedIdDevice(myPalette.getId_device());
                                                    acceptingPalette.setPaletteDeviceName(addingPalette.getDeviceName());
                                                    AppDatabase.getInstance().projectDAO().getProject(LastProjectState.getInstance().getProjectId())
                                                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                                                            subscribe((myProject)->{
                                                                acceptingPalette.setProject(myProject);
                                                                send(JsonConverter.getGson().toJson(acceptingPalette), true);
                                                            });
                                                });
                                    }, throwable -> {
                                        AppDatabase.getInstance().paletteDAO().insert(palette).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                                                subscribe(()->{
                                                    Log.i("Create Palette", "Se creó la paleta");
                                                    AcceptingPalette acceptingPalette = new AcceptingPalette();
                                                    acceptingPalette.setA1_eventCode(CodeEvent.ACCEPTED_PALETTE);
                                                    acceptingPalette.setMacAddress("");
                                                    acceptingPalette.setLinkedDevice(palette.getPaletteDeviceName());
                                                    acceptingPalette.setLinkedIdDevice(palette.getId_device());
                                                    acceptingPalette.setPaletteDeviceName(addingPalette.getDeviceName());
                                                    AppDatabase.getInstance().projectDAO().getProject(LastProjectState.getInstance().getProjectId())
                                                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                                                            subscribe((myProject)->{
                                                                acceptingPalette.setProject(myProject);
                                                                send(JsonConverter.getGson().toJson(acceptingPalette), true);
                                                            });
                                                });
                                    });
                        }, throwable -> {

                        });




                break;
            case CodeEvent.SELECT_OPTION_PALLETE:
                Log.i("EVENT", "SELECT_OPTION_PALLETE");
                ChangingOption changingOption = JsonConverter.getGson().fromJson(jsonMessage, ChangingOption.class);
                if(changingOption.getOriginalSender().equals(LastProjectState.getInstance().getDeviceName())) {
                    return;
                }
                if(LastProjectState.getInstance().getProjectId() == null){
                    return;
                }
                if(!changingOption.getTargetDeviceName().equals(LastProjectState.getInstance().getDeviceName())){
                    return;
                }
                AppDatabase.getInstance().deviceDAO().getDeviceByDeviceNameAndProject(LastProjectState.getInstance().getDeviceName(), LastProjectState.getInstance().getProjectId())
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                        subscribe((myDevice)->{
                            String myId = myDevice.getId();
                            AppDatabase.getInstance().paletteDAO().getPaletteByProjectIdDeviceId(LastProjectState.getInstance().getProjectId(), myId)
                                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                                    subscribe((myPalette)->{
                                        myPalette.setName(changingOption.getDeviceName());
                                        myPalette.setSelectedOption(changingOption.getSelectedOption());
                                        myPalette.setSubOption(changingOption.getSubOption());
                                        myPalette.setPaletteDeviceName(changingOption.getTargetDeviceName());

                                        PaletteState.getInstance().setSelectedOption(changingOption.getSelectedOption());
                                        PaletteState.getInstance().setSubOption(changingOption.getSubOption());
                                        PaletteState.getInstance().setTextToPrint(changingOption.getTextToInsert());
                                        PaletteState.getInstance().setColor(changingOption.getColor());

                                        AppDatabase.getInstance().paletteDAO().insert(myPalette).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                                                subscribe(()->{
                                                    Log.i("Update Palette", "Se actualizó la paleta");
                                                });
                                    }, throwable -> {

                                    });
                        }, throwable -> {

                        });

                break;
            case CodeEvent.ACCEPTED_PALETTE:
                AcceptingPalette acceptingPalette = JsonConverter.getGson().fromJson(jsonMessage, AcceptingPalette.class);
                if(!acceptingPalette.getPaletteDeviceName().equals(LastProjectState.getInstance().getDeviceName())){
                    return;
                }
                CanvaStateForPalette.getInstance().setAcceptingPalette(acceptingPalette);
                Project project2 = acceptingPalette.getProject();

//                Palette palette1 = new Palette();
//                palette1.setId(UUID.randomUUID().toString());
//                palette1.setId_device(acceptingPalette.getLinkedIdDevice());
//                palette1.setPaletteDeviceName("");
//                palette1.setSelectedOption(1);
//                palette1.setSubOption(-1);
//                palette1.setName(acceptingPalette.getPaletteDeviceName());
//                PaletteState.getInstance().setMyDeviceName(acceptingPalette.getPaletteDeviceName());
//                PaletteState.getInstance().setTargetDeviceName(acceptingPalette.getLinkedDevice());
//                PaletteState.getInstance().setSelectedOption(0);
//                PaletteState.getInstance().setSubOption(-1);

                AppDatabase.getInstance().projectDAO().insert(project2).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(()->{
                            Log.i("Proyecto creado", "Se creó el proyecto");

//                            AppDatabase.getInstance().paletteDAO().insert(palette1).subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe();
                        });
                break;
            case CodeEvent.INSERT_NEW_ELEMENT:
                Log.i("EVENT", "INSERT_NEW_ELEMENT");
                if(LastProjectState.getInstance().getProjectId() == null) {
                    return;
                }

                NewElementInserted newElementInserted = JsonConverter.getGson().fromJson(jsonMessage, NewElementInserted.class);
                if(newElementInserted.getOriginalSender().equals(LastProjectState.getInstance().getDeviceName())) {
                    return;
                }
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
                if(pinchEventResponse.getOriginalSender().equals(LastProjectState.getInstance().getDeviceName())) {
                    return;
                }
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

    @SuppressLint("CheckResult")
    public void onPinchEvent(PinchEventResponse pinchEventResponse, EngagePinchEvent engagePinchEvent, Canva newCanva, Device newDevice, float posXnewCanva, float posYnewCanva) {
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
