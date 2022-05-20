package pe.edu.upc.wallpapeer.utils;

public class JoinLienzoTarget {
    private static JoinLienzoTarget INSTANCE;

    private String lastTarget = "";

    public JoinLienzoTarget() {
    }

    public static JoinLienzoTarget getInstance() {
        if(INSTANCE == null) {
//            INSTANCE = new MyLastPinch();
            //remover overhead, asi solo la primera vez se bloqueara
            //IMPORTANTE PARA THREAD SAFE
            synchronized (JoinLienzoTarget.class)
            {
                if(INSTANCE==null)
                {
                    // Si la instancia es nula inicializar
                    INSTANCE = new JoinLienzoTarget();
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
