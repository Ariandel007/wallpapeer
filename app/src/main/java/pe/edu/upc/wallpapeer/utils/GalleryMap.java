package pe.edu.upc.wallpapeer.utils;

import java.util.HashMap;
import java.util.HashSet;

import pe.edu.upc.wallpapeer.dtos.AcceptingPalette;

public class GalleryMap {
    private static GalleryMap INSTANCE;

    private HashMap<Integer, String> galleryHashSet = new HashMap<>();

    public GalleryMap() {
    }

    public static GalleryMap getInstance() {
        if(INSTANCE == null) {
//            INSTANCE = new MyLastPinch();
            //remover overhead, asi solo la primera vez se bloqueara
            //IMPORTANTE PARA THREAD SAFE
            synchronized (GalleryMap.class)
            {
                if(INSTANCE==null)
                {
                    // Si la instancia es nula inicializar
                    INSTANCE = new GalleryMap();
                    INSTANCE.galleryHashSet.put(0, "ig_beaker");
                }

            }
        }

        return INSTANCE;
    }

    public HashMap<Integer, String> getGalleryHashSet() {
        return galleryHashSet;
    }
}
