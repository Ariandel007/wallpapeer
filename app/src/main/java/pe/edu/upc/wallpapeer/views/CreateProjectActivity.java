package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCrearProyecto:
                String idM = UUID.randomUUID().toString();
                Date dateM = Calendar.getInstance().getTime();
                String nameM = etName.getText().toString();
                Float altoM = Float.parseFloat(etAlto.getText().toString());
                Float anchoM = Float.parseFloat(etAncho.getText().toString());
                proyecto = new Project(idM, nameM, dateM, altoM, anchoM);
                Context context = this;

                AppDatabase.getInstance(this).projectDAO().insert(proyecto).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            //Ejemplo de como llamar un getAll
                            Log.e("nunca dudé", "a");
                            AppDatabase.getInstance(context).projectDAO().getAll().subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((projects)-> {
                                Log.e("Proyectos", String.valueOf(projects.size()));
                            } );

                            }, throwable -> {
                                Log.e("TAG", "AAAAAAAAAAAAAAAAAAAAAA", throwable);
                            }
                        );






                //Log.e("AHORA SI", a);

                Intent intent = new Intent(this, CanvasActivity.class);
                this.startActivity(intent);

                break;
        }
    }
}