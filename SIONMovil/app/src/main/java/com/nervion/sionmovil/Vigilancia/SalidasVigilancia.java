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
import com.nervion.sionmovil.Entinte.EntinteHistorial_Adapter;
import com.nervion.sionmovil.Entinte.EntinteHistorial_Constructor;
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

public class SalidasVigilancia extends Fragment {

    public SalidasVigilancia() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private final String isCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    private EditText svFolio, svPedido, svProducto, svLote, svEnvase, svCantidad;
    private ImageButton svActualizar, svCamara;
    private Button svAgregar, svTerminar;
    private RecyclerView recyclerView;
    private String tiendaGlobal, textoActualizar = "", resultado;

    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat3 = new SimpleDateFormat("kk:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vigilancia = inflater.inflate(R.layout.fragment_salidas_vigilancia, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = preferences.getString("TiendaGlobal", "");

        svFolio = vigilancia.findViewById(R.id.svFolio);
        svPedido = vigilancia.findViewById(R.id.svPedido);
        svProducto = vigilancia.findViewById(R.id.svProducto);
        svLote = vigilancia.findViewById(R.id.svLote);
        svEnvase = vigilancia.findViewById(R.id.svEnvase);
        svCantidad = vigilancia.findViewById(R.id.svCantidad);

        svActualizar = vigilancia.findViewById(R.id.svActualizar);
        svCamara = vigilancia.findViewById(R.id.svCamara);
        svAgregar = vigilancia.findViewById(R.id.svAgregar);
        svTerminar = vigilancia.findViewById(R.id.svTerminar);
        recyclerView = vigilancia.findViewById(R.id.rvSalidasVigilancia);

        svActualizar.setOnClickListener(view -> {
            textoActualizar = "Actualizando...";
            DescargarSalidasVigilancia(textoActualizar);
        });

        svCamara.setOnClickListener(view -> CodigoQR());

        svAgregar.setOnClickListener(view -> {
            if (!svFolio.getText().toString().equals("") && svFolio.length() >= 5) {
                if (!svPedido.getText().toString().equals("") && svPedido.length() >= 5) {
                    if (!svProducto.getText().toString().equals("")) {
                        if (!svLote.getText().toString().equals("") && svLote.length() >= 5) {
                            if (!svEnvase.getText().toString().equals("") && svEnvase.length() == 2) {
                                if (!svCantidad.getText().toString().equals("")) {
                                    Date date = Calendar.getInstance().getTime();
                                    String dateTime = dateFormat1.format(date.getTime());
                                    String fecha = dateFormat2.format(date.getTime());
                                    String hora = dateFormat3.format(date.getTime());
                                    String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Vigilancia/Vig_Sal_Vigilancia_AgregarTerminar.php?Producto="
                                            +svProducto.getText()+"&Lote="+svLote.getText()+"&Cantidad="+svCantidad.getText()+"&Envase="+svEnvase.getText()
                                            +"&Usuario="+isCorreo+"&Folio="+svFolio.getText()+"&Fecha="+fecha+"&Hora="+hora+"&Tienda="+tiendaGlobal
                                            +"&Pedido="+svPedido.getText()+"&DateTime="+dateTime).replaceAll(" ","%20");

                                    asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            if (statusCode == 200) {
                                                Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                                                svPedido.setText("");
                                                svProducto.setText("");
                                                svLote.setText("");
                                                svEnvase.setText("");
                                                svCantidad.setText("");
                                                textoActualizar = "Actualizando...";
                                                DescargarSalidasVigilancia(textoActualizar);
                                            }
                                        }
                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else { Toast.makeText(getActivity(),"Favor de ingresar un valor!", Toast.LENGTH_LONG).show(); }
                            }else { Toast.makeText(getActivity(),"Favor de checar el campo de Envase!", Toast.LENGTH_LONG).show(); }
                        }else { Toast.makeText(getActivity(),"Favor de checar el campo de Lote!", Toast.LENGTH_LONG).show(); }
                    }else { Toast.makeText(getActivity(),"Favor de checar el campo de Producto!", Toast.LENGTH_LONG).show(); }
                }else { Toast.makeText(getActivity(),"Favor de checar el campo de Pedido!", Toast.LENGTH_LONG).show(); }
            }else { Toast.makeText(getActivity(),"Favor de checar el campo de Folio!", Toast.LENGTH_LONG).show(); }
        });

        svTerminar.setOnClickListener(view -> {
            if (!svFolio.getText().toString().equals("") && svFolio.length() >= 5) {
                if (!svPedido.getText().toString().equals("") && svPedido.length() >= 5) {
                    if (!svProducto.getText().toString().equals("")) {
                        if (!svLote.getText().toString().equals("") && svLote.length() >= 5) {
                            if (!svEnvase.getText().toString().equals("") && svEnvase.length() == 2) {
                                if (!svCantidad.getText().toString().equals("")) {
                                    Date date = Calendar.getInstance().getTime();
                                    String dateTime = dateFormat1.format(date.getTime());
                                    String fecha = dateFormat2.format(date.getTime());
                                    String hora = dateFormat3.format(date.getTime());
                                    String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Vigilancia/Vig_Sal_Vigilancia_AgregarTerminar.php?Producto="
                                            +svProducto.getText()+"&Lote="+svLote.getText()+"&Cantidad="+svCantidad.getText()+"&Envase="+svEnvase.getText()
                                            +"&Usuario="+isCorreo+"&Folio="+svFolio.getText()+"&Fecha="+fecha+"&Hora="+hora+"&Tienda="+tiendaGlobal
                                            +"&Pedido="+svPedido.getText()+"&DateTime="+dateTime).replaceAll(" ","%20");

                                    asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            if (statusCode == 200) {
                                                Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                                                svPedido.setText("");
                                                svProducto.setText("");
                                                svLote.setText("");
                                                svEnvase.setText("");
                                                svCantidad.setText("");
                                                svFolio.setText("");
                                                textoActualizar = "Actualizando...";
                                                DescargarSalidasVigilancia(textoActualizar);
                                            }
                                        }
                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else { Toast.makeText(getActivity(),"Favor de ingresar un valor!", Toast.LENGTH_LONG).show(); }
                            }else { Toast.makeText(getActivity(),"Favor de checar el campo de Envase!", Toast.LENGTH_LONG).show(); }
                        }else { Toast.makeText(getActivity(),"Favor de checar el campo de Lote!", Toast.LENGTH_LONG).show(); }
                    }else { Toast.makeText(getActivity(),"Favor de checar el campo de Producto!", Toast.LENGTH_LONG).show(); }
                }else { Toast.makeText(getActivity(),"Favor de checar el campo de Pedido!", Toast.LENGTH_LONG).show(); }
            }else { Toast.makeText(getActivity(),"Favor de checar el campo de Folio!", Toast.LENGTH_LONG).show(); }
        });

        DescargarSalidasVigilancia(textoActualizar);
        return vigilancia;
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
                        String esProducto = separated[0] + "-" + separated[1] + "-" + separated[2];
                        String esEnvase = separated[3];
                        String esLote = separated[4];
                        textMessage.post(() -> {
                            svProducto.setText(esProducto);
                            svEnvase.setText(esEnvase);
                            svLote.setText(esLote);
                            alertDialog.dismiss();
                        });
                    } else { textMessage.setText("Error, escanea nuevamente"); }
                } else { textMessage.setText("Error, escanea nuevamente"); }
            }
        });
        alertDialog = alertLoad.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void DescargarSalidasVigilancia(String textoActualizar) {
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

        String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Vigilancia/Vig_Sal_Vigilancia.php?Tienda="+tiendaGlobal+"&Folio="+svFolio.getText();

        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaSalidasVigilancia(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void ListaSalidasVigilancia(String respuesta) {
        List<SalidasVigilancia_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                SalidasVigilancia_Constructor listaVigilancia = new SalidasVigilancia_Constructor();
                listaVigilancia.setSvFolio(jsonArray.getJSONObject(i).getString("Observaciones"));
                listaVigilancia.setSvProducto(jsonArray.getJSONObject(i).getString("Producto"));
                listaVigilancia.setSvFecha(jsonArray.getJSONObject(i).getString("Fecha"));
                listaVigilancia.setSvPedido(jsonArray.getJSONObject(i).getString("Observaciones2"));
                listaVigilancia.setSvEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaVigilancia.setSvCantidad(jsonArray.getJSONObject(i).getDouble("Cantidad"));
                listaVigilancia.setSvLote(jsonArray.getJSONObject(i).getInt("Lote"));
                listaVigilancia.setSvHora(jsonArray.getJSONObject(i).getString("Hora"));
                list.add(listaVigilancia);
            }

            SalidasVigilancia_Adapter adapter = new SalidasVigilancia_Adapter(getContext(), list);
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