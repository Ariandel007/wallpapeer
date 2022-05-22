package pe.edu.upc.wallpapeer.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.views.ImageListActivity;

public class ImageListArrayAdapter extends ArrayAdapter<ImageListActivity.Item> {
    private final List<ImageListActivity.Item> list;
    private final Activity context;

    static class ViewHolder {
        protected ImageView img;
    }

    public ImageListArrayAdapter(Activity context, List<ImageListActivity.Item> list) {
        super(context, R.layout.activity_image_row, list);
        this.context = context;
        this.list = list;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.activity_image_row, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) view.findViewById(R.id.img);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.img.setImageDrawable(list.get(position).getImg());
        return view;
    }


}
