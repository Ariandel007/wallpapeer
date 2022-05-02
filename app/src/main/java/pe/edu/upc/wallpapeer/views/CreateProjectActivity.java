package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.App;
import pe.edu.upc.wallpapeer.R;

import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Device;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.utils.AppDatabase;

public class CreateProjectActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnCreateProject;
    EditText etName, etAlto, etAncho;
    Project proyecto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        btnCreateProject = findViewById(R.id.btnCrearProyecto);
        btnCreateProject.setOnClickListener(this);

        etName = findViewById(R.id.etNombreProyecto);
        etAlto = findViewById(R.id.etAltoLienzo);
        etAncho = findViewById(R.id.etAnchoLienzo);



    }

    @SuppressLint("CheckResult")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCrearProyecto:
                String idMPRoject = UUID.randomUUID().toString();
                Date dateM = Calendar.getInstance().getTime();
                String nameM = etName.getText().toString();
                Float altoM = Float.parseFloat(etAlto.getText().toString());
                Float anchoM = Float.parseFloat(etAncho.getText().toString());
                proyecto = new Project(idMPRoject, nameM, dateM, altoM, anchoM);
                Context context = this;

                String userDeviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
                if (userDeviceName == null)
                    userDeviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");
                final String finalUserDeviceName = userDeviceName;

                AppDatabase.getInstance().projectDAO().insert(proyecto).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            Log.e("nunca dudé", "a");
//                            Ejemplo de llamar un getAll
//                            AppDatabase.getInstance(context).projectDAO().getAll().subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe((projects)-> {
//                                Log.e("Proyectos", String.valueOf(projects.size()));
//                            } );
                            Device device = new Device();
                            device.setId(UUID.randomUUID().toString());
                            device.setId_project(idMPRoject);
                            device.setDeviceName(finalUserDeviceName);
                            device.setHeightScreen(getScreenHeight());
                            device.setWidthScreen(getScreenWidth());

                            AppDatabase.getInstance().deviceDAO().insert(device).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                                Canva canva = new Canva(UUID.randomUUID().toString(), true, device.getHeightScreen(), device.getWidthScreen(), 0, 0, device.getId());

                                AppDatabase.getInstance().canvaDAO().insert(canva).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {

                                   Intent intent = new Intent(this, CanvasActivity.class);
                                   intent.putExtra("project_id",idMPRoject);
                                   intent.putExtra("device_id",device.getId());
                                    intent.putExtra("canva_id",canva.getId());
                                    intent.putExtra("project_load","new_project");
                                    this.startActivity(intent);

                                }, throwable -> {
                                    Log.e("TAG", "Error", throwable);
                                });
                            }, throwable -> {
                                Log.e("TAG", "Error", throwable);
                            });

                            }, throwable -> {
                                Log.e("TAG", "Error", throwable);
                            }
                        );







//                Intent intent = new Intent(this, CanvasActivity.class);
//                this.startActivity(intent);

                break;
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}