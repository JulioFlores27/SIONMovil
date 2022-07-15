package com.nervion.sionmovil.Vigilancia;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nervion.sionmovil.Calidad.Calidad_Constructor;
import com.nervion.sionmovil.R;

import org.json.JSONArray;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class EntradasAlmacen extends Fragment {

    public EntradasAlmacen() { /*Required empty public constructor */ }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private final String isCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    private EditText eaFolio, eaNuevaUbicacion;
    private ImageButton eaActualizar;
    private RecyclerView recyclerView;
    private String tiendaGlobal, textoActualizar = "", subResultado;

    private ProgressBar progressBar;
    private AlertDialog alertDialog, tempAD;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat3 = new SimpleDateFormat("kk:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vigilancia = inflater.inflate(R.layout.fragment_entradas_almacen, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = preferences.getString("TiendaGlobal", "");

        eaFolio = vigilancia.findViewById(R.id.eaFolio);
        eaActualizar = vigilancia.findViewById(R.id.eaActualizar);
        recyclerView = vigilancia.findViewById(R.id.rvEntradasAlmacen);

        eaActualizar.setOnClickListener(view -> {
            if (!eaFolio.getText().toString().equals("")){
                textoActualizar = "Actualizando...";
                DescargarEntradasAlmacen(textoActualizar);
            }else { Toast.makeText(getActivity(),"Ingrese el Número de Folio!", Toast.LENGTH_LONG).show(); }
        });
        return vigilancia;
    }

    private void DescargarEntradasAlmacen(String textoActualizar) {
        AlertDialog.Builder alertLoad = new AlertDialog.Builder(getContext());
        LayoutInflater inflaterLoad = this.getLayoutInflater();
        View dialogLoad = inflaterLoad.inflate(R.layout.loading_builder, null);
        alertLoad.setView(dialogLoad);
        progressBar = dialogLoad.findViewById(R.id.simpleProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        TextView textView = dialogLoad.findViewById(R.id.textoCambio);
        if (!textoActualizar.equals("")) { textView.setText(textoActualizar); }
        alertDialog = alertLoad.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();

        String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Vigilancia/Vig_Ent_Almacen.php?Tienda="+tiendaGlobal+"&Folio="+eaFolio.getText();

        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaEntradasAlmacen(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void ListaEntradasAlmacen(String respuesta) {
        List<EntradasAlmacen_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                EntradasAlmacen_Constructor listaVigilancia = new EntradasAlmacen_Constructor();
                listaVigilancia.setEaID(jsonArray.getJSONObject(i).getInt("ID"));
                listaVigilancia.setEaRack(jsonArray.getJSONObject(i).getInt("Rack"));
                listaVigilancia.setEaFila(jsonArray.getJSONObject(i).getInt("Fila"));
                listaVigilancia.setEaCantidad(jsonArray.getJSONObject(i).getInt("Columna"));
                listaVigilancia.setEaProducto(jsonArray.getJSONObject(i).getString("Producto"));
                listaVigilancia.setEaEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaVigilancia.setEaLote(jsonArray.getJSONObject(i).getInt("Lote"));
                listaVigilancia.setEaCantidad(jsonArray.getJSONObject(i).getDouble("Cantidad"));
                listaVigilancia.setEaObservaciones(jsonArray.getJSONObject(i).getString("Observaciones"));
                listaVigilancia.setEaObservaciones2(jsonArray.getJSONObject(i).getString("Observaciones2"));
                listaVigilancia.setEaRecibido(jsonArray.getJSONObject(i).getInt("Recibido"));
                list.add(listaVigilancia);
            }

            EntradasAlmacen_Adapter adapter = new EntradasAlmacen_Adapter(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);

            adapter.setOnItemClickListener((view, position) -> {
                EntradasAlmacen_Constructor getVigilancia = list.get(position);
                final String eaID = String.valueOf(getVigilancia.getEaID());
                final String eaProducto = getVigilancia.getEaProducto();
                final String eaLote = String.valueOf(getVigilancia.getEaLote());
                final String eaCantidad = String.valueOf(getVigilancia.getEaCantidad());
                final String eaEnvase = getVigilancia.getEaEnvase();
                final String eaObservaciones = getVigilancia.getEaObservaciones();
                final String eaObservaciones2 = getVigilancia.getEaObservaciones2();
                final String eaRecibido = String.valueOf(getVigilancia.getEaObservaciones2());

                clickBuilder(eaID, eaProducto, eaLote, eaCantidad, eaEnvase, eaObservaciones, eaObservaciones2);
            });

            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }

    private void clickBuilder(String eaID, String eaProducto, String eaLote, String eaCantidad, String eaEnvase, String eaObservaciones, String eaObservaciones2) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.vigilancia_entradas_almacen_click_builder, null);
        dialogBuilder.setView(dialogView);
        //INICIO CONTENIDO DEL ALERT
        Button botonGuardar = dialogView.findViewById(R.id.botonGuardar);
        Button botonCancelar = dialogView.findViewById(R.id.botonCancelar);
        ImageButton botonCamara = dialogView.findViewById(R.id.botonCamara);

        EditText eaCantidadMovida = dialogView.findViewById(R.id.eaCantidadMovida);
        eaNuevaUbicacion = dialogView.findViewById(R.id.eaNuevaUbicacion);

        eaCantidadMovida.setText(eaCantidad);

        botonCamara.setOnClickListener(view -> Sub_CodigoQR());

        botonGuardar.setOnClickListener(view -> {
            if (!eaCantidadMovida.getText().toString().equals("") && !eaNuevaUbicacion.getText().toString().equals("")) {
                Date date = Calendar.getInstance().getTime();
                String dateTime = dateFormat1.format(date.getTime());
                String fecha = dateFormat2.format(date.getTime());
                String hora = dateFormat3.format(date.getTime());
                String[] separated = eaNuevaUbicacion.getText().toString().split("-");
                String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Vigilancia/Vig_Ent_Almacen_AlertPress.php?Rack="+separated[0]+"&Fila="+separated[1]
                        +"&Columna="+separated[2]+"&Producto="+eaProducto+"&Lote="+eaLote+"&Cantidad="+eaCantidad+"&Usuario="+isCorreo+"&Observaciones="+eaObservaciones
                        +"&Fecha="+fecha+"&Hora="+hora+"&Envase="+eaEnvase+"&Tienda="+tiendaGlobal+"&Observaciones2="+eaObservaciones2
                        +"&DateTime="+dateTime+"&ID="+eaID).replaceAll(" ","%20");

                asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                            textoActualizar = "Actualizando...";
                            alertDialog.dismiss();
                            DescargarEntradasAlmacen(textoActualizar);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                    }
                });
            }else { Toast.makeText(getActivity(),"Favor de checar los compos!", Toast.LENGTH_LONG).show(); }
        });

        botonCancelar.setOnClickListener(view -> alertDialog.dismiss());
        //FIN CONTENIDO DEL ALERT
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void Sub_CodigoQR() {
        AlertDialog.Builder alertLoad = new AlertDialog.Builder(getContext());
        LayoutInflater inflaterLoad = this.getLayoutInflater();
        View dialogLoad = inflaterLoad.inflate(R.layout.camara_qr, null);
        alertLoad.setView(dialogLoad);
        surfaceView = dialogLoad.findViewById(R.id.camera_view);
        TextView textMessage = dialogLoad.findViewById(R.id.textMessage);
        // creo el detector qr
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getContext()).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        // creo la camara
        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector).setRequestedPreviewSize(1600, 1024).setAutoFocusEnabled(true).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                    }
                } else {
                    try { cameraSource.start(surfaceView.getHolder());
                    } catch (IOException ie) { Toast.makeText(getContext(), "Error: " + ie, Toast.LENGTH_LONG).show(); }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() { }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {
                    subResultado = barcodes.valueAt(0).displayValue;
                    String[] separated = subResultado.split("-");
                    if ((separated[0].length() <= 2 && separated[0].length() > 0) && (separated[1].length() <= 2 && separated[1].length() > 0)) {
                        textMessage.post(() -> {
                            eaNuevaUbicacion.setText(subResultado);
                            tempAD.dismiss();
                        });
                    }else { textMessage.setText("Este no es la ubicación"); }
                    barcodeDetector.release();
                }else { textMessage.setText("Error, escanea nuevamente"); }
            }
        });
        tempAD = alertLoad.create();
        tempAD.setCancelable(false);
        tempAD.show();
    }
}