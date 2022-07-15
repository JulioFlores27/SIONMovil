package com.nervion.sionmovil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nervion.sionmovil.Buscador.BuscadorActivity;
import com.nervion.sionmovil.Calidad.CalidadActivity;
import com.nervion.sionmovil.Entinte.EntinteActivity;
import com.nervion.sionmovil.Entregas.EntregasActivity;
import com.nervion.sionmovil.Graficas.GraficasActivity;
import com.nervion.sionmovil.Inventarios.InventariosActivity;
import com.nervion.sionmovil.Movimientos.MovimientosActivity;
import com.nervion.sionmovil.Penalizacion.PenalizacionActivity;
import com.nervion.sionmovil.Surtir.SurtirActivity;
import com.nervion.sionmovil.Vigilancia.SalidasVigilancia_Adapter;
import com.nervion.sionmovil.Vigilancia.SalidasVigilancia_Constructor;
import com.nervion.sionmovil.Vigilancia.VigilanciaActivity;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView mainText, userText;
    private AlertDialog alertDialog;
    public String tiendaGlobal;
    private final FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

    private final String[] tiendas = {"Selecciona:", "01", "55", "56", "58", "A0", "A3", "A5", "A8", "AG", "AI", "AK", "AL", "AM", "Pruebas"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userText = findViewById(R.id.usuarioTextView);
        if (firebaseAuth == null) {
            finish();
            overridePendingTransition(0, 0);
            startActivity(new Intent(MainActivity.this, Login_Activity.class));
            overridePendingTransition(0, 0);
        }else if (firebaseAuth.getDisplayName().equals("")) {
            updateUser();
        }else if (!firebaseAuth.getDisplayName().equals("")) {
            userText.setText(getString(R.string.bienvenido)+"\n"+firebaseAuth.getDisplayName());
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
            createNotificationChannel();
            getToken();
        }

        SharedPreferences resultadoTienda = getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = resultadoTienda.getString("TiendaGlobal", "");
        mainText = findViewById(R.id.tiendaTextView);
        if(!tiendaGlobal.equals("")){ mainText.setText(getString(R.string.tienda)+" "+tiendaGlobal); }else{ alertTienda(); }
        recyclerView = findViewById(R.id.rvModulo);

        List<MainActivity_Constructor> modulos = new ArrayList<>();
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloEntregas), R.drawable.entregas));
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloInventarios), R.drawable.inventarios));
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloBuscador), R.drawable.buscador));
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloMovimientos), R.drawable.movimientos));
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloVigilancia), R.drawable.vigilancia));
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloEntinte), R.drawable.entinte));
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloSurtir), R.drawable.surtir));
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloCalidad), R.drawable.calidad));
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloGraficas), R.drawable.graficas));
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloPenalizacion), R.drawable.penalizacion));
        modulos.add(new MainActivity_Constructor(getString(R.string.moduloConfiguracion), R.drawable.configuracion));
        if (firebaseAuth.getEmail().equals("julio@nervion.com.mx") || firebaseAuth.getEmail().equals("javierb@gmail.com")) {
            modulos.add(new MainActivity_Constructor(getString(R.string.moduloCrear), R.drawable.usuarios));
        }

        MainActivity_Adaptor adapter = new MainActivity_Adaptor(this, modulos);
        recyclerView.setAdapter(adapter);
        layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setOnItemClickListener((v, position) -> {
            int opciones = recyclerView.getChildAdapterPosition(v);
            switch (opciones){
                case 0:
                    startActivity(new Intent(MainActivity.this, EntregasActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(MainActivity.this, InventariosActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(MainActivity.this, BuscadorActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(MainActivity.this, MovimientosActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(MainActivity.this, VigilanciaActivity.class));
                    break;
                case 5:
                    startActivity(new Intent(MainActivity.this, EntinteActivity.class));
                    break;
                case 6:
                    startActivity(new Intent(MainActivity.this, SurtirActivity.class));
                    break;
                case 7:
                    startActivity(new Intent(MainActivity.this, CalidadActivity.class));
                    break;
                case 8:
                    startActivity(new Intent(MainActivity.this, GraficasActivity.class));
                    break;
                case 9:
                    startActivity(new Intent(MainActivity.this, PenalizacionActivity.class));
                    break;
                case 10:
                    alertTienda();
                    break;
                case 11:
                    startActivity(new Intent(MainActivity.this, UsuarioActivity.class));
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Módulo No Seleccionado", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void updateUser() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_usuario_builder, null);
        dialogBuilder.setView(dialogView);
        String areas[] = {"Selecciona su Área:","Almacén","Calidad","Operaciones","Producción","Sistemas","Ventas"};

        /*INICIO CONTENIDO DEL ALERT*/
        Spinner spinnerArea = dialogView.findViewById(R.id.updateArea);
        Spinner spinnerTienda = dialogView.findViewById(R.id.updateTienda);
        Button updateButton = dialogView.findViewById(R.id.updateButton);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, R.layout.configuracion_adapter, areas);
        spinnerArea.setAdapter(arrayAdapter1);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(this, R.layout.configuracion_adapter, tiendas);
        spinnerTienda.setAdapter(arrayAdapter2);
        EditText nombre = dialogView.findViewById(R.id.updateNombre);

        updateButton.setOnClickListener(view -> {
            String updateNombre = nombre.getText().toString();
            String selectArea = spinnerArea.getSelectedItem().toString();
            String selectTienda = spinnerTienda.getSelectedItem().toString();

            if (selectTienda.equals("Selecciona") || updateNombre.equals("") || selectArea.equals("Selecciona su Área:")){
                Toast.makeText(this, "Los campos estan vacíos!", Toast.LENGTH_LONG).show();
            }else {
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(updateNombre).build();
                firebaseAuth.updateProfile(request).addOnSuccessListener(unused -> {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    Map<String, Object> campos = new HashMap<>();
                    String decoded = new String(updateNombre.getBytes(StandardCharsets.UTF_8));
                    campos.put("nombre", decoded);
                    String decodedArea = new String(selectArea.getBytes(StandardCharsets.UTF_8));
                    campos.put("area", decodedArea);
                    campos.put("tiendaOrigen", selectTienda);

                    firestore.collection("usuarios").document(firebaseAuth.getEmail()).set(campos).addOnSuccessListener(documentReference -> {
                        Toast.makeText(MainActivity.this, "Cuenta Actualizada", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        /*ESTE CODIGO PERMITE REINICIAR LA APLICACION SIN PARPADEAR LA ACTIVIDAD QUE ESTA*/
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        /*FIN DEL CODIGO*/
                    });
                }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error: "+e, Toast.LENGTH_LONG).show());
            }
            /*FIN CONTENIDO DEL ALERT*/
        });
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void alertTienda(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.configuracion_activity, null);
        dialogBuilder.setView(dialogView);

        //INICIO CONTENIDO DEL ALERT
        TextView tiendaTV = dialogView.findViewById(R.id.tituloText);
        Spinner spinner = dialogView.findViewById(R.id.tiendaSpinner);
        Button button = dialogView.findViewById(R.id.tiendaButton);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.configuracion_adapter, tiendas);
        spinner.setAdapter(arrayAdapter);

        if(!tiendaGlobal.equals("")){ tiendaTV.setText("Seleccione Tienda: "+tiendaGlobal); }

        button.setOnClickListener(v9 -> {
            String selectTienda = spinner.getSelectedItem().toString();
            if(!selectTienda.equals("Selecciona:")){
                SharedPreferences sharedPreferences = getSharedPreferences("Tienda", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("TiendaGlobal", selectTienda);
                editor.apply();
                alertDialog.dismiss();
                /*ESTE CODIGO PERMITE REINICIAR LA APLICACION SIN PARPADEAR LA ACTIVIDAD QUE ESTA*/
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                /*FIN DEL CODIGO*/
            }else{ Toast.makeText(MainActivity.this, "Tiene que seleccionar una tienda", Toast.LENGTH_SHORT).show(); }
        });
        /*FIN CONTENIDO DEL ALERT*/

        alertDialog = dialogBuilder.create();
        if(tiendaGlobal.equals("")){ alertDialog.setCancelable(false); }
        alertDialog.show();
        /*ESTE alertDialog, es para ampliar un poco mas la vista y asi tener un diseño adecuado*/
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifChannel";
            String description = "Receve Firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("101", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String token = task.getResult();
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/notificationSelect.php?Usuario="+firebaseAuth.getEmail();
            asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        try {
                            final JSONArray jsonArray = new JSONArray(new String(responseBody));
                            String tokenDB = "";
                            for (int i=0; i<jsonArray.length(); i++) {
                                tokenDB = jsonArray.getJSONObject(i).getString("Token");
                            }
                            if (tokenDB.equals("")) { insertToken(token);
                            }else if (!tokenDB.equals(token)) { updateToken(token);
                            }else { Toast.makeText(MainActivity.this, "Token Existente", Toast.LENGTH_SHORT).show(); }
                        }catch (Exception e) {
                            Toast.makeText(MainActivity.this,"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(MainActivity.this,"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void updateToken(String token) {
        try {
            URL url =
                    new URL("https://www.sionm.tech/Aplicacion_DispositivoMovil/notificationUpdate.php?Token=" + token
                            + "&Usuario=" + firebaseAuth.getEmail());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                if (!line.equals("OK")) {
                    Toast.makeText(this, "Error to register data", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Register data Updated", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, connection.getResponseMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void insertToken(String token) {
        try {
            URL url =
                    new URL("https://www.sionm.tech/Aplicacion_DispositivoMovil/notificationInsert.php?Token=" + token
                            + "&NombreCompleto=" + URLEncoder.encode(firebaseAuth.getDisplayName(), "UTF-8")
                            + "&Usuario=" + firebaseAuth.getEmail());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                if (!line.equals("OK")) {
                    Toast.makeText(this, "Error to register data", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Register data Successfully", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, connection.getResponseMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}