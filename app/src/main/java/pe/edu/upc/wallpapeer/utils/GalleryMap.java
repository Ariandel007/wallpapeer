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
                    INSTANCE.galleryHashSet.put(0, "ig_atom");
                    INSTANCE.galleryHashSet.put(1, "ig_beaker");
                    INSTANCE.galleryHashSet.put(2, "ig_bell");
                    INSTANCE.galleryHashSet.put(3, "ig_board");
                    INSTANCE.galleryHashSet.put(4, "ig_boardandteacher");
                    INSTANCE.galleryHashSet.put(5, "ig_book");
                    INSTANCE.galleryHashSet.put(6, "ig_booksandapple");
                    INSTANCE.galleryHashSet.put(7, "ig_briefcase");
                    INSTANCE.galleryHashSet.put(8, "ig_bus");
                    INSTANCE.galleryHashSet.put(9, "ig_certificate");
                    INSTANCE.galleryHashSet.put(10, "ig_circlebell");
                    INSTANCE.galleryHashSet.put(11, "ig_circlebooksandapple");
                    INSTANCE.galleryHashSet.put(12, "ig_circlebriefcase");
                    INSTANCE.galleryHashSet.put(13, "ig_circlebuilding");
                    INSTANCE.galleryHashSet.put(14, "ig_circlecoach");
                    INSTANCE.galleryHashSet.put(15, "ig_circlegraduation");
                    INSTANCE.galleryHashSet.put(16, "ig_circlemonitor");
                    INSTANCE.galleryHashSet.put(17, "ig_circlenotebook");
                    INSTANCE.galleryHashSet.put(18, "ig_circlepresentation");
                    INSTANCE.galleryHashSet.put(19, "ig_circlescience");
                    INSTANCE.galleryHashSet.put(20, "ig_circleteacher");
                    INSTANCE.galleryHashSet.put(21, "ig_eraser");
                    INSTANCE.galleryHashSet.put(22, "ig_glasses");
                    INSTANCE.galleryHashSet.put(23, "ig_globe");
                    INSTANCE.galleryHashSet.put(24, "ig_graduation");
                    INSTANCE.galleryHashSet.put(25, "ig_lightbulb");
                    INSTANCE.galleryHashSet.put(26, "ig_microscope");
                    INSTANCE.galleryHashSet.put(27, "ig_owl");
                    INSTANCE.galleryHashSet.put(28, "ig_palette");
                    INSTANCE.galleryHashSet.put(29, "ig_pencil");
                }

            }
        }

        return INSTANCE;
    }

    public HashMap<Integer, String> getGalleryHashSet() {
        return galleryHashSet;
    }
}
