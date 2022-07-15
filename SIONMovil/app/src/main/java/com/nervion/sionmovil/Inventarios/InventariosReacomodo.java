package com.nervion.sionmovil.Inventarios;

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

public class InventariosReacomodo extends Fragment {

    public InventariosReacomodo() { /*Required empty public constructor*/ }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private RecyclerView recyclerView;
    private EditText irProducto, irNuevaUbicacion;
    private ImageButton irCamara, irActualizar;
    private AlertDialog alertDialog, tempAD;
    private String tiendaGlobal, textoActualizar = "", resultado = "", subResultado = "";
    private final String isCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    private ProgressBar progressBar;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat3 = new SimpleDateFormat("kk:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inventarios = inflater.inflate(R.layout.fragment_inventarios_reacomodo, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = preferences.getString("TiendaGlobal", "");

        recyclerView = inventarios.findViewById(R.id.rvInventariosReacomodo);

        /*SECCIÓN DE EDIT TEXT*/
        irProducto = inventarios.findViewById(R.id.irProducto);

        /*SECCIÓN DE IMAGE BUTTONS*/
        irCamara = inventarios.findViewById(R.id.irCamara);
        irActualizar = inventarios.findViewById(R.id.irActualizar);

        irActualizar.setOnClickListener(v -> {
            textoActualizar = "Actualizando...";
            DescargarInventariosReacomodo(textoActualizar);
        });

        irCamara.setOnClickListener(v -> CodigoQR());

        DescargarInventariosReacomodo(textoActualizar);

        return inventarios;
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
                    resultado = barcodes.valueAt(0).displayValue;
                    String[] separated = resultado.split("-");
                    if (separated[0].length() == 5) {
                        String isElLote = separated[0];
                        textMessage.post(() -> {
                            irProducto.setText(isElLote);
                            alertDialog.dismiss();
                        });
                    } else if (separated[0].length() == 8 || separated[0].length() == 9) {
                        String isElProducto = separated[0] + "-" + separated[1] + "-" + separated[2];
                        textMessage.post(() -> {
                            irProducto.setText(isElProducto);
                            alertDialog.dismiss();
                            DescargarInventariosReacomodo(textoActualizar);
                        });
                    } else { textMessage.setText("Error, escanea nuevamente"); }
                    barcodeDetector.release();
                } else { textMessage.setText("Error, escanea nuevamente"); }
            }
        });
        alertDialog = alertLoad.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void DescargarInventariosReacomodo(String textoActualizar) {
        String url;
        if (!irProducto.getText().toString().equals("")) {
            url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Reacomodo.php?Tienda=" + tiendaGlobal
                    + "&Producto=" + irProducto.getText();
        } else {
            url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Reacomodo.php?Tienda=" + tiendaGlobal;
        }

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
                if (statusCode == 200) { ListaInventariosReacomodo(new String(responseBody)); }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }
        });
    }

    public void ListaInventariosReacomodo(String respuesta) {
        List<InventariosReacomodo_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                InventariosReacomodo_Constructor listaInventarios = new InventariosReacomodo_Constructor();
                listaInventarios.setIrProducto(jsonArray.getJSONObject(i).getString("MateriaPrima"));
                listaInventarios.setIrEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaInventarios.setIrCantidad(jsonArray.getJSONObject(i).getDouble("Cantidad"));
                listaInventarios.setIrLote(jsonArray.getJSONObject(i).getInt("LoteMP"));
                listaInventarios.setIrRack(jsonArray.getJSONObject(i).getInt("Rack"));
                listaInventarios.setIrFila(jsonArray.getJSONObject(i).getInt("Fila"));
                listaInventarios.setIrColumna(jsonArray.getJSONObject(i).getInt("Columna"));
                list.add(listaInventarios);
            }
            InventariosReacomodo_Adapter adapter = new InventariosReacomodo_Adapter(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();

            adapter.setOnItemClickListener((view, position) -> {
                InventariosReacomodo_Constructor getInventarios = list.get(position);
                final String irRack = String.valueOf(getInventarios.getIrRack());
                final String irFila = String.valueOf(getInventarios.getIrFila());
                final String irColumna = String.valueOf(getInventarios.getIrColumna());
                final String irProducto = getInventarios.getIrProducto();
                final String irLote = String.valueOf(getInventarios.getIrLote());
                final String irEnvase = getInventarios.getIrEnvase();
                final Double irCantidad = getInventarios.getIrCantidad();
                isClickBuilder(irRack, irFila, irColumna, irProducto, irLote, irEnvase, irCantidad);
            });

        } catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }

    private void isClickBuilder(String irRack, String irFila, String irColumna, String irProducto, String irLote, String irEnvase, Double irCantidad) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.inventarios_reacomodo_click_builder, null);
        dialogBuilder.setView(dialogView);
        //INICIO CONTENIDO DEL ALERT
        Button botonGuardar = dialogView.findViewById(R.id.botonGuardar);
        Button botonCancelar = dialogView.findViewById(R.id.botonCancelar);
        ImageButton botonCamara = dialogView.findViewById(R.id.botonCamara);

        EditText irCantidadMovida = dialogView.findViewById(R.id.irCantidadMovida);
        irNuevaUbicacion = dialogView.findViewById(R.id.irNuevaUbicacion);

        irCantidadMovida.setText(irCantidad.toString());

        botonCamara.setOnClickListener(view1 -> Sub_CodigoQR());

        botonGuardar.setOnClickListener(view -> {
            Date date = Calendar.getInstance().getTime();
            String dateTime = dateFormat1.format(date.getTime());
            String fecha = dateFormat2.format(date.getTime());
            String hora = dateFormat3.format(date.getTime());

            String[] separated = irNuevaUbicacion.getText().toString().split("-");

            for(int i=0; i<separated.length; i++) {
                if (i == 2) {
                    if (!separated[0].equals("") && !separated[1].equals("")  && !separated[2].equals("")) {
                        if (separated[0].length() <= 2 && separated[0].length() >= 0) {
                            if (separated[1].length() <= 2 && separated[1].length() >= 0) {
                                if (separated[2].length() <= 2 && separated[2].length() >= 0) {
                                    String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Reacomodo_AgregarPress.php?Rack="+irRack
                                            +"&Fila="+irFila+"&Columna="+irColumna+"&Producto="+irProducto +"&Lote="+irLote+"&Envase="+irEnvase
                                            +"&Cantidad=-"+irCantidadMovida.getText()+"&Usuario="+isCorreo+"&Observaciones=Reacomodo&Fecha="+fecha +"&Hora="+hora
                                            +"&DateTime="+dateTime+"&Observaciones2=Android&Consecutivo=0&Tienda="+tiendaGlobal+"&RackNuevo="+separated[0]
                                            +"&FilaNueva="+separated[1]+"&ColumnaNueva="+separated[2]
                                            +"&CantidadNueva="+irCantidadMovida.getText()).replaceAll(" ", "%20");

                                    asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            if (statusCode == 200) {
                                                Toast.makeText(getActivity(), "Se han registrado los datos", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                                alertDialog.dismiss();
                                                textoActualizar = "Actualizando...";
                                                DescargarInventariosReacomodo(textoActualizar);
                                            }
                                        }
                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else { Toast.makeText(getActivity(), "Este no es la ubicación", Toast.LENGTH_LONG).show(); }
                            }else { Toast.makeText(getActivity(), "Este no es la ubicación", Toast.LENGTH_LONG).show(); }
                        }else { Toast.makeText(getActivity(), "Este no es la ubicación", Toast.LENGTH_LONG).show(); }
                    }else { Toast.makeText(getActivity(), "Este no es la ubicación", Toast.LENGTH_LONG).show(); }
                }
            }
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
                            irNuevaUbicacion.setText(subResultado);
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