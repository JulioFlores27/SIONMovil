package com.nervion.sionmovil.Entregas;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nervion.sionmovil.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class EntregasActivity extends FragmentActivity implements OnMapReadyCallback {

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private String latitud;
    private String longitud;
    final String isCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat3 = new SimpleDateFormat("kk:mm:ss");

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                //Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                latitud = String.valueOf(currentLocation.getLatitude());
                longitud = String.valueOf(currentLocation.getLongitude());
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapaEntregas);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(EntregasActivity.this);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Estoy aquí!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entregas_activity);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();

        EditText eRemision = findViewById(R.id.eRemision);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(view -> {
            if(eRemision.getText().length() == 4 || eRemision.getText().length() == 5) {
                Date date = Calendar.getInstance().getTime();
                String dateTime = dateFormat1.format(date.getTime());
                String fecha = dateFormat2.format(date.getTime());
                String hora = dateFormat3.format(date.getTime());

                String url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Entregas/Entregas_Agregar.php?Solicitud="+eRemision.getText()
                        +"&Nombre_Entrega="+isCorreo+"&Latitud="+latitud+"&Longitud="+longitud+"&Fecha="+fecha+"&Hora="+hora
                        +"&DateTime="+dateTime).replace(" ", "%20");
                AsyncHttpClient localAsyncHttpClient = new AsyncHttpClient();
                localAsyncHttpClient.post(url, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(EntregasActivity.this, "Se han registrado los datos!", Toast.LENGTH_SHORT).show();
                        eRemision.setText("");
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(EntregasActivity.this, "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                    }
                });
            }else { Toast.makeText(EntregasActivity.this, "El número de remisión tiene que ser un rango de 4 o 5 numeros!", Toast.LENGTH_LONG).show(); }
        });
    }
}