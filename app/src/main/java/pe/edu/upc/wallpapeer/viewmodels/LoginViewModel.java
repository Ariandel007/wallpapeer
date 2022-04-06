package pe.edu.upc.wallpapeer.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

import pe.edu.upc.wallpapeer.model.User;
import pe.edu.upc.wallpapeer.views.RegistrarActivity;


public class LoginViewModel extends ViewModel {
    //private User user;
    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();

    private User user;
    private Context context;

    public LoginViewModel (User user, Context context){
        this.user = user;
        this.context = context;
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
                                Toast.makeText(context, "El usuario "+username+" si esta registrado", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("sdfdf",e.getMessage());
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
        Intent i = new Intent(this, RegistrarActivity.class);
        startActivity(i);
    }

}
