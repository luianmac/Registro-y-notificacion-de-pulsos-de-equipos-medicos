package com.grupo2.iomt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.grupo2.iomt.db.DB;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin, btnRegistrarse;

    private RequestQueue mQueue=null;
    private String token=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        * Variables creadas referenciadas a los controles
        * Richard*/

        mQueue= Volley.newRequestQueue(this);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegistrarse = (Button) findViewById(R.id.btnRegistrarse);
        DB db = instanceDB("mainDB");
    }

    public void IniciarSesion(View view){
        final EditText dt1=(EditText) findViewById(R.id.txtUser);
        final EditText dt2=(EditText) findViewById(R.id.txtPasswd);
        String usuario=dt1.getText().toString();
        String contrasena=dt2.getText().toString();
        iniciarSesion(usuario,contrasena);
    }

    private void iniciarSesion(String usuario, String contrasena) {
        Map<String, String> params=new HashMap<>();
        params.put("username", usuario);
        params.put("password", contrasena);

        JSONObject parametros=new JSONObject(params);
        String URL = "https://amstdb.herokuapp.com/db/nuevo-jwt";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, parametros, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                try {
                    token = response.getString("token");
                    Intent i = new Intent(getBaseContext(), Menu.class);
                    i.putExtra("token", token);
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Alerta");
                alertDialog.setMessage("Credenciales Incorrectas");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int
                                    which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
        mQueue.add(request);
    }

    private DB instanceDB(String name){
        DB db = Room.databaseBuilder(this, DB.class, name)
                .allowMainThreadQueries()
                .build();
        return db;
    }
}
