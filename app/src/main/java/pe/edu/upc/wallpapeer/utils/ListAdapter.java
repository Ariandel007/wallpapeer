package pe.edu.upc.wallpapeer.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.entities.Project;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Project>  lProjects;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListAdapter(List<Project> itemList, Context context){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.lProjects = itemList;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.project_list_element, null);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        holder.bindData(lProjects.get(position));
    }

    @Override
    public int getItemCount() {
        return lProjects.size();
    }

    public void setItems(List<Project> items) {
        lProjects = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProject;
        TextView ultEdicion, nombreProyecto;

        ViewHolder(View itemView){
            super(itemView);
            ivProject = itemView.findViewById(R.id.projectImage);
            ultEdicion = itemView.findViewById(R.id.tvUltEdicion);
            nombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
        }

        void bindData(final Project item){
            ultEdicion.setText(item.getDateCreation().toString());
            nombreProyecto.setText(item.getName());
        }

    }
}
