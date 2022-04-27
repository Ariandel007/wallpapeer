package pe.edu.upc.wallpapeer.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import pe.edu.upc.wallpapeer.entities.Device;
import pe.edu.upc.wallpapeer.entities.Palette;

public class PaletteDevice {
    @Embedded public Palette palette;
    @Relation(
            parentColumn = "id",
            entityColumn = "id_paleta"
    )
    public Device device;
}
