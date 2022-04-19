package com.nervion.sionmovil.Inventarios;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class InventariosExistencias extends Fragment {

    public InventariosExistencias() { /*Required empty public constructor*/ }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private RecyclerView recyclerView;
    private EditText ieProducto, ieEnvase, ieFecha, ieHora;
    private ImageButton ieActualizar;
    private AlertDialog alertDialog;
    private String tiendaGlobal, textoActualizar = "";

    private ProgressBar progressBar;

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

    final Calendar myCalendar= Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inventarios = inflater.inflate(R.layout.fragment_inventarios_existencias, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = preferences.getString("TiendaGlobal", "");

        /*SECCIÓN DE EDIT TEXT*/
        ieProducto = inventarios.findViewById(R.id.ieProducto);
        ieEnvase = inventarios.findViewById(R.id.ieEnvase);
        ieFecha = inventarios.findViewById(R.id.ieFecha);
        ieHora = inventarios.findViewById(R.id.ieHora);

        /*SECCIÓN DE IMAGE BUTTONS*/
        ieActualizar = inventarios.findViewById(R.id.ieActualizar);

        recyclerView = inventarios.findViewById(R.id.rvInventariosExistencias);

        DatePickerDialog.OnDateSetListener date = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            ieFecha.setText(dateFormat2.format(myCalendar.getTime()));
        };

        ieFecha.setOnLongClickListener(view -> {
            ieFecha.setText("");
            return false;
        });
        ieFecha.setOnClickListener(view -> new DatePickerDialog(getContext(),date,
                myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        ieHora.setOnLongClickListener(view -> {
            ieHora.setText("");
            return false;
        });
        ieHora.setOnClickListener(view -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(),
                    (timePicker, selectedHour, selectedMinute) -> ieHora.setText( selectedHour + ":" + selectedMinute), hour, minute, true);
            mTimePicker.setTitle("Selecciona Hora");
            mTimePicker.show();
        });

        ieActualizar.setOnClickListener(view -> {
            textoActualizar = "Actualizando...";
            DescargarInventariosExistencias(textoActualizar);
        });

        DescargarInventariosExistencias(textoActualizar);
        return inventarios;
    }

    private void DescargarInventariosExistencias(String textoActualizar) {
        Date date = Calendar.getInstance().getTime();
        String url;
        if (!ieProducto.getText().toString().equals("") || !ieEnvase.getText().toString().equals("")
                ||  !ieFecha.getText().toString().equals("") || !ieHora.getText().toString().equals("")) {
            if(!ieFecha.getText().toString().equals("") && !ieHora.getText().toString().equals("")) {
                String fecha[] = ieFecha.getText().toString().split("/");
                String fechaCompleta = fecha[2]+"-"+fecha[1]+"-"+fecha[0]+" "+ieHora.getText()+":00";
                url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Existencias_Otra_Fecha.php?Tienda="+tiendaGlobal
                        +"&Producto="+ieProducto.getText()+"&Envase="+ieEnvase.getText()+"&DateTime="+fechaCompleta).replaceAll(" ", "%20");
            }else if(!ieFecha.getText().toString().equals("") && ieHora.getText().toString().equals("")) {
                String fecha[] = ieFecha.getText().toString().split("/");
                String fechaUnicamente= fecha[2]+"-"+fecha[1]+"-"+fecha[0]+" 00:00:00";
                url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Existencias_Otra_Fecha.php?Tienda="+tiendaGlobal
                        +"&Producto="+ieProducto.getText()+"&Envase="+ieEnvase.getText()+"&DateTime="+fechaUnicamente).replaceAll(" ", "%20");
            }else if(ieFecha.getText().toString().equals("") && !ieHora.getText().toString().equals("")) {
                DateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd");
                String fechaDinamica = dateTime.format(date.getTime());
                String tiempoUnicamnete = fechaDinamica+" "+ieHora.getText()+":00";
                url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Existencias_Otra_Fecha.php?Tienda="+tiendaGlobal
                        +"&Producto="+ieProducto.getText()+"&Envase="+ieEnvase.getText()+"&DateTime="+tiempoUnicamnete).replaceAll(" ", "%20");
            }else{
                String dateTime = dateFormat1.format(date.getTime());
                url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Existencias_Otra_Fecha.php?Tienda="+tiendaGlobal
                        +"&Producto="+ieProducto.getText()+"&Envase="+ieEnvase.getText()+"&DateTime="+dateTime).replaceAll(" ", "%20");
            }
        } else {
            String dateTime = dateFormat1.format(date.getTime());
            url = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Inventarios/Inv_Existencias_Otra_Fecha.php?Tienda="+tiendaGlobal
            +"&DateTime="+dateTime).replaceAll(" ", "%20");
        }

        AlertDialog.Builder alertLoad = new AlertDialog.Builder(getContext());
        LayoutInflater inflaterLoad = this.getLayoutInflater();
        View dialogLoad = inflaterLoad.inflate(R.layout.loading_builder, null);
        alertLoad.setView(dialogLoad);
        progressBar = dialogLoad.findViewById(R.id.simpleProgressBar);
        TextView textView = dialogLoad.findViewById(R.id.textoCambio);
        if (!textoActualizar.equals("")) { textView.setText(textoActualizar); }
        progressBar.setVisibility(View.VISIBLE);
        alertDialog = alertLoad.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();

        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaInventariosExistencias(new String(responseBody)); }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }
        });
    }

    public void ListaInventariosExistencias(String respuesta) {
        List<InventariosExistencias_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++) {
                InventariosExistencias_Constructor listaInventarios = new InventariosExistencias_Constructor();
                listaInventarios.setIeProducto(jsonArray.getJSONObject(i).getString("Producto"));
                listaInventarios.setIeEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaInventarios.setIeCantidad(jsonArray.getJSONObject(i).getDouble("Existencia"));
                list.add(listaInventarios);
            }
            InventariosExistencias_Adapter adapter = new InventariosExistencias_Adapter(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        } catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }
}