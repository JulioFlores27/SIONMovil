package com.nervion.sionmovil.Surtir;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SurtirLocal extends Fragment {

    public SurtirLocal() { /*Required empty public constructor*/ }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private RecyclerView recyclerView;
    private ImageButton slActualizar;
    private String textoActualizar = "", slSolicitud = "", resultado = "";

    private SurfaceView surfaceView;
    private CameraSource cameraSource;

    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private String tiendaGlobal;
    final String isCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat3 = new SimpleDateFormat("kk:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View surtir = inflater.inflate(R.layout.fragment_surtir_local, container, false);
        SharedPreferences preferences = this.getActivity().getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = preferences.getString("TiendaGlobal", "");

        recyclerView = surtir.findViewById(R.id.rvSurtirLocal);
        final EditText slSalida = surtir.findViewById(R.id.slSalida);
        slActualizar = surtir.findViewById(R.id.slActualizar);

        slActualizar.setOnClickListener(v -> {
            if(!slSalida.equals("")){
                textoActualizar = "Actualizando...";
                slSolicitud =  slSalida.getText().toString();
                DescargarSurtirLocal(textoActualizar, slSalida.getText().toString());
            }else { Toast.makeText(getContext(),"Numero de Salida esta Vacía!", Toast.LENGTH_SHORT).show(); }
        });

        return surtir;
    }

    private void DescargarSurtirLocal(String textoActualizartextoActualizar, String slSalida) {
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
        String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Surtir/Sur_Local_Remisiones.php?Solicitud="+slSalida+"&Tienda="+tiendaGlobal;
        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaSurtirLocal(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"No hay conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ListaSurtirLocal(String respuesta) {
        List<SurtirLocal_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                SurtirLocal_Constructor listaSurtir = new SurtirLocal_Constructor();
                listaSurtir.setSlPartida(jsonArray.getJSONObject(i).getString("Partida"));
                listaSurtir.setSlRack(jsonArray.getJSONObject(i).getInt("Rack"));
                listaSurtir.setSlFila(jsonArray.getJSONObject(i).getInt("Fila"));
                listaSurtir.setSlColumna(jsonArray.getJSONObject(i).getInt("Columna"));
                listaSurtir.setSlClave(jsonArray.getJSONObject(i).getString("Producto"));
                listaSurtir.setSlEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaSurtir.setSlCantidadSolicitada(jsonArray.getJSONObject(i).getInt("CantidadSolicitada"));
                listaSurtir.setSlCantidadPendiente(jsonArray.getJSONObject(i).getInt("CantidadSurtida"));
                listaSurtir.setSlLote(jsonArray.getJSONObject(i).getInt("Lote"));
                listaSurtir.setSlCantidad(jsonArray.getJSONObject(i).getInt("CantidadDisponible"));
                list.add(listaSurtir);
            }

            SurtirLocal_Adapter adapter = new SurtirLocal_Adapter(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();

            adapter.setOnItemClickListener((view, position) -> {
                SurtirLocal_Constructor getSurtir = list.get(position);
                final int slRack = getSurtir.getSlRack();
                final int slFila = getSurtir.getSlFila();
                final int slColumna = getSurtir.getSlColumna();
                final String slProducto = getSurtir.getSlClave();
                final String slEnvase = getSurtir.getSlEnvase();
                final String slLote = String.valueOf(getSurtir.getSlLote());
                final int slCantidadSolicitada = getSurtir.getSlCantidadSolicitada();

                final int slResultado= getSurtir.getSlCantidadSolicitada()+getSurtir.getSlCantidadPendiente();
                if (slResultado != 0) {
                    slClickBuilder(slRack, slFila, slColumna, slProducto, slLote, slEnvase, slCantidadSolicitada);
                }else { Toast.makeText(getActivity(),"Partida Completa", Toast.LENGTH_SHORT).show(); }
            });
        }catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }

    private void slClickBuilder(int slRack, int slFila, int slColumna, String slProducto, String slLote, String slEnvase, int slCantidadSolicitada) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.surtir_local_click_builder, null);
        dialogBuilder.setView(dialogView);
        //INICIO CONTENIDO DEL ALERT
        Button botonGuardar = dialogView.findViewById(R.id.botonGuardar);
        ImageButton botonEscanear = dialogView.findViewById(R.id.botonEscanear);
        Button botonCancelar = dialogView.findViewById(R.id.botonCancelar);

        TextView slCantidadColor = dialogView.findViewById(R.id.isCantidad);
        EditText slTextoCantidad = dialogView.findViewById(R.id.isTextoCantidad);

        slTextoCantidad.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) { slCantidadColor.setTextColor(Color.parseColor("#003C8F"));
            } else { slCantidadColor.setTextColor(Color.parseColor("#CFCFCF")); }
        });

        String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Penalizacion/PenalizacionAlmacen_InventariosSalidas.php?Usuario="+isCorreo;
        AsyncHttpClient localAsyncHttpClient = new AsyncHttpClient();
        localAsyncHttpClient.post(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String respuesta = new String(responseBody);
                        if (statusCode == 200) {
                            try {
                                final JSONArray jsonArray = new JSONArray(respuesta);
                                int paCantidad = jsonArray.getJSONObject(0).getInt("Cantidad");
                                int paCantidadEscaneada = jsonArray.getJSONObject(0).getInt("CantidadEscaneada");

                                slTextoCantidad.setEnabled(false);

                                botonEscanear.setVisibility(View.VISIBLE);
                                botonEscanear.setOnClickListener(v -> {
                                    alertDialog.dismiss();
                                    CodigoQRInsert(slRack, slFila, slColumna, slProducto, slLote, slEnvase, paCantidad, paCantidadEscaneada, slCantidadSolicitada);
                                });
                                botonCancelar.setOnClickListener(v -> {
                                    alertDialog.dismiss();
                                    textoActualizar = "Actualizando...";
                                    DescargarSurtirLocal(textoActualizar, slSolicitud);
                                });
                            } catch (Exception e) {
                                botonGuardar.setVisibility(View.VISIBLE);
                                botonGuardar.setOnClickListener(v1 -> {
                                    String slCantidadSalida = slTextoCantidad.getText().toString();
                                    if (!slCantidadSalida.equals("")) {
                                        if (0 < Double.parseDouble(slCantidadSalida) && Double.parseDouble(slCantidadSalida) <= slCantidadSolicitada) {
                                            Date date = Calendar.getInstance().getTime();
                                            String dateTime = dateFormat1.format(date.getTime());
                                            String fecha = dateFormat2.format(date.getTime());
                                            String hora = dateFormat3.format(date.getTime());

                                            String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Surtir/Sur_Local_AlertPress.php?Rack=" + slRack
                                                    + "&Fila=" + slFila + "&Columna=" + slColumna + "&Producto=" + slProducto + "&Lote=" + slLote + "&Envase=" + slEnvase
                                                    + "&Cantidad=-" + slCantidadSalida + "&Usuario=" + isCorreo + "&Observaciones=" + slSolicitud + "&Fecha=" + fecha
                                                    + "&Hora=" + hora + "&DateTime=" + dateTime + "&Observaciones2=Android&Consecutivo=0&Tienda=" + tiendaGlobal).replaceAll(" ", "%20");

                                            asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    if (statusCode == 200) {
                                                        Toast.makeText(getActivity(), "Se han registrado los datos", Toast.LENGTH_SHORT).show();
                                                        alertDialog.dismiss();
                                                        textoActualizar = "Actualizando...";
                                                        DescargarSurtirLocal(textoActualizar, slSolicitud);
                                                    }
                                                }
                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }else { Toast.makeText(getActivity(), "Cantidad Incorrecta", Toast.LENGTH_LONG).show(); }
                                    }else { Toast.makeText(getActivity(), "El campo esta vacío", Toast.LENGTH_LONG).show(); }
                                });
                            }
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getActivity(), "No hay conexión", Toast.LENGTH_SHORT).show();
                    }
                });

        botonCancelar.setOnClickListener(view -> alertDialog.dismiss());
        //FIN CONTENIDO DEL ALERT
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    private void CodigoQRInsert(int slRack, int slFila, int slColumna, String slProducto, String slLote, String slEnvase, int paCantidad, int paCantidadEscaneada, int slCantidadSolicitada) {
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
            public void surfaceDestroyed(SurfaceHolder holder) { cameraSource.stop(); }
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
                        String isElEnvase = separated[1];
                    } else if (separated[0].length() == 8 || separated[0].length() == 9) {
                        String slElProducto = separated[0] + "-" + separated[1] + "-" + separated[2];
                        String slElEnvase = separated[3];
                        String slElLote = separated[4];
                        textMessage.post(() -> {
                            if (slElProducto.equals(slProducto) && slElEnvase.equals(slEnvase) && slElLote.equals(slLote)) {
                                Date date = Calendar.getInstance().getTime();
                                String dateTime = dateFormat1.format(date.getTime());
                                String fecha = dateFormat2.format(date.getTime());
                                String hora = dateFormat3.format(date.getTime());

                                String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Surtir/Sur_Local_AlertPress.php?Rack=" + slRack
                                        + "&Fila=" + slFila + "&Columna=" + slColumna + "&Producto=" + slProducto + "&Lote=" + slLote + "&Envase=" + slEnvase
                                        + "&Cantidad=-1&Usuario=" + isCorreo + "&Observaciones=" + slSolicitud + "&Fecha=" + fecha
                                        + "&Hora=" + hora + "&DateTime=" + dateTime + "&Observaciones2=Android: Escaneo Surtir&Consecutivo=0&Tienda=" + tiendaGlobal).replaceAll(" ", "%20");

                                int resultado = paCantidadEscaneada + 1;
                                String insertUrlsTwo = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Penalizacion/PenalizacionAlmacen_AplicarInventariosSalidas.php?"
                                        + "UsuarioPenalizado=" + isCorreo + "&CantidadEscaneada=" + resultado).replaceAll(" ", "%20");

                                asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        if (statusCode == 200) {
                                            asyncHttpClient.post(insertUrlsTwo, new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    if (statusCode == 200) {
                                                        int finalResultado = paCantidad;
                                                        if(finalResultado == resultado){
                                                            String insertUrlsThree = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Penalizacion/PenalizacionAlmacen_AplicarInventariosSalidas.php?"
                                                                    + "UsuarioPenalizado=" + isCorreo + "&Terminado=Finalizado").replaceAll(" ", "%20");
                                                            asyncHttpClient.post(insertUrlsThree, new AsyncHttpResponseHandler() {
                                                                @Override
                                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                    if (statusCode == 200) {
                                                                        Toast.makeText(getActivity(), "Se ha registrado el dato", Toast.LENGTH_SHORT).show();
                                                                        alertDialog.dismiss();
                                                                        textoActualizar = "Actualizando...";
                                                                        DescargarSurtirLocal(textoActualizar, slSolicitud);
                                                                    }
                                                                }
                                                                @Override
                                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                    Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }else if(slCantidadSolicitada == 0){
                                                            alertDialog.dismiss();
                                                            textoActualizar = "No hay producto...";
                                                            DescargarSurtirLocal(textoActualizar, slSolicitud);
                                                        }else {
                                                            Toast.makeText(getActivity(), "Se ha registrado el dato", Toast.LENGTH_SHORT).show();
                                                            alertDialog.dismiss();
                                                            slClickBuilder(slRack, slFila, slColumna, slProducto, slLote, slEnvase, (slCantidadSolicitada - 1));
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else { textMessage.setText("No coincide el producto"); }
                        });
                    }else { textMessage.setText("Error, escanea nuevamente"); }
                    barcodeDetector.release();
                }else { textMessage.setText("Esperando Código QR"); }
            }
        });
        alertDialog = alertLoad.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
