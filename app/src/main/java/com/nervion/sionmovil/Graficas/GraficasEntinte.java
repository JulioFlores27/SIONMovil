package com.nervion.sionmovil.Graficas;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nervion.sionmovil.MainActivity;
import com.nervion.sionmovil.R;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cz.msebera.android.httpclient.Header;


public class GraficasEntinte extends Fragment {

    public GraficasEntinte() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private PieChart gePedidosEntregados, geTonosTotales;
    private BarChart geTiempoEntrega;

    private ImageButton geActualizar;
    private EditText geFechaInicial, geFechaFinal, geLimiteInferior, geLimiteSuperior;
    private Spinner geEntonador;

    private ProgressBar progressBar;
    private String textoActualizar = "", entonadorSeleccionado = "";
    private AlertDialog alertDialog;

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    final Calendar myCalendar= Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View graficas = inflater.inflate(R.layout.fragment_graficas_entinte, container, false);

        geFechaInicial = graficas.findViewById(R.id.geFechaInicial);
        geFechaFinal = graficas.findViewById(R.id.geFechaFinal);
        geLimiteInferior = graficas.findViewById(R.id.geLimiteInferior);
        geLimiteSuperior = graficas.findViewById(R.id.geLimiteSuperior);
        geActualizar = graficas.findViewById(R.id.geActualizar);
        gePedidosEntregados = graficas.findViewById(R.id.gePedidosEntregados);
        geTonosTotales = graficas.findViewById(R.id.geTonosTotales);
        geTiempoEntrega = graficas.findViewById(R.id.geTiempoEntrega);
        geEntonador = graficas.findViewById(R.id.geEntonador);

        DescargarGraficasEntintePie(entonadorSeleccionado);
        DescargarGraficasEntinteBar(textoActualizar, entonadorSeleccionado);

        DatePickerDialog.OnDateSetListener date = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            geFechaInicial.setText(dateFormat1.format(myCalendar.getTime()));
        };

        geFechaInicial.setOnLongClickListener(view -> {
            geFechaInicial.setText("");
            return false;
        });
        geFechaInicial.setOnClickListener(view -> new DatePickerDialog(getContext(),date, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        DatePickerDialog.OnDateSetListener dateFinal = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            geFechaFinal.setText(dateFormat1.format(myCalendar.getTime()));
        };

        geFechaFinal.setOnLongClickListener(view -> {
            geFechaFinal.setText("");
            return false;
        });
        geFechaFinal.setOnClickListener(view -> new DatePickerDialog(getContext(),dateFinal, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        String[] entonadores = {"Seleciona:", "Armando Nolasco", "Arnold Andrade", "Eduardo Hernández", "Gildardo Meléndez",
                "Jonathan García", "Julio Rojas", "Marco Antonio Gallardo", "Miguel Ángel Laguna", "Silverio Flores", "Otro"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.configuracion_adapter, entonadores);
        geEntonador.setAdapter(arrayAdapter);
        geActualizar.setOnClickListener(view -> {
            String selectEntonador = geEntonador.getSelectedItem().toString();
            if(!selectEntonador.equals("Seleciona:")){
                textoActualizar = "Actualizando...";
                entonadorSeleccionado = selectEntonador;
                DescargarGraficasEntintePie(entonadorSeleccionado);
                DescargarGraficasEntinteBar(textoActualizar, entonadorSeleccionado);
            }else {
                entonadorSeleccionado = "";
                DescargarGraficasEntintePie(entonadorSeleccionado);
                DescargarGraficasEntinteBar(textoActualizar, entonadorSeleccionado);
            }

        });
        return graficas;
    }

    private void DescargarGraficasEntinteBar(String textoActualizar, String selectEntonador) {
        String urlAprobacion;
        Date date = Calendar.getInstance().getTime();
        String dateTime = dateFormat1.format(date.getTime());

        if (!geFechaInicial.getText().toString().equals("") && !geFechaFinal.getText().toString().equals("")) {
            urlAprobacion = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_TiempoEntregaPedidos.php?FechaInicial="
                    +geFechaInicial.getText()+" 00:00:00&FechaFinal="+geFechaFinal.getText()+" 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
        }else if (!geFechaInicial.getText().toString().equals("") && geFechaFinal.getText().toString().equals("")) {
            urlAprobacion = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_TiempoEntregaPedidos.php?FechaInicial="
                    +geFechaInicial.getText()+" 00:00:00&FechaFinal="+dateTime+" 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
        }else if (geFechaInicial.getText().toString().equals("") && !geFechaFinal.getText().toString().equals("")) {
            urlAprobacion = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_TiempoEntregaPedidos.php?FechaInicial=2015-01-01 00:00:00"
                    +"&FechaFinal="+geFechaFinal.getText()+" 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
        } else {
            urlAprobacion = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_TiempoEntregaPedidos.php?FechaInicial=2015-01-01 00:00:00"
                    +"&FechaFinal=2019-12-31 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
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

        asyncHttpClient.post(urlAprobacion, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        final JSONArray jsonArray = new JSONArray(new String(responseBody));
                        double limiteInferior, limiteSuperior;
                        if (!geLimiteInferior.getText().toString().equals("")) {
                            limiteInferior = Double.parseDouble(geLimiteInferior.getText().toString());
                        }else{ limiteInferior = -30000; }
                        if (!geLimiteSuperior.getText().toString().equals("")) {
                            limiteSuperior = Double.parseDouble(geLimiteSuperior.getText().toString());
                        }else{ limiteSuperior = 20000; }

                        ArrayList<BarEntry> entries = new ArrayList<>();

                        //fit the data into a bar
                        for (int i=0; i<jsonArray.length(); i++) {
                            double horas = jsonArray.getJSONObject(i).getDouble("Diferencia");
                            if (limiteInferior <= horas && horas <= limiteSuperior){
                                BarEntry barEntry = new BarEntry(i, (float) jsonArray.getJSONObject(i).getDouble("Diferencia"));
                                entries.add(barEntry);
                            }
                        }
                        final ArrayList<String> xAxisLabel = new ArrayList<>();
                        for (int i=0; i<jsonArray.length(); i++) { xAxisLabel.add(String.valueOf(jsonArray.getJSONObject(i).getInt("Pedido"))); }
                        geTiempoEntrega.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
                        geTiempoEntrega.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                        YAxis yAxisRight = geTiempoEntrega.getAxisRight();
                        yAxisRight.setEnabled(false);

                        BarDataSet barDataSet = new BarDataSet(entries, "Tiempos de Entrega de Pedidos");
                        BarData data = new BarData(barDataSet);
                        geTiempoEntrega.getXAxis().setGranularity(1);
                        geTiempoEntrega.getXAxis().setGranularityEnabled(true);
                        geTiempoEntrega.getDescription().setEnabled(false);
                        geTiempoEntrega.setData(data);
                        geTiempoEntrega.notifyDataSetChanged();
                        geTiempoEntrega.invalidate();

                        progressBar.setVisibility(View.INVISIBLE);
                        alertDialog.dismiss();
                    }catch (Exception e) {
                        Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        alertDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }
        });
    }

    private void DescargarGraficasEntintePie(String selectEntonador) {
        String urlPedidos, urlATonos;
        Date date = Calendar.getInstance().getTime();
        String dateTime = dateFormat1.format(date.getTime());
        if (!geFechaInicial.getText().toString().equals("") && !geFechaFinal.getText().toString().equals("")) {
            urlPedidos = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_PedidosEntregados.php?FechaInicial="+geFechaInicial.getText()+" 00:00:00"
                    +"&FechaFinal="+geFechaFinal.getText()+" 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
            urlATonos = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_TonosTotales.php?FechaInicial="+geFechaInicial.getText()+" 00:00:00"
                    +"&FechaFinal="+geFechaFinal.getText()+" 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
        }else if (!geFechaInicial.getText().toString().equals("") && geFechaFinal.getText().toString().equals("")) {
            urlPedidos = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_PedidosEntregados.php?FechaInicial="+geFechaInicial.getText()+" 00:00:00"
                    +"&FechaFinal="+dateTime+" 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
            urlATonos = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_TonosTotales.php?FechaInicial="+geFechaInicial.getText()+" 00:00:00"
                    +"&FechaFinal="+dateTime+" 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
        }else if (geFechaInicial.getText().toString().equals("") && !geFechaFinal.getText().toString().equals("")) {
            urlPedidos = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_PedidosEntregados.php?FechaInicial=2015-01-01 00:00:00 "
                    +"&FechaFinal="+geFechaFinal.getText()+" 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
            urlATonos = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_TonosTotales.php?FechaInicial=2015-01-01 00:00:00 "
                    +"&FechaFinal="+geFechaFinal.getText()+" 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
        } else {
            urlPedidos = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_PedidosEntregados.php?FechaInicial=2019-01-01 00:00:00 "
                    +"&FechaFinal=2019-12-31 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
            urlATonos = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Entinte_TonosTotales.php?FechaInicial=2015-01-01 00:00:00 "
                    +"&FechaFinal="+dateTime+" 23:59:59&Entonador="+selectEntonador).replace(" ", "%20");
        }

        asyncHttpClient.post(urlPedidos, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        final JSONArray jsonArray = new JSONArray(new String(responseBody));
                        ArrayList<PieEntry> pieEntries = new ArrayList<>();
                        Map<String, Integer> data = new HashMap<>();
                        ArrayList<Integer> colors = new ArrayList<>();
                        for (int i=0; i<jsonArray.length(); i++) {
                            Random rnd = new Random();
                            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            colors.add(color);
                            pieEntries.add(new PieEntry(jsonArray.getJSONObject(i).getInt("NumeroDePedidos"), jsonArray.getJSONObject(i).getString("Entonador")));
                        }

                        //collecting the entries with label name
                        PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
                        pieDataSet.setValueTextSize(16f);
                        pieDataSet.setColors(colors);
                        PieData pieData = new PieData(pieDataSet);
                        //showing the value of the entries, default true if not set
                        pieData.setDrawValues(true);
                        gePedidosEntregados.setEntryLabelColor(Color.BLACK);
                        gePedidosEntregados.getDescription().setText("Pedidos Entregados");
                        gePedidosEntregados.setHoleRadius(50f);
                        gePedidosEntregados.setData(pieData);
                        gePedidosEntregados.invalidate();
                    }catch (Exception e) {
                        Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            }
        });

        asyncHttpClient.post(urlATonos, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        final JSONArray jsonArray = new JSONArray(new String(responseBody));
                        ArrayList<PieEntry> pieEntries = new ArrayList<>();
                        Map<String, Integer> data = new HashMap<>();
                        for (int i=0; i<jsonArray.length(); i++) {
                            data.put(jsonArray.getJSONObject(i).getString("Entonador"),jsonArray.getJSONObject(i).getInt("TonosTotales"));
                        }

                        //initializing colors for the entries
                        ArrayList<Integer> colors = new ArrayList<>();
                        for (int i=0; i<jsonArray.length(); i++) {
                            Random rnd = new Random();
                            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            colors.add(color);
                        }

                        for(String type: data.keySet()){ pieEntries.add(new PieEntry(data.get(type).floatValue(), type)); }
                        //collecting the entries with label name
                        PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
                        pieDataSet.setValueTextSize(16f);
                        pieDataSet.setColors(colors);
                        PieData pieData = new PieData(pieDataSet);
                        //showing the value of the entries, default true if not set
                        pieData.setDrawValues(true);
                        geTonosTotales.setEntryLabelColor(Color.BLACK);
                        geTonosTotales.getDescription().setText("Tonos Totales");
                        geTonosTotales.setHoleRadius(50f);
                        geTonosTotales.setData(pieData);
                        geTonosTotales.invalidate();
                    }catch (Exception e) {
                        Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            }
        });
    }
}