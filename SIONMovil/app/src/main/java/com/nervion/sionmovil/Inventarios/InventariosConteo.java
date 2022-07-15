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

public class InventariosConteo extends Fragment {

    public InventariosConteo() { /*Required empty public constructor*/ }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private RecyclerView recyclerView;
    private EditText icUbicacion, icProducto, icLote, icCantidad, icEnvase;
    private ImageButton icCamara, icActualizar;
    private Button icAgregar , icFinUbicacion;
    private AlertDialog alertDialog;
    private String tiendaGlobal, textoActualizar = "", botonResultado = "", resultado = "";

    private final String isCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    private ProgressBar progressBar;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat3 = new SimpleDateFormat("kk:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inventarios = inflater.inflate(R.layout.fragment_inventarios_conteo, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = preferences.getString("TiendaGlobal", "");

        recyclerView = inventarios.findViewById(R.id.rvInventariosConteo);

        /*SECCIÓN DE EDIT TEXT*/
        icUbicacion = inventarios.findViewById(R.id.icUbicacion);
        icProducto = inventarios.findViewById(R.id.icProducto);
        icLote = inventarios.findViewById(R.id.icLote);
        icEnvase = inventarios.findViewById(R.id.icEnvase);
        icCantidad = inventarios.findViewById(R.id.icCantidad);

        /*SECCIÓN DE IMAGE BUTTONS*/
        icCamara = inventarios.findViewById(R.id.icCamara);
        icActualizar = inventarios.findViewById(R.id.icActualizar);

        /*SECCIÓN DE BUTTONS*/
        icAgregar = inventarios.findViewById(R.id.icAgregar);
        icFinUbicacion = inventarios.findViewById(R.id.icFinUbicacion);

        icActualizar.setOnClickListener(v -> {
            textoActualizar = "Actualizando...";
            DescargarInventariosConteo(textoActualizar);
        });

        icCamara.setOnClickListener(v -> CodigoQR());

        DescargarInventariosConteo(textoActualizar);

        icAgregar.setOnClickListener(v -> {
            if (!icUbicacion.getText().toString().equals("") && !icProducto.getText().toString().equals("") && !icLote.getText().toString().equals("")
                    && !icCantidad.getText().toString().equals("") && !icEnvase.getText().toString().equals("")) {

                Date date = Calendar.getInstance().getTime();
                String dateTime = dateFormat1.format(date.getTime());
                String fecha = dateFormat2.format(date.getTime());
                String hora = dateFormat3.format(date.getTime());

                String[] separated = icUbicacion.getText().toString().split("-");
                if ((separated[0].length() <= 2 && separated[0].length() >= 0) && (separated[1].length() <= 2 && separated[1].length() >= 0)) {
                    String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Conteo_Agregar_AgregarFinUbicacion.php?Rack="+separated[0]
                            +"&Fila="+separated[1]+"&Columna="+separated[2]+"&Producto="+icProducto.getText().toString() +"&Lote="+icLote.getText().toString()
                            +"&Envase="+icEnvase.getText().toString() +"&Cantidad="+icCantidad.getText().toString() +"&Usuario="+isCorreo
                            +"&Observaciones=Conteo&Fecha="+fecha +"&Hora="+hora+"&DateTime="+dateTime
                            +"&Observaciones2=Android&Consecutivo=0&Tienda="+tiendaGlobal).replaceAll(" ", "%20");

                    asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                Toast.makeText(getActivity(), "Se han registrado los datos", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                textoActualizar = "Actualizando...";
                                botonResultado = "Conteo";
                                icProducto.setText("");
                                icLote.setText("");
                                icCantidad.setText("");
                                icEnvase.setText("");
                                DescargarInventariosConteo(textoActualizar);
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                        }
                    });
                }else { Toast.makeText(getActivity(), "Este no es la ubicación", Toast.LENGTH_LONG).show(); }
            }else {
                StringBuilder oracion = new StringBuilder("Campos Vacios:\n");
                if (icUbicacion.getText().toString().equals("")){ oracion.append("Ubicación\n"); }
                if (icProducto.getText().toString().equals("")){ oracion.append("Materia Prima\n"); }
                if (icLote.getText().toString().equals("")){ oracion.append("No. de Lote\n"); }
                if (icCantidad.getText().toString().equals("")){ oracion.append("Cantidad\n"); }
                if (icEnvase.getText().toString().equals("")){ oracion.append("Envase"); }
                Toast.makeText(getActivity(), oracion, Toast.LENGTH_LONG).show();
            }
        });

        icFinUbicacion.setOnClickListener(v -> {
            if (!icUbicacion.getText().toString().equals("") && !icProducto.getText().toString().equals("") && !icLote.getText().toString().equals("")
                    && !icCantidad.getText().toString().equals("") && !icEnvase.getText().toString().equals("")) {

                Date date = Calendar.getInstance().getTime();
                String dateTime = dateFormat1.format(date.getTime());
                String fecha = dateFormat2.format(date.getTime());
                String hora = dateFormat3.format(date.getTime());

                String[] separated = icUbicacion.getText().toString().split("-");
                if ((separated[0].length() <= 2 && separated[0].length() >= 0) && (separated[1].length() <= 2 && separated[1].length() >= 0)) {
                    String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Conteo_Agregar_AgregarFinUbicacion.php?Rack="+separated[0]
                            +"&Fila="+separated[1]+"&Columna="+separated[2]+"&Producto="+icProducto.getText().toString() +"&Lote="+icLote.getText().toString()
                            +"&Envase="+icEnvase.getText().toString() +"&Cantidad="+icCantidad.getText().toString() +"&Usuario="+isCorreo
                            +"&Observaciones=Conteo&Fecha="+fecha +"&Hora="+hora+"&DateTime="+dateTime
                            +"&Observaciones2=Android&Consecutivo=0&Tienda="+tiendaGlobal).replaceAll(" ", "%20");

                    asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                Toast.makeText(getActivity(), "Se han registrado los datos", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                textoActualizar = "Actualizando...";
                                icUbicacion.setText("");
                                icProducto.setText("");
                                icLote.setText("");
                                icCantidad.setText("");
                                icEnvase.setText("");
                                DescargarInventariosConteo(textoActualizar);
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                        }
                    });
                }else { Toast.makeText(getActivity(), "Este no es la ubicación", Toast.LENGTH_LONG).show(); }
            }else {
                StringBuilder oracion = new StringBuilder("Campos Vacios:\n");
                if (icUbicacion.getText().toString().equals("")){ oracion.append("Ubicación\n"); }
                if (icProducto.getText().toString().equals("")){ oracion.append("Materia Prima\n"); }
                if (icLote.getText().toString().equals("")){ oracion.append("No. de Lote\n"); }
                if (icCantidad.getText().toString().equals("")){ oracion.append("Cantidad\n"); }
                if (icEnvase.getText().toString().equals("")){ oracion.append("Envase"); }
                Toast.makeText(getActivity(), oracion, Toast.LENGTH_LONG).show();
            }
        });

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
                    if ((separated[0].length() <= 2 && separated[0].length() >= 0) && (separated[1].length() <= 2 && separated[1].length() >= 0)) {
                        textMessage.post(() -> {
                            icUbicacion.setText(resultado);
                            alertDialog.dismiss();
                            textoActualizar = "Actualizando...";
                            DescargarInventariosConteo(textoActualizar);
                        });
                    }else { textMessage.setText("Este no es la ubicación"); }
                    barcodeDetector.release();
                }else { textMessage.setText("Error, escanea nuevamente"); }
            }
        });
        alertDialog = alertLoad.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void DescargarInventariosConteo(String textoActualizar) {
        String url;
        if (!botonResultado.equals("")) {
            String[] laUbicacion = icUbicacion.getText().toString().split("-");
            if (!textoActualizar.equals("Actualizando...")) {
                url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Conteo.php?Tienda="+tiendaGlobal
                        +"&Rack="+laUbicacion[0]+"&Fila=-"+laUbicacion[1]+"&Columna=-"+laUbicacion[2]
                        +"&Producto="+icProducto.getText()+"&Lote="+icLote.getText()
                        +"&Envase="+icEnvase.getText()).replaceAll("[- ]","");
            }else {
                if ((laUbicacion[0].length() <= 2 && laUbicacion[0].length() >= 0) && (laUbicacion[1].length() <= 2 && laUbicacion[1].length() >= 0)) {
                    url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Conteo.php?Tienda="+tiendaGlobal
                            +"&Rack="+laUbicacion[0]+"&Fila=-"+laUbicacion[1]+"&Columna=-"+laUbicacion[2]
                            +"&Producto="+icProducto.getText()+"&Lote="+icLote.getText()
                            +"&Envase="+icEnvase.getText()).replaceAll(" ","");
                }else {
                    url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Conteo.php?Tienda="+tiendaGlobal
                            +"&Rack="+laUbicacion[0]+"&Fila=-"+laUbicacion[1]+"&Columna=-"+laUbicacion[2]
                            +"&Producto="+icProducto.getText()+"&Lote="+icLote.getText()
                            +"&Envase="+icEnvase.getText()).replaceAll("[- ]","");
                }
                botonResultado = "";
            }
        }else {
            if (!icUbicacion.getText().toString().equals("")) {
                String[] laUbicacion = icUbicacion.getText().toString().split("-");
                if (!textoActualizar.equals("Actualizando...")) {
                    url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Conteo.php?Tienda="+tiendaGlobal
                            +"&Rack="+laUbicacion[0]+"&Fila=-"+laUbicacion[1]+"&Columna=-"+laUbicacion[2]).replaceAll("[- ]","");
                }else {
                    url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Conteo.php?Tienda="+tiendaGlobal
                            +"&Rack="+laUbicacion[0]+"&Fila=-"+laUbicacion[1]+"&Columna=-"+laUbicacion[2]).replaceAll(" ","");
                }
            }else { url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Conteo.php?Tienda=" + tiendaGlobal; }
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
                if (statusCode == 200) { ListaInventariosConteo(new String(responseBody)); }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }
        });
    }

    public void ListaInventariosConteo(String respuesta) {
        List<InventariosConteo_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                InventariosConteo_Constructor listaInventarios = new InventariosConteo_Constructor();
                listaInventarios.setIcProducto(jsonArray.getJSONObject(i).getString("MateriaPrima"));
                listaInventarios.setIcEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaInventarios.setIcCantidad(jsonArray.getJSONObject(i).getDouble("Cantidad"));
                listaInventarios.setIcLote(jsonArray.getJSONObject(i).getInt("LoteMP"));
                listaInventarios.setIcRack(jsonArray.getJSONObject(i).getInt("Rack"));
                listaInventarios.setIcFila(jsonArray.getJSONObject(i).getInt("Fila"));
                listaInventarios.setIcColumna(jsonArray.getJSONObject(i).getInt("Columna"));
                list.add(listaInventarios);
            }
            InventariosConteo_Adapter adapter = new InventariosConteo_Adapter(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();

            adapter.OnItemLongClickListener((view, position) -> {
                InventariosConteo_Constructor getInventarios = list.get(position);
                final String icRack = String.valueOf(getInventarios.getIcRack());
                final String icFila = String.valueOf(getInventarios.getIcFila());
                final String icColumna = String.valueOf(getInventarios.getIcColumna());
                final String icProducto = getInventarios.getIcProducto();
                final String icLote = String.valueOf(getInventarios.getIcLote());
                final String icEnvase = getInventarios.getIcEnvase();
                final Double icCantidad = getInventarios.getIcCantidad();
                isLongClickBuilder(icRack, icFila, icColumna, icProducto, icLote, icEnvase, icCantidad);
                return true;
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }

    private void isLongClickBuilder(String icRack, String icFila, String icColumna, String icProducto, String icLote, String icEnvase, Double icCantidad) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.inventarios_conteo_longclick_builder, null);
        dialogBuilder.setView(dialogView);
        //INICIO CONTENIDO DEL ALERT
        Button botonGuardar = dialogView.findViewById(R.id.botonGuardar);
        Button botonEliminar = dialogView.findViewById(R.id.botonEliminar);
        Button botonCancelar = dialogView.findViewById(R.id.botonCancelar);

        EditText claveCorregida = dialogView.findViewById(R.id.icClaveCorregida);
        EditText cantidadCorregida = dialogView.findViewById(R.id.icCantidadCorregida);
        EditText ubicacionCorregida = dialogView.findViewById(R.id.icUbicacionCorregida);
        EditText loteCorregida = dialogView.findViewById(R.id.icLoteCorregida);

        claveCorregida.setText(icProducto);
        cantidadCorregida.setText(icCantidad.toString());
        ubicacionCorregida.setText(icRack+"-"+icFila+"-"+icColumna);
        loteCorregida.setText(icLote);

        botonEliminar.setOnClickListener(view -> {
            Date date = Calendar.getInstance().getTime();
            String dateTime = dateFormat1.format(date.getTime());
            String fecha = dateFormat2.format(date.getTime());
            String hora = dateFormat3.format(date.getTime());
            String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Conteo_AgregarLongPress_Eliminar.php?Rack="+icRack
                    +"&Fila="+icFila+"&Columna="+icColumna+"&Producto="+icProducto +"&Lote="+icLote+"&Envase="+icEnvase+"&Cantidad=-"+icCantidad
                    +"&Usuario="+isCorreo+"&Observaciones=Eliminado conteo&Fecha="+fecha +"&Hora="+hora+"&DateTime="+dateTime
                    +"&Observaciones2=Android&Consecutivo=0&Tienda="+tiendaGlobal).replaceAll(" ", "%20");

            asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        Toast.makeText(getActivity(), "Se ha eliminado los datos", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        textoActualizar = "Actualizando...";
                        DescargarInventariosConteo(textoActualizar);
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                }
            });
        });

        botonGuardar.setOnClickListener(view -> {
            if (!claveCorregida.getText().toString().equals(icProducto) || !cantidadCorregida.getText().toString().equals(icCantidad.toString())
                    || !ubicacionCorregida.getText().toString().equals(icRack+"-"+icFila+"-"+icColumna) || !loteCorregida.getText().toString().equals(icLote) ) {
                if (!claveCorregida.getText().toString().equals("") && !cantidadCorregida.getText().toString().equals("")
                        && !ubicacionCorregida.getText().toString().equals("") && !loteCorregida.getText().toString().equals("")) {

                    Date date = Calendar.getInstance().getTime();
                    String dateTime = dateFormat1.format(date.getTime());
                    String fecha = dateFormat2.format(date.getTime());
                    String hora = dateFormat3.format(date.getTime());

                    String[] separated = ubicacionCorregida.getText().toString().split("-");
                    for(int i=0; i<separated.length; i++) {
                        if (i == 2) {
                            if (!separated[0].equals("") && !separated[1].equals("")  && !separated[2].equals("")) {
                                if (separated[0].length() <= 2 && separated[0].length() >= 0) {
                                    if (separated[1].length() <= 2 && separated[1].length() >= 0) {
                                        if (separated[2].length() <= 2 && separated[2].length() >= 0) {
                                            String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Conteo_AgregarLongPress_Guardar.php?Rack="+icRack
                                                    +"&Fila="+icFila+"&Columna="+icColumna+"&Producto="+icProducto +"&Lote="+icLote+"&Envase="+icEnvase+"&Cantidad=-"+icCantidad
                                                    +"&Usuario="+isCorreo+"&Observaciones=Editado conteo&Fecha="+fecha +"&Hora="+hora+"&DateTime="+dateTime
                                                    +"&Observaciones2=Android&Consecutivo=0&Tienda="+tiendaGlobal+"&RackNuevo="+separated[0]+"&FilaNueva="+separated[1]
                                                    +"&ColumnaNueva="+separated[2]+"&ProductoNuevo="+claveCorregida.getText().toString()+"&LoteNuevo="+loteCorregida.getText().toString()
                                                    +"&CantidadNueva="+cantidadCorregida.getText().toString()).replaceAll(" ", "%20");

                                            asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    if (statusCode == 200) {
                                                        Toast.makeText(getActivity(), "Se han registrado los datos", Toast.LENGTH_SHORT).show();
                                                        alertDialog.dismiss();
                                                        textoActualizar = "Actualizando...";
                                                        DescargarInventariosConteo(textoActualizar);
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
                }else {
                    StringBuilder oracion = new StringBuilder("Campos Vacios:\n");
                    if (claveCorregida.getText().toString().equals("")){ oracion.append("Ubicación\n"); }
                    if (cantidadCorregida.getText().toString().equals("")){ oracion.append("Materia Prima\n"); }
                    if (ubicacionCorregida.getText().toString().equals("")){ oracion.append("No. de Lote\n"); }
                    if (loteCorregida.getText().toString().equals("")){ oracion.append("Cantidad\n"); }
                    Toast.makeText(getActivity(), oracion, Toast.LENGTH_LONG).show();
                }
            }else { Toast.makeText(getActivity(),"Todos los datos coincide!", Toast.LENGTH_LONG).show(); }
        });

        botonCancelar.setOnClickListener(view -> alertDialog.dismiss());
        //FIN CONTENIDO DEL ALERT
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}