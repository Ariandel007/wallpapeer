package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.databinding.ActivityProjectListBinding;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.model.User;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
import pe.edu.upc.wallpapeer.utils.SingleAdapter;
import pe.edu.upc.wallpapeer.viewmodels.LoginViewModel;
import pe.edu.upc.wallpapeer.viewmodels.factory.LoginViewModelFactory;

public class ProjectListActivity extends AppCompatActivity {

    List<Project> elements = new ArrayList<>();

    RecyclerView recyclerViewProjects;

    androidx.appcompat.widget.AppCompatButton btnGetSelected;

    public String userDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        User user = (User)getIntent().getSerializableExtra("USER");

        ActivityProjectListBinding activityProjectListBinding = DataBindingUtil.setContentView(this,R.layout.activity_project_list);
        LoginViewModel loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(user, this)).get(LoginViewModel.class);

        activityProjectListBinding.setLoginViewModel(loginViewModel);

        recyclerViewProjects = findViewById(R.id.rvProjects);
        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(this));

        btnGetSelected = findViewById(R.id.btnGetSelected);

        userDeviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        if (userDeviceName == null)
            userDeviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");


        init();


    }

    @SuppressLint("CheckResult")
    public void init(){
        Context context = this;
        AppDatabase.getInstance().projectDAO().getAll().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((projects)-> {
            elements = projects;
            Log.e("TEST", String.valueOf(elements.size()));
            SingleAdapter adapter = new SingleAdapter(elements, this);
            recyclerViewProjects.setAdapter(adapter);

            btnGetSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapter.getSelected() != null) {
                        Project projectSelected = adapter.getSelected();
                        AppDatabase.getInstance().deviceDAO().getDeviceByDeviceNameAndProject(userDeviceName, projectSelected.getId())
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        (device) -> {
                            AppDatabase.getInstance().canvaDAO().getCanvaByIdDevice(device.getId())
                                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe((canva)->{
                                        Intent intent = new Intent(context, CanvasActivity.class);
                                        intent.putExtra("project_id",projectSelected.getId());
                                        intent.putExtra("device_id",device.getId());
                                        intent.putExtra("canva_id",canva.getId());
                                        intent.putExtra("project_load","loaded_project");
                                        startActivity(intent);
                                    }, throwable -> {
                                        showToast("Error al traer el canva");
                                    });
                        }, throwable -> {
                                    showToast("Error al traer el device");
                        });

                    } else {
                        showToast("No Selection");
                    }
                }
            });
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}