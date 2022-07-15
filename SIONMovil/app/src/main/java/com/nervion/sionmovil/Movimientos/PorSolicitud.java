package com.nervion.sionmovil.Movimientos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nervion.sionmovil.R;
import org.json.JSONArray;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import cz.msebera.android.httpclient.Header;

public class PorSolicitud extends Fragment {

    public PorSolicitud() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private String textoActualizar = "";

    private RecyclerView recyclerView;
    private ImageButton msActualizar;

    private EditText msSolicitud;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private String tiendaGlobal, solicitud;
    final String isCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat3 = new SimpleDateFormat("kk:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View movimientos = inflater.inflate(R.layout.fragment_movimientos_solicitud, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = preferences.getString("TiendaGlobal", "");

        recyclerView = movimientos.findViewById(R.id.rvMovimientoSolicitud);
        msSolicitud = movimientos.findViewById(R.id.msSolicitud);
        msActualizar = movimientos.findViewById(R.id.msActualizar);

        msActualizar.setOnClickListener(v -> {
            textoActualizar = "Actualizando...";
            DescargarPorSolicitud(textoActualizar);
        });

        DescargarPorSolicitud(textoActualizar);
        return movimientos;
    }

    private void DescargarPorSolicitud(String textoActualizar) {
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
        String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Movimientos/Mov_Por_Solicitud.php?Observaciones="+msSolicitud.getText().toString()+"&Tienda="+tiendaGlobal;
        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaPorSolicitud(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"No hay conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ListaPorSolicitud(String respuesta) {
        List<PorSolicitud_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                PorSolicitud_Constructor listaMovimientos = new PorSolicitud_Constructor();
                listaMovimientos.setMsRack(jsonArray.getJSONObject(i).getInt("Rack"));
                listaMovimientos.setMsFila(jsonArray.getJSONObject(i).getInt("Fila"));
                listaMovimientos.setMsColumna(jsonArray.getJSONObject(i).getInt("Columna"));
                listaMovimientos.setMsClave(jsonArray.getJSONObject(i).getString("Producto"));
                listaMovimientos.setMsEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaMovimientos.setMsLote(jsonArray.getJSONObject(i).getInt("Lote"));
                listaMovimientos.setMsCantidad(jsonArray.getJSONObject(i).getInt("Cantidad"));
                listaMovimientos.setMsObservaciones(jsonArray.getJSONObject(i).getString("Observaciones"));
                list.add(listaMovimientos);
            }

            PorSolicitud_Adapter adapter = new PorSolicitud_Adapter(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();

            adapter.OnItemLongClickListener((view, position) -> {
                PorSolicitud_Constructor getMovimientos = list.get(position);
                final int psRack = getMovimientos.getMsRack();
                final int psFila = getMovimientos.getMsFila();
                final int psColumna = getMovimientos.getMsColumna();
                final String psProducto = getMovimientos.getMsClave();
                final String psEnvase = getMovimientos.getMsEnvase();
                final String psLote = String.valueOf(getMovimientos.getMsLote());
                final int psCantidad= getMovimientos.getMsCantidad();
                final String psObservaciones = getMovimientos.getMsObservaciones();
                msLongClickBuilder(psRack, psFila, psColumna, psProducto, psEnvase, psLote, psCantidad, psObservaciones);
                return false;
            });
        }catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }

    private void msLongClickBuilder(int psRack, int psFila, int psColumna, String psProducto, String psEnvase, String psLote, int psCantidad,
                                    String psObservaciones) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.por_solicitud_longclick_builder, null);
        dialogBuilder.setView(dialogView);
        //INICIO CONTENIDO DEL ALERT
        Button botonGuardar = dialogView.findViewById(R.id.botonGuardar);
        Button botonEliminar = dialogView.findViewById(R.id.botonEliminar);
        Button botonCancelar = dialogView.findViewById(R.id.botonCancelar);

        EditText msCantidad = dialogView.findViewById(R.id.msCantidadCorregida);
        msCantidad.setText(String.valueOf(psCantidad));

        Date date = Calendar.getInstance().getTime();
        String dateTime = dateFormat1.format(date.getTime());
        String fecha = dateFormat2.format(date.getTime());
        String hora = dateFormat3.format(date.getTime());

        botonGuardar.setOnClickListener(v1 -> {
            String msCantidadCorregida = msCantidad.getText().toString();
            if (!msCantidadCorregida.equals("") && !msCantidadCorregida.equals(String.valueOf(psCantidad))) {
                String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Movimientos/Mov_Por_Solicitud_AgregarLongPress_Guardar.php?Rack="+psRack
                        +"&Fila="+psFila+"&Columna="+psColumna+"&Producto="+psProducto+"&Lote="+psLote+"&Envase="+psEnvase
                        + "&Cantidad="+(psCantidad*-1)+"&Usuario="+isCorreo+"&Observaciones="+psObservaciones+"&Fecha="+fecha
                        +"&Hora="+hora+"&DateTime="+dateTime+"&Observaciones2=Android: Solicitud editada&Consecutivo=0&Tienda="+tiendaGlobal
                        +"&CantidadNueva="+msCantidadCorregida).replaceAll(" ", "%20");
                asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            Toast.makeText(getActivity(), "Se han registrado los datos", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            textoActualizar = "Actualizando...";
                            DescargarPorSolicitud(textoActualizar);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                    }
                });
            }else { Toast.makeText(getActivity(),"Favor de checar la Cantidad!", Toast.LENGTH_LONG).show(); }
        });

        botonEliminar.setOnClickListener(v1 -> {
            String insertUrl = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Movimientos/Mov_Por_Solicitud_AgregarLongPress_Eliminar.php?Rack="+psRack
                    +"&Fila="+psFila+"&Columna="+psColumna+"&Producto="+psProducto+"&Lote="+psLote+"&Envase="+psEnvase
                    + "&Cantidad="+(psCantidad*-1)+"&Usuario="+isCorreo+"&Observaciones="+psObservaciones+"&Fecha="+fecha
                    +"&Hora="+hora+"&DateTime="+dateTime+"&Observaciones2=Android: Partida eliminada solicitud&Consecutivo=0&Tienda="+tiendaGlobal).replaceAll(" ", "%20");
            asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        Toast.makeText(getActivity(), "Se han registrado los datos", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        textoActualizar = "Actualizando...";
                        DescargarPorSolicitud(textoActualizar);
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getActivity(), "NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                }
            });
        });
        botonCancelar.setOnClickListener(view -> alertDialog.dismiss());
        //FIN CONTENIDO DEL ALERT
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}