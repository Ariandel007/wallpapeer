package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.databinding.ActivityProjectListBinding;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.model.User;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
import pe.edu.upc.wallpapeer.utils.ListAdapter;
import pe.edu.upc.wallpapeer.utils.ProjectListElement;
import pe.edu.upc.wallpapeer.viewmodels.LoginViewModel;
import pe.edu.upc.wallpapeer.viewmodels.factory.LoginViewModelFactory;

public class ProjectListActivity extends AppCompatActivity {

    ArrayList<Project> elements;

    RecyclerView recyclerViewProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        User user = (User)getIntent().getSerializableExtra("USER");

        ActivityProjectListBinding activityProjectListBinding = DataBindingUtil.setContentView(this,R.layout.activity_project_list);
        LoginViewModel loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(user, this)).get(LoginViewModel.class);

        activityProjectListBinding.setLoginViewModel(loginViewModel);

        elements = new ArrayList<Project>();

        recyclerViewProjects = findViewById(R.id.rvProjects);
        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(this));

        init();

        ListAdapter adapter = new ListAdapter(elements, this);
        recyclerViewProjects.setAdapter(adapter);

    }

    public void init(){
        AppDatabase.getInstance(this).projectDAO().getAll().subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((projects)-> {
                                if(projects.size() == 0){

                                } else {
                                elements.addAll(projects);

                                }
                            } );
    }
}