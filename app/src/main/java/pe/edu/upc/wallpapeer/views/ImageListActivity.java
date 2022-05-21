package pe.edu.upc.wallpapeer.views;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.utils.ImageListArrayAdapter;

public class ImageListActivity extends ListActivity {
    private TypedArray imgs;
    private List<Item> imgList;
    public static String RESULT_POSITION = "posicion";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateItemList();
        ArrayAdapter<Item> adapter = new ImageListArrayAdapter(this, imgList);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(RESULT_POSITION, position);
                setResult(RESULT_OK, returnIntent);
                imgs.recycle(); //recycle images
                finish();
            }
        });
    }

    private void populateItemList() {
        imgList = new ArrayList<Item>();
        imgs = getResources().obtainTypedArray(R.array.images_palette_array);
        for (int i = 0; i < imgs.length(); i++){
            imgList.add(new Item(imgs.getDrawable(i)));
        }
    }

    public class Item {
        private Drawable img;

        public Item(Drawable img) {
            this.img = img;
        }

        public Drawable getImg() {
            return img;
        }

        public void setImg(Drawable img) {
            this.img = img;
        }
    }

}

