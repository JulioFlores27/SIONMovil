package com.nervion.sionmovil.Movimientos;

import android.app.DatePickerDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nervion.sionmovil.R;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MovimientosPorProducto extends Fragment {

    public MovimientosPorProducto() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private String textoActualizar = "";

    private RecyclerView recyclerView;
    private ImageButton mpActualizar;

    private EditText mpFechaInicial, mpFechaFinal, mpProducto, mpEnvase;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    private String tiendaGlobal;

    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    final Calendar myCalendar= Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View movimientos = inflater.inflate(R.layout.fragment_movimientos_producto, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = preferences.getString("TiendaGlobal", "");

        recyclerView = movimientos.findViewById(R.id.rvMovimientoProducto);
        mpFechaInicial = movimientos.findViewById(R.id.mpFechaInicial);
        mpFechaFinal = movimientos.findViewById(R.id.mpFechaFinal);
        mpProducto = movimientos.findViewById(R.id.mpProducto);
        mpEnvase = movimientos.findViewById(R.id.mpEnvase);
        mpActualizar = movimientos.findViewById(R.id.mpActualizar);

        DatePickerDialog.OnDateSetListener date1 = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            mpFechaInicial.setText(dateFormat2.format(myCalendar.getTime()));
        };
        DatePickerDialog.OnDateSetListener date2 = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            mpFechaFinal.setText(dateFormat2.format(myCalendar.getTime()));
        };
        mpFechaInicial.setOnLongClickListener(view -> {
            mpFechaInicial.setText("");
            return false;
        });
        mpFechaFinal.setOnLongClickListener(view -> {
            mpFechaFinal.setText("");
            return false;
        });
        mpFechaInicial.setOnClickListener(view -> new DatePickerDialog(getContext(),date1,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        mpFechaFinal.setOnClickListener(view -> new DatePickerDialog(getContext(),date2, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        mpActualizar.setOnClickListener(v -> {
            textoActualizar = "Actualizando...";
            DescargarMovimientosPorProducto(textoActualizar);
        });

        DescargarMovimientosPorProducto(textoActualizar);
        return movimientos;
    }

    private void DescargarMovimientosPorProducto(String textoActualizar) {
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
        String url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Movimientos/Mov_Por_Producto.php?FechaInicial="+mpFechaInicial.getText().toString()
                +"&FechaFinal="+mpFechaFinal.getText().toString()+"&Producto="+mpProducto.getText().toString()
                +"&Envase="+mpEnvase.getText().toString()+"&Tienda="+tiendaGlobal).replace(" ","%20");
        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaMovimientosPorProducto(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"No hay conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ListaMovimientosPorProducto(String respuesta) {
        List<MovimientosPorProducto_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                MovimientosPorProducto_Constructor listaMovimientos = new MovimientosPorProducto_Constructor();
                listaMovimientos.setMpClave(jsonArray.getJSONObject(i).getString("Producto"));
                listaMovimientos.setMpEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaMovimientos.setMpCantidad(jsonArray.getJSONObject(i).getInt("Cantidad"));
                listaMovimientos.setMpLote(jsonArray.getJSONObject(i).getInt("Lote"));
                listaMovimientos.setMpObservaciones(jsonArray.getJSONObject(i).getString("Observaciones"));
                listaMovimientos.setMpFecha(jsonArray.getJSONObject(i).getString("Fecha"));
                listaMovimientos.setMpHora(jsonArray.getJSONObject(i).getString("Hora"));
                listaMovimientos.setMpUsuario(jsonArray.getJSONObject(i).getString("Usuario"));
                list.add(listaMovimientos);
            }

            MovimientosPorProducto_Adapter adapter = new MovimientosPorProducto_Adapter(getContext(), list);
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