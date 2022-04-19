package com.nervion.sionmovil.Buscador;

import android.Manifest;
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

import android.text.Editable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nervion.sionmovil.R;
import com.nervion.sionmovil.Surtir.SurtirLocal_Adapter;
import com.nervion.sionmovil.Surtir.SurtirLocal_Constructor;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ExistenciasGlobales extends Fragment {

    public ExistenciasGlobales() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private RecyclerView recyclerView;
    private ImageButton egActualizar, egCamara;
    private EditText egProducto, egEnvase;
    private String textoActualizar = "", resultado = "";

    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View buscador = inflater.inflate(R.layout.fragment_buscador_existencias_globales, container, false);

        recyclerView = buscador.findViewById(R.id.rvExistenciasGlobales);
        egProducto = buscador.findViewById(R.id.egProducto);
        egEnvase = buscador.findViewById(R.id.egEnvase);
        egActualizar = buscador.findViewById(R.id.egActualizar);
        egCamara = buscador.findViewById(R.id.egCamara);

        egActualizar.setOnClickListener(v -> {
            textoActualizar = "Actualizando...";
            DescargarExistenciasGlobales(textoActualizar);
        });

        egCamara.setOnClickListener(v -> CodigoQR());

        DescargarExistenciasGlobales(textoActualizar);
        
        return buscador;
    }

    private void CodigoQR() {
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
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException ie) {
                        Toast.makeText(getContext(), "Error: " + ie, Toast.LENGTH_LONG).show();
                    }
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
                    resultado = barcodes.valueAt(0).displayValue;
                    String[] separated = resultado.split("-");
                    if (separated[0].length() == 8 || separated[0].length() == 9) {
                        String isElProducto = separated[0] + "-" + separated[1] + "-" + separated[2];
                        String isElEnvase = separated[3];
                        textMessage.post(() -> {
                            egProducto.setText(isElProducto);
                            egEnvase.setText(isElEnvase);
                            alertDialog.dismiss();
                            DescargarExistenciasGlobales(textoActualizar);
                        });
                    } else {
                        textMessage.setText("Error, escanea nuevamente");
                    }
                } else {
                    textMessage.setText("Error, escanea nuevamente");
                }
            }
        });
        alertDialog = alertLoad.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void DescargarExistenciasGlobales(String textoActualizar) {
        String url;
        if (!egProducto.getText().toString().equals("") && !egEnvase.getText().toString().equals("")) {
            url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Buscador/Bus_Existencias_Globales.php?Producto="+egProducto.getText()
                    +"&Envase="+egEnvase.getText();
        } else { url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Buscador/Bus_Existencias_Globales.php"; }

        AlertDialog.Builder alertLoad = new AlertDialog.Builder(getContext());
        LayoutInflater inflaterLoad = this.getLayoutInflater();
        View dialogLoad = inflaterLoad.inflate(R.layout.loading_builder, null);
        alertLoad.setView(dialogLoad);
        progressBar = dialogLoad.findViewById(R.id.simpleProgressBar);
        TextView textView = dialogLoad.findViewById(R.id.textoCambio);
        if (!textoActualizar.equals("")) {
            textView.setText(textoActualizar);
        }
        progressBar.setVisibility(View.VISIBLE);
        alertDialog = alertLoad.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();

        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaExistenciasGlobales(new String(responseBody)); }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }
        });
    }

    public void ListaExistenciasGlobales(String respuesta) {
        List<ExistenciasGlobales_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                ExistenciasGlobales_Constructor listaSurtir = new ExistenciasGlobales_Constructor();
                listaSurtir.setEgProducto(jsonArray.getJSONObject(i).getString("Producto"));
                listaSurtir.setEgEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaSurtir.setEgCantidad(jsonArray.getJSONObject(i).getInt("Cantidad"));
                listaSurtir.setEgTienda(jsonArray.getJSONObject(i).getString("Tienda"));
                list.add(listaSurtir);
            }

            ExistenciasGlobales_Adapter adapter = new ExistenciasGlobales_Adapter(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }
}