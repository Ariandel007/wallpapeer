package pe.edu.upc.wallpapeer.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import pe.edu.upc.wallpapeer.Constants;
import pe.edu.upc.wallpapeer.views.LastProjectActivity;
import pe.edu.upc.wallpapeer.model.User;
import pe.edu.upc.wallpapeer.utils.ApiError;
import pe.edu.upc.wallpapeer.views.ChangePasswordActivity;
import pe.edu.upc.wallpapeer.views.CreateProjectActivity;
import pe.edu.upc.wallpapeer.views.JoinLienzoActivity;
import pe.edu.upc.wallpapeer.views.JoinProjectActivity;
import pe.edu.upc.wallpapeer.views.ProfileActivity;
import pe.edu.upc.wallpapeer.views.ProjectListActivity;
import pe.edu.upc.wallpapeer.views.RegistrarActivity;


public class LoginViewModel extends ViewModel implements Serializable {
    //private User user;
    public static String token;
    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();

    public MutableLiveData<String> password_re = new MutableLiveData<>();

    private User user;
    private Context context;


    public LoginViewModel (User user, Context context){
        this.user = user;
        this.context = context;
        this.username.setValue(user.getUserName());
    }

    public void onLoginClick(){
        user.setUserName(username.getValue());
        user.setPassword(password.getValue());

        if(user.isValidUsername() && user.isValidPassword()){

            Map<String, String> datos = new HashMap<>();
            datos.put("username", username.getValue());
            datos.put("password", password.getValue());
            JSONObject jsondata = new JSONObject(datos);

            AndroidNetworking.post("https://infinite-tundra-77261.herokuapp.com/api/auth/signin")
                    .addJSONObjectBody(jsondata)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String username = response.getString("username");

                                String tokenType = response.getString("tokenType");
                                token = response.getString("accessToken");

                                Toast.makeText(context, "El usuario "+username+" si esta registrado" + " ", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(context, ProfileActivity.class);
                                intent.putExtra("USER", user);
                                context.startActivity(intent);

                            } catch (JSONException e) {
                                Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(context, "Error: "+anError.getErrorDetail(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }else{
            Toast.makeText(context, "No se puede ingresar si existen campos vacios", Toast.LENGTH_SHORT).show();
        }
    }

    public void irRegistrar(View view){
        Intent intent = new Intent(context, RegistrarActivity.class);
        context.startActivity(intent);
    }

    public void onRegisterClick(){
        user.setUserName(username.getValue());
        user.setEmail(email.getValue());
        user.setPassword(password.getValue());

        if(user.isValidUsername() && user.isValidPassword() && user.isValidEmail()){

            Map<String, String> datos = new HashMap<>();
            datos.put("username", username.getValue());
            datos.put("email", email.getValue());
            datos.put("password", password.getValue());

            JSONObject jsondata = new JSONObject(datos);

            AndroidNetworking.post("https://infinite-tundra-77261.herokuapp.com/api/auth/signup")
                    .addJSONObjectBody(jsondata)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String messageSucces = response.getString("message");
                                Toast.makeText(context, messageSucces, Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(context, "Error: EL usuario esta repetido o tiene un error", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else{
            Toast.makeText(context, "No se puede ingresar si existen campos vacios", Toast.LENGTH_SHORT).show();
        }
    }
    public void irCambioContrasena(View view){
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        intent.putExtra("USER", user);
        context.startActivity(intent);
    }
    public void onChangePasswordClick(){
        user.setPassword(password.getValue());

        if(user.isValidPassword()){
            if (password_re.getValue().equals(password.getValue())) {
                Map<String, String> datos = new HashMap<>();
                datos.put("username", username.getValue());
                datos.put("password", password.getValue());
                JSONObject jsondata = new JSONObject(datos);
                String bearerToken = "Bearer " + token;
                AndroidNetworking.put("https://infinite-tundra-77261.herokuapp.com/api/user/user/" + username.getValue()).addHeaders("Authorization", bearerToken)
                        .addJSONObjectBody(jsondata)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String username = response.getString("username");
                                    Toast.makeText(context, "El usuario " + username + " ha modificado su contraseña", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(context, ProfileActivity.class);
                                    intent.putExtra("USER", user);
                                    context.startActivity(intent);

                                } catch (JSONException e) {
                                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(context, "Error: " + anError.getErrorDetail(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }else{
                Toast.makeText(context, "Las contraseñas ingresadas no son iguales", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Existen campos vacios", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSearchDevices() {
        //TODO: Buscar los dispositivos integrando librerias Wifi Direct
    }

    public void irCrearProyecto(View view){
        Intent intent = new Intent(context, CreateProjectActivity.class);
        context.startActivity(intent);
    }
    public void irMisProyectos(View view){
        Intent intent = new Intent(context, ProjectListActivity.class);
        intent.putExtra("USER", user);
        context.startActivity(intent);
    }

    public void irUnirseProyecto(View view){
        Intent intent = new Intent(context, JoinProjectActivity.class);
        intent.putExtra("USER", user);
        context.startActivity(intent);
    }

    public void irUnirseProyectoComoLienzo(View view){
        Intent intent = new Intent(context, JoinLienzoActivity.class);
        intent.putExtra("USER", user);
        intent.putExtra(Constants.IS_OFFLINE, false);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void abrirUltimoProyecto(View view){
        Intent intent = new Intent(context, LastProjectActivity.class);
        intent.putExtra("USER", user);
        intent.putExtra(Constants.IS_OFFLINE, false);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
