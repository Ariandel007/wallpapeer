package pe.edu.upc.wallpapeer.utils;

public class JoinPaletaTarget {
    private static JoinPaletaTarget INSTANCE;

    private String lastTarget = "";

    public JoinPaletaTarget() {
    }

    public static JoinPaletaTarget getInstance() {
        if(INSTANCE == null) {
//            INSTANCE = new MyLastPinch();
            //remover overhead, asi solo la primera vez se bloqueara
            //IMPORTANTE PARA THREAD SAFE
            synchronized (JoinPaletaTarget.class)
            {
                if(INSTANCE==null)
                {
                    // Si la instancia es nula inicializar
                    INSTANCE = new JoinPaletaTarget();
                }

            }
        }

        return INSTANCE;
    }

    public String getLastTarget() {
        return lastTarget;
    }

    public void setLastTarget(String lastTarget) {
        this.lastTarget = lastTarget;
    }
}
