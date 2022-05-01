package pe.edu.upc.wallpapeer.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    //General
    private final static String DATABASE_NAME = "LocalDB";
    private final static int DATABASE_VERSION = 1;

    //Entidades

    //Palette
    public static final String TBL_PALETTE = "palette";
    public static final String PALETTE_ID = "id";
    public static final String PALETTE_NAME = "name";
    public static final String PALETTE_OPTION = "selected_option";

    private String TBL_CREATE_PALETTE = "create table " + TBL_PALETTE + " (" +
            PALETTE_ID + "integer primary key, "+
            PALETTE_NAME + "text," +
            PALETTE_OPTION + "integer)";

    //Device
    public static final String TBL_DEVICE = "device";
    public static final String DEVICE_ID = "id";
    public static final String DEVICE_WIDTH = "width_screen";
    public static final String DEVICE_HEIGHT = "height_screen";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_MAC = "mac_address";
    public static final String ID_PALETTE = "id_palette";

    private String TBL_CREATE_DEVICE = "create table " + TBL_DEVICE + " (" +
            DEVICE_ID + "integer primary key," +
            DEVICE_WIDTH + "real," +
            DEVICE_HEIGHT + "real," +
            DEVICE_NAME + "text," +
            DEVICE_MAC + "text, " +
            ID_PALETTE + "integer, " +
            "FOREIGN KEY(" + ID_PALETTE + ") REFERENCES palette(id)";

    //Canvas
    public static final String TBL_CANVAS = "canvas";
    public static final String CANVAS_ID = "id";
    public static final String CANVAS_MAIN = "is_main";
    public static final String CANVAS_WIDTH = "width_canvas";
    public static final String CANVAS_HEIGHT = "height_canvas";
    public static final String CANVAS_POSX = "posx";
    public static final String CANVAS_POSY = "posy";

    private String TBL_CREATE_CANVAS = "create table " + TBL_CANVAS + " (" +
            CANVAS_ID + "integer primary key, " +
            CANVAS_MAIN + "integer, " +
            CANVAS_WIDTH + "real, "+
            CANVAS_HEIGHT + "real, "+
            CANVAS_POSX + "real, "+
            CANVAS_POSY + "real)";

    //Project
    public static final String TBL_PROJECT = "project";
    public static final String PROJECT_ID = "id";
    public static final String PROJECT_NAME = "name";
    public static final String PROJECT_DATE = "date_creation";

    private String TBL_CREATE_PROJECT = "create table " + TBL_PROJECT + " (" +
            PROJECT_ID + "text primary key, "+
            PROJECT_NAME + "text, "+
            PROJECT_DATE + "text)";

    //Element
    public static final String TBL_ELEMENT = "element";
    public static final String ELEMENT_ID = "id";
    public static final String ELEMENT_TYPE = "type_element";
    public static final String ELEMENT_WIDTH = "width_element";
    public static final String ELEMENT_HEIGHT = "height_element";
    public static final String ELEMENT_POSX = "posx_element";
    public static final String ELEMENT_POSY = "posy_element";
    public static final String ELEMENT_Z = "z_index";
    public static final String ELEMENT_OPACITY = "opacity";
    public static final String ELEMENT_ROTATION = "rotation";

    private String TBL_CREATE_ELEMENT = "create table " + TBL_ELEMENT + " (" +
            ELEMENT_ID + "integer primary key, " +
            ELEMENT_TYPE + "text, "+
            ELEMENT_WIDTH + "real, "+
            ELEMENT_HEIGHT + "real, "+
            ELEMENT_POSX + "real, "+
            ELEMENT_POSY + "real, "+
            ELEMENT_Z + "integer, "+
            ELEMENT_OPACITY + "real, "+
            ELEMENT_ROTATION + "real)";

    //Image
    public static final String TBL_IMAGE = "image";
    public static final String IMAGE_ID = "id";
    public static final String IMAGE_IMAGE = "image";
    public static final String ID_ELEMENT = "id_element";

    private String TBL_CREATE_IMAGE = "create table " + TBL_IMAGE + " (" +
            IMAGE_ID + "integer primary key, " +
            IMAGE_IMAGE + "blob, " +
            ID_ELEMENT + "integer, "+
            "FOREIGN KEY(" + ID_ELEMENT + ") REFERENCES element(id)";

    //Filter
    public static final String TBL_FILTER = "filter";
    public static final String FILTER_ID = "id";
    public static final String FILTER_NAME = "name";
    public static final String ID_IMAGE = "id_image";

    private String TBL_CREATE_FILTER = "create table " + TBL_FILTER + " (" +
            FILTER_ID + "integer primary key, " +
            FILTER_NAME + "text, " +
            ID_IMAGE + "integer, "+
            "FOREIGN KEY(" + ID_IMAGE + ") REFERENCES image(id)";

    //Color
    public static final String TBL_COLOR = "color";
    public static final String COLOR_ID = "id";
    public static final String COLOR_CODE = "color_code";

    private String TBL_CREATE_COLOR = "create table " + TBL_COLOR + " (" +
            COLOR_ID + "integer primary key, " +
            COLOR_CODE + "text, " +
            ID_ELEMENT + "integer, "+
            "FOREIGN KEY(" + ID_ELEMENT + ") REFERENCES element(id)";

    //Text
    public static final String TBL_TEXT = "text";
    public static final String TEXT_ID = "id";
    public static final String TEXT_SIZE = "size";
    public static final String TEXT_TEXT = "text";

    private String TBL_CREATE_TEXT = "create table " + TBL_TEXT + " (" +
            TEXT_ID + "integer primary key, " +
            TEXT_SIZE + "integer, " +
            TEXT_TEXT + "text, " +
            ID_ELEMENT + "integer, "+
            "FOREIGN KEY(" + ID_ELEMENT + ") REFERENCES element(id)";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TBL_CREATE_PALETTE);
        db.execSQL(TBL_CREATE_DEVICE);
        db.execSQL(TBL_CREATE_CANVAS);
        db.execSQL(TBL_CREATE_PROJECT);
        db.execSQL(TBL_CREATE_ELEMENT);
        db.execSQL(TBL_CREATE_IMAGE);
        db.execSQL(TBL_CREATE_FILTER);
        db.execSQL(TBL_CREATE_COLOR);
        db.execSQL(TBL_CREATE_TEXT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
