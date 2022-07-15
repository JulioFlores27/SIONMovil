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
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
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

public class InventariosSalidas extends Fragment {

    public InventariosSalidas() { /*Required empty public constructor*/ }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private RecyclerView recyclerView;
    private EditText isNoSalida, isProducto, isEnvase;
    private ImageButton isCamara, isActualizar;
    private AlertDialog alertDialog;
    private String tiendaGlobal, textoActualizar = "", resultado = "", envaseNuevo, isProductoFinal, condicion = "";

    private final String isCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    private ProgressBar progressBar;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat3 = new SimpleDateFormat("kk:mm:ss");

    private Spinner spinner;
    private double resultadoOp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inventarios = inflater.inflate(R.layout.fragment_inventarios_salidas, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = preferences.getString("TiendaGlobal", "");

        recyclerView = inventarios.findViewById(R.id.rvInventariosSalidas);

        /*SECCIÓN DE EDIT TEXT*/
        isNoSalida = inventarios.findViewById(R.id.isNumeroSalida);
        isProducto = inventarios.findViewById(R.id.isProducto);
        isEnvase = inventarios.findViewById(R.id.isEnvase);

        /*SECCIÓN DE IMAGE BUTTONS*/
        isCamara = inventarios.findViewById(R.id.isCamara);
        isActualizar = inventarios.findViewById(R.id.isActualizar);

        isActualizar.setOnClickListener(v -> {
            textoActualizar = "Actualizando...";
            DescargarInventariosSalidas(textoActualizar);
        });

        isCamara.setOnClickListener(v -> CodigoQR());

        DescargarInventariosSalidas(textoActualizar);
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
                    if (separated[0].length() == 5) {
                        String isElLote = separated[0];
                        String isElEnvase = separated[1];
                        isProducto.post(() -> {
                            isProducto.setText(isElLote);
                            alertDialog.dismiss();
                        });
                        isEnvase.post(() -> {
                            isEnvase.setText(isElEnvase);
                            DescargarInventariosSalidas(textoActualizar);
                        });
                    } else if (separated[0].length() == 8 || separated[0].length() == 9) {
                        String isElProducto = separated[0] + "-" + separated[1] + "-" + separated[2];
                        String isElEnvase = separated[3];
                        textMessage.post(() -> {
                            isProducto.setText(isElProducto);
                            isEnvase.setText(isElEnvase);
                            alertDialog.dismiss();
                            DescargarInventariosSalidas(textoActualizar);
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

    private void DescargarInventariosSalidas(String textoActualizar) {
        String url;
        if (!isProducto.getText().toString().equals("") && !isEnvase.getText().toString().equals("")) {
            url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Salidas.php?Tienda=" + tiendaGlobal
                    + "&Producto=" + isProducto.getText() + "&Envase=" + isEnvase.getText();
        } else {
            url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Salidas.php?Tienda=" + tiendaGlobal;
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
                if (statusCode == 200) { ListaInventariosSalidas(new String(responseBody)); }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }
        });
    }

    public void ListaInventariosSalidas(String respuesta) {
        List<InventariosSalidas_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                InventariosSalidas_Constructor listaInventarios = new InventariosSalidas_Constructor();
                listaInventarios.setIsID(jsonArray.getJSONObject(i).getInt("ID"));
                listaInventarios.setIsProducto(jsonArray.getJSONObject(i).getString("MateriaPrima"));
                listaInventarios.setIsEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaInventarios.setIsCantidad(jsonArray.getJSONObject(i).getDouble("Cantidad"));
                listaInventarios.setIsLote(jsonArray.getJSONObject(i).getInt("LoteMP"));
                listaInventarios.setIsRack(jsonArray.getJSONObject(i).getInt("Rack"));
                listaInventarios.setIsFila(jsonArray.getJSONObject(i).getInt("Fila"));
                listaInventarios.setIsColumna(jsonArray.getJSONObject(i).getInt("Columna"));
                list.add(listaInventarios);
            }
            InventariosSalidas_Adaptor adapter = new InventariosSalidas_Adaptor(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();

            adapter.setOnItemClickListener((view, position) -> {
                InventariosSalidas_Constructor getInventarios = list.get(position);
                final String isRack = String.valueOf(getInventarios.getIsRack());
                final String isFila = String.valueOf(getInventarios.getIsFila());
                final String isColumna = String.valueOf(getInventarios.getIsColumna());
                final String isProducto = getInventarios.getIsProducto();
                final String isLote = String.valueOf(getInventarios.getIsLote());
                final String isEnvase = getInventarios.getIsEnvase();
                final Double isCantidad = getInventarios.getIsCantidad();

                if (!isNoSalida.getText().toString().equals("")) {
                    if (isNoSalida.getText().length() > 3) {
                        isClickBuilder(isRack, isFila, isColumna, isProducto, isLote, isEnvase, isCantidad, isNoSalida.getText().toString());
                    } else { Toast.makeText(getActivity(), "Ingresa Número de Salida Correctamente", Toast.LENGTH_SHORT).show(); }
                } else { Toast.makeText(getActivity(), "Número de Salida Vacía", Toast.LENGTH_SHORT).show(); }
            });

            adapter.OnItemLongClickListener((view, position) -> {
                InventariosSalidas_Constructor getInventarios = list.get(position);
                final String isRack = String.valueOf(getInventarios.getIsRack());
                final String isFila = String.valueOf(getInventarios.getIsFila());
                final String isColumna = String.valueOf(getInventarios.getIsColumna());
                final String isProducto = getInventarios.getIsProducto();
                final String isLote = String.valueOf(getInventarios.getIsLote());
                final String isEnvase = getInventarios.getIsEnvase();
                final Double isCantidad = getInventarios.getIsCantidad();

                isLongClickBuilder(isRack, isFila, isColumna, isProducto, isLote, isEnvase, isCantidad);
                return true;
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }

    private void isLongClickBuilder(String isRack, String isFila, String isColumna, final String isProducto, String isLote,
                                    String isEnvase, Double isCantidad) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.inventarios_salidas_longclick_builder, null);
        dialogBuilder.setView(dialogView);
        //INICIO CONTENIDO DEL ALERT
        TextView isProductoOriginal = dialogView.findViewById(R.id.isProductoOriginal);
        String producto1Texto = "<font color=#9E9E9E>Producto Original:</font>";
        String producto2Texto = "<font color=#1565C0>" + isProducto + "</font>";
        isProductoOriginal.setText(Html.fromHtml(producto1Texto + " " + producto2Texto));

        TextView isEnvaseOriginal = dialogView.findViewById(R.id.isEnvaseOriginal);
        String envase1Texto = "<font color=#9E9E9E>Envase Original:</font>";
        String envase2Texto = "<font color=#1565C0>" + isEnvase + "</font>";
        isEnvaseOriginal.setText(Html.fromHtml(envase1Texto + " " + envase2Texto));

        String[] envases = {"Seleciona Envase:", "04", "A1", "A3", "A5", "A7", "A9", "C2", "C3", "C5", "C7", "C9", "D8", "E3", "E5", "E7", "GB", "G3", "G5", "G7", "LL", "W1", "X3", "ZA"};
        spinner = dialogView.findViewById(R.id.isenvaseSpinner);
        spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, envases));
        spinner.setEnabled(false);

        TextView isCantidadConvertida = dialogView.findViewById(R.id.isCantidadConvertida);

        TextView isProductoConvertido = dialogView.findViewById(R.id.isProductoConvertido);
        String productoC1Texto = "<font color=#9E9E9E>Producto Convertido:</font>";
        String productoC2Texto = "<font color=#1565C0>" + isProducto + "</font>";
        isProductoConvertido.setText(Html.fromHtml(productoC1Texto + " " + productoC2Texto));

        EditText isTextoCantidad = dialogView.findViewById(R.id.isTextoCantidad);

        Switch aSwitch = dialogView.findViewById(R.id.isEnviarEntinte);

        Button botonCancelar = dialogView.findViewById(R.id.botonCancelar);
        Button botonTransformar = dialogView.findViewById(R.id.botonTransformar);

        isTextoCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    aSwitch.setEnabled(true);
                    spinner.setEnabled(true);
                    botonTransformar.setEnabled(true);
                } else {
                    aSwitch.setEnabled(false);
                    spinner.setEnabled(false);
                    aSwitch.setChecked(false);
                    spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, envases));
                    botonTransformar.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        final String envaseConvertido = isEnvase;
        aSwitch.setOnClickListener(v -> {
            if (aSwitch.isChecked()) {
                double cantidadConvertido;
                isTextoCantidad.setEnabled(false);
                String divisor[] = isProducto.split("-");
                StringBuilder builder = new StringBuilder();
                String resultProductoConvertido = builder.append("<font color=#1565C0>" + divisor[0] + "-" + "</font>").toString();
                isProductoFinal = divisor[0] + "-";
                condicion = "Entinte";
                isProductoConvertido.setText(Html.fromHtml(productoC1Texto + " " + resultProductoConvertido));
                spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, envases));
                spinner.setEnabled(false);
                if (envaseConvertido.equals("04") || envaseConvertido.equals("E5") || envaseConvertido.equals("4")) {
                    cantidadConvertido = 19.0;
                } else if (envaseConvertido.equals("A1")) {
                    cantidadConvertido = 0.25;
                } else if (envaseConvertido.equals("A3")) {
                    cantidadConvertido = 0.5;
                } else if (envaseConvertido.equals("A5")) {
                    cantidadConvertido = 0.75;
                } else if (envaseConvertido.equals("A7") || envaseConvertido.equals("A9") || envaseConvertido.equals("GB") || envaseConvertido.equals("LL")) {
                    cantidadConvertido = 1.0;
                } else if (envaseConvertido.equals("C2") || envaseConvertido.equals("C3") || envaseConvertido.equals("C9") || envaseConvertido.equals("G7")) {
                    cantidadConvertido = 4.0;
                } else if (envaseConvertido.equals("C5") || envaseConvertido.equals("X3")) {
                    cantidadConvertido = 3.0;
                } else if (envaseConvertido.equals("C7")) {
                    cantidadConvertido = 15.0;
                } else if (envaseConvertido.equals("D8")) {
                    cantidadConvertido = 10.0;
                } else if (envaseConvertido.equals("E3")) {
                    cantidadConvertido = 16.0;
                } else if (envaseConvertido.equals("E7")) {
                    cantidadConvertido = 20.0;
                } else if (envaseConvertido.equals("G3")) {
                    cantidadConvertido = 2.0;
                } else if (envaseConvertido.equals("G5")) {
                    cantidadConvertido = 5.0;
                } else if (envaseConvertido.equals("W1")) {
                    cantidadConvertido = 7.0;
                } else if (envaseConvertido.equals("ZA")) {
                    cantidadConvertido = 7.5;
                } else { cantidadConvertido = 0.0; }

                double cantidadAConvertir = Double.parseDouble(String.valueOf(isTextoCantidad.getText()));
                String cantidadC1Texto = "<font color=#9E9E9E>Cantidad Convertida:</font>";
                String cantidadC2Texto = "<font color=#1565C0>" + (cantidadAConvertir * cantidadConvertido) + "</font>";

                resultadoOp = cantidadAConvertir * cantidadConvertido;
                isCantidadConvertida.setText(Html.fromHtml(cantidadC1Texto + " " + cantidadC2Texto));
            } else {
                isTextoCantidad.setEnabled(true);
                isProductoFinal = isProducto;
                isProductoConvertido.setText(Html.fromHtml(productoC1Texto + " " + productoC2Texto));
                spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, envases));
                spinner.setEnabled(true);
                isCantidadConvertida.setText(Html.fromHtml("<font color=#9E9E9E>Cantidad Convertida:</font>"));
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String envaseSeleccionada = parent.getItemAtPosition(position).toString();
                if (!envaseSeleccionada.equals("Seleciona Envase:")) {
                    double cantidadConvertido = 0.0, cantidadPredeterminada = 0.0;
                    if (envaseSeleccionada.equals("04") || envaseSeleccionada.equals("E5") || envaseSeleccionada.equals("4")) {
                        cantidadConvertido = 19.0;
                    } else if (envaseSeleccionada.equals("A1")) {
                        cantidadConvertido = 0.25;
                    } else if (envaseSeleccionada.equals("A3")) {
                        cantidadConvertido = 0.5;
                    } else if (envaseSeleccionada.equals("A5")) {
                        cantidadConvertido = 0.75;
                    } else if (envaseSeleccionada.equals("A7") || envaseSeleccionada.equals("A9") || envaseSeleccionada.equals("GB") || envaseSeleccionada.equals("LL")) {
                        cantidadConvertido = 1.0;
                    } else if (envaseSeleccionada.equals("C2") || envaseSeleccionada.equals("C3") || envaseSeleccionada.equals("C9") || envaseSeleccionada.equals("G7")) {
                        cantidadConvertido = 4.0;
                    } else if (envaseSeleccionada.equals("C5") || envaseSeleccionada.equals("X3")) {
                        cantidadConvertido = 3.0;
                    } else if (envaseSeleccionada.equals("C7")) {
                        cantidadConvertido = 15.0;
                    } else if (envaseSeleccionada.equals("D8")) {
                        cantidadConvertido = 10.0;
                    } else if (envaseSeleccionada.equals("E3")) {
                        cantidadConvertido = 16.0;
                    } else if (envaseSeleccionada.equals("E7")) {
                        cantidadConvertido = 20.0;
                    } else if (envaseSeleccionada.equals("G3")) {
                        cantidadConvertido = 2.0;
                    } else if (envaseSeleccionada.equals("G5")) {
                        cantidadConvertido = 5.0;
                    } else if (envaseSeleccionada.equals("W1")) {
                        cantidadConvertido = 7.0;
                    } else if (envaseSeleccionada.equals("ZA")) {
                        cantidadConvertido = 7.5;
                    }

                    if (envaseConvertido.equals("04") || envaseConvertido.equals("E5") || envaseConvertido.equals("4")) {
                        cantidadPredeterminada = 19.0;
                    } else if (envaseConvertido.equals("A1")) {
                        cantidadPredeterminada = 0.25;
                    } else if (envaseConvertido.equals("A3")) {
                        cantidadPredeterminada = 0.5;
                    } else if (envaseConvertido.equals("A5")) {
                        cantidadPredeterminada = 0.75;
                    } else if (envaseConvertido.equals("A7") || envaseConvertido.equals("A9") || envaseConvertido.equals("GB") || envaseConvertido.equals("LL")) {
                        cantidadPredeterminada = 1.0;
                    } else if (envaseConvertido.equals("C2") || envaseConvertido.equals("C3") || envaseConvertido.equals("C9") || envaseConvertido.equals("G7")) {
                        cantidadPredeterminada = 4.0;
                    } else if (envaseConvertido.equals("C5") || envaseConvertido.equals("X3")) {
                        cantidadPredeterminada = 3.0;
                    } else if (envaseConvertido.equals("C7")) {
                        cantidadPredeterminada = 15.0;
                    } else if (envaseConvertido.equals("D8")) {
                        cantidadPredeterminada = 10.0;
                    } else if (envaseConvertido.equals("E3")) {
                        cantidadPredeterminada = 16.0;
                    } else if (envaseConvertido.equals("E7")) {
                        cantidadPredeterminada = 20.0;
                    } else if (envaseConvertido.equals("G3")) {
                        cantidadPredeterminada = 2.0;
                    } else if (envaseConvertido.equals("G5")) {
                        cantidadPredeterminada = 5.0;
                    } else if (envaseConvertido.equals("W1")) {
                        cantidadPredeterminada = 7.0;
                    } else if (envaseConvertido.equals("ZA")) { cantidadPredeterminada = 7.5; }

                    envaseNuevo = spinner.getSelectedItem().toString();

                    double cantidadAConvertir = Double.parseDouble(String.valueOf(isTextoCantidad.getText()));
                    resultadoOp = ((cantidadAConvertir * cantidadPredeterminada) / cantidadConvertido);
                    String cantidadC1Texto = "<font color=#9E9E9E>Cantidad Convertida:</font>";
                    isCantidadConvertida.setText(Html.fromHtml(cantidadC1Texto +" "+ resultadoOp));
                }
            }

            public void onNothingSelected(AdapterView<?> parent) { }
        });

        botonTransformar.setOnClickListener(v2 -> {
            double cantidadAConvertir = Double.parseDouble(String.valueOf(isTextoCantidad.getText()));
            if (cantidadAConvertir <= isCantidad && cantidadAConvertir >= 0.00) {
                if (resultadoOp > 0.00) {
                    Date date = Calendar.getInstance().getTime();
                    String dateTime = dateFormat1.format(date.getTime());
                    String fecha = dateFormat2.format(date.getTime());
                    String hora = dateFormat3.format(date.getTime());

                    if (envaseNuevo == null){ envaseNuevo = "LL"; }

                    String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Salidas_AgregarLongPress.php?Rack="+isRack
                            +"&Fila="+isFila+"&Columna="+isColumna+"&Producto="+isProducto+"&Lote="+isLote+"&Envase="+isEnvase
                            +"&Cantidad=-"+cantidadAConvertir+"&Usuario="+isCorreo+"&Observaciones=Reenvasado Entinte&Fecha="+fecha
                            +"&Hora="+hora+"&DateTime="+dateTime+"&Observaciones2=Android&Consecutivo=0&Tienda="+tiendaGlobal
                            +"&ProductoNuevo="+isProductoFinal+"&CantidadNuevo="+resultadoOp+"&EnvaseNuevo="+envaseNuevo
                            +"&Condicion="+condicion).replaceAll(" ", "%20");

                    asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                Toast.makeText(getActivity(), "Se han registrado los datos", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                textoActualizar = "Actualizando...";
                                DescargarInventariosSalidas(textoActualizar);
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                        }
                    });
                }else { Toast.makeText(getActivity(), "La cantidad es nula!", Toast.LENGTH_SHORT).show(); }
            }else { Toast.makeText(getActivity(), "La cantidad es incorrecta!", Toast.LENGTH_SHORT).show(); }
        });

        botonCancelar.setOnClickListener(v2 -> alertDialog.dismiss());
        //FIN CONTENIDO DEL ALERT
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void isClickBuilder(String isRack, String isFila, String isColumna, String isProducto, String isLote,
                                String isEnvase, Double isCantidad, String isNoSalida) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.inventarios_salidas_builder, null);
        dialogBuilder.setView(dialogView);

        //INICIO CONTENIDO DEL ALERT
        TextView isCantidadColor = dialogView.findViewById(R.id.isCantidad);
        EditText isTextoCantidad = dialogView.findViewById(R.id.isTextoCantidad);

        isTextoCantidad.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) { isCantidadColor.setTextColor(Color.parseColor("#003C8F"));
            } else { isCantidadColor.setTextColor(Color.parseColor("#CFCFCF")); }
        });

        Button botonGuardar = dialogView.findViewById(R.id.botonGuardar);
        ImageButton botonEscanear = dialogView.findViewById(R.id.botonEscanear);
        Button botonCancelar = dialogView.findViewById(R.id.botonCancelar);

        botonCancelar.setOnClickListener(v -> alertDialog.dismiss());

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

                        isTextoCantidad.setEnabled(false);

                        botonEscanear.setVisibility(View.VISIBLE);
                        botonEscanear.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            CodigoQRInsert(isRack, isFila, isColumna, isProducto, isLote, isEnvase, isCantidad, isNoSalida, paCantidadEscaneada, paCantidad);
                        });
                        botonCancelar.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            textoActualizar = "Actualizando...";
                            DescargarInventariosSalidas(textoActualizar);
                        });
                    } catch (Exception e) {
                        botonGuardar.setVisibility(View.VISIBLE);
                        botonGuardar.setOnClickListener(v1 -> {
                            String isCantidadSalida = isTextoCantidad.getText().toString();
                            if (!isCantidadSalida.equals("")) {
                                if (0 < Double.parseDouble(isCantidadSalida) && Double.parseDouble(isCantidadSalida) <= isCantidad) {
                                    Date date = Calendar.getInstance().getTime();
                                    String dateTime = dateFormat1.format(date.getTime());
                                    String fecha = dateFormat2.format(date.getTime());
                                    String hora = dateFormat3.format(date.getTime());

                                    String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Salidas_AgregarPress.php?Rack=" + isRack
                                            + "&Fila=" + isFila + "&Columna=" + isColumna + "&Producto=" + isProducto + "&Lote=" + isLote + "&Envase=" + isEnvase
                                            + "&Cantidad=-" + isCantidadSalida + "&Usuario=" + isCorreo + "&Observaciones=" + isNoSalida + "&Fecha=" + fecha
                                            + "&Hora=" + hora + "&DateTime=" + dateTime + "&Observaciones2=Android&Consecutivo=0&Tienda=" + tiendaGlobal).replaceAll(" ", "%20");

                                    asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            if (statusCode == 200) {
                                                Toast.makeText(getActivity(), "Se han registrado los datos", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                                textoActualizar = "Actualizando...";
                                                DescargarInventariosSalidas(textoActualizar);
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
        //FIN CONTENIDO DEL ALERT
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void CodigoQRInsert(String isRack, String isFila, String isColumna, String isProducto, String isLote,
                                String isEnvase, Double isCantidad, String isNoSalida, int paCantidadEscaneada, int paCantidad) {
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
                        String isElProducto = separated[0] + "-" + separated[1] + "-" + separated[2];
                        String isElEnvase = separated[3];
                        String isElLote = separated[4];
                        textMessage.post(() -> {
                            if (isElProducto.equals(isProducto) && isElEnvase.equals(isEnvase) && isElLote.equals(isLote)) {
                                Date date = Calendar.getInstance().getTime();
                                String dateTime = dateFormat1.format(date.getTime());
                                String fecha = dateFormat2.format(date.getTime());
                                String hora = dateFormat3.format(date.getTime());

                                String insertUrls = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Salidas_AgregarPress.php?Rack=" + isRack
                                        + "&Fila=" + isFila + "&Columna=" + isColumna + "&Producto=" + isProducto + "&Lote=" + isLote + "&Envase=" + isEnvase
                                        + "&Cantidad=-1&Usuario=" + isCorreo + "&Observaciones=" + isNoSalida + "&Fecha=" + fecha
                                        + "&Hora=" + hora + "&DateTime=" + dateTime + "&Observaciones2=Android: Escaneo&Consecutivo=0&Tienda=" + tiendaGlobal).replaceAll(" ", "%20");

                                int resultado = paCantidadEscaneada + 1;
                                String insertUrlsTwo = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Penalizacion/PenalizacionAlmacen_AplicarInventariosSalidas.php?"
                                        + "UsuarioPenalizado=" + isCorreo + "&CantidadEscaneada=" + resultado).replaceAll(" ", "%20");

                                asyncHttpClient.post(insertUrls, new AsyncHttpResponseHandler() {
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
                                                                        DescargarInventariosSalidas(textoActualizar);
                                                                    }
                                                                }
                                                                @Override
                                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                    Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }else if(isCantidad == 0){
                                                            alertDialog.dismiss();
                                                            textoActualizar = "No hay producto...";
                                                            DescargarInventariosSalidas(textoActualizar);
                                                        }else {
                                                            Toast.makeText(getActivity(), "Se ha registrado el dato", Toast.LENGTH_SHORT).show();
                                                            alertDialog.dismiss();
                                                            isClickBuilder(isRack, isFila, isColumna, isProducto, isLote, isEnvase, (isCantidad - 1), isNoSalida);
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
                            } else {
                                textMessage.setText("No coincide el producto");
                            }
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