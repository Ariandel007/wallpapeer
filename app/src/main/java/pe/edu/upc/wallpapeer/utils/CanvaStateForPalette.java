package pe.edu.upc.wallpapeer.utils;

import pe.edu.upc.wallpapeer.dtos.AcceptingPalette;

public class CanvaStateForPalette {
    private static CanvaStateForPalette INSTANCE;

    private AcceptingPalette acceptingPalette;

    public CanvaStateForPalette() {
    }

    public static CanvaStateForPalette getInstance() {
        if(INSTANCE == null) {
//            INSTANCE = new MyLastPinch();
            //remover overhead, asi solo la primera vez se bloqueara
            //IMPORTANTE PARA THREAD SAFE
            synchronized (CanvaStateForPalette.class)
            {
                if(INSTANCE==null)
                {
                    // Si la instancia es nula inicializar
                    INSTANCE = new CanvaStateForPalette();
                }

            }
        }

        return INSTANCE;
    }

    public AcceptingPalette getAcceptingPalette() {
        return acceptingPalette;
    }

    public void setAcceptingPalette(AcceptingPalette acceptingPalette) {
        this.acceptingPalette = acceptingPalette;
    }
}
