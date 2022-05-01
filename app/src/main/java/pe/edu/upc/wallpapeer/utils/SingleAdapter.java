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

public class SingleAdapter extends RecyclerView.Adapter<SingleAdapter.SingleViewHolder> {
    private int selected_position = -1;

    private List<Project>  lProjects;
    private LayoutInflater layoutInflater;
    private Context context;
    private int checkedPosition = 0;

    public SingleAdapter(List<Project> itemList, Context context){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.lProjects = itemList;
    }

//    @NonNull
//    @Override
//    public SingleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = layoutInflater.inflate(R.layout.project_list_element, null);
//        return new SingleAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull SingleAdapter.ViewHolder holder, int position) {
//        holder.bindData(lProjects.get(position));
//    }

    @Override
    public int getItemCount() {
        return lProjects.size();
    }

    public void setItems(List<Project> items) {
        lProjects = items;
    }

//    public class ViewHolder extends RecyclerView.ViewHolder{
//        ImageView ivProject;
//        TextView ultEdicion, nombreProyecto;
//
//        ViewHolder(View itemView){
//            super(itemView);
//            ivProject = itemView.findViewById(R.id.projectImage);
//            ultEdicion = itemView.findViewById(R.id.tvUltEdicion);
//            nombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
//        }
//
//        void bindData(final Project item){
//            ultEdicion.setText(item.getDateCreation().toString());
//            nombreProyecto.setText(item.getName());
//        }
//
//    }


    @NonNull
    @Override
    public SingleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_list_element, viewGroup, false);
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleViewHolder singleViewHolder, int position) {
        singleViewHolder.bind(lProjects.get(position));
    }

    class SingleViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProject;
        TextView ultEdicion, nombreProyecto;

        SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProject = itemView.findViewById(R.id.projectImage);
            ultEdicion = itemView.findViewById(R.id.tvUltEdicion);
            nombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
        }

        void bind(final Project project) {
            if (checkedPosition == -1) {
                ivProject.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    ivProject.setVisibility(View.VISIBLE);
                } else {
                    ivProject.setVisibility(View.GONE);
                }
            }
            ultEdicion.setText(project.getDateCreation().toString());
            nombreProyecto.setText(project.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ivProject.setVisibility(View.VISIBLE);
                    if (checkedPosition != getAdapterPosition()) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                }
            });
        }
    }

    public Project getSelected() {
        if (checkedPosition != -1) {
            return lProjects.get(checkedPosition);
        }
        return null;
    }
}
