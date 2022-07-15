package com.nervion.sionmovil.Graficas;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

public class GraficasCalidad extends Fragment {

    public GraficasCalidad() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private PieChart gcAjustes, gcATiempo;
    private BarChart gcAprobacion;

    private ImageButton gcActualizar;
    private EditText gcFechaInicial, gcFechaFinal, gcLimiteInferior, gcLimiteSuperior;

    private ProgressBar progressBar;
    private String textoActualizar = "";
    private AlertDialog alertDialog;

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    final Calendar myCalendar= Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View graficas = inflater.inflate(R.layout.fragment_graficas_calidad, container, false);

        gcFechaInicial = graficas.findViewById(R.id.gcFechaInicial);
        gcFechaFinal = graficas.findViewById(R.id.gcFechaFinal);
        gcLimiteInferior = graficas.findViewById(R.id.gcLimiteInferior);
        gcLimiteSuperior = graficas.findViewById(R.id.gcLimiteSuperior);
        gcActualizar = graficas.findViewById(R.id.gcActualizar);
        gcAjustes = graficas.findViewById(R.id.gcAjustes);
        gcATiempo = graficas.findViewById(R.id.gcATiempo);
        gcAprobacion = graficas.findViewById(R.id.gcAprobacion);

        DescargarGraficasCalidadPie();
        DescargarGraficasCalidadBar(textoActualizar);

        DatePickerDialog.OnDateSetListener date = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            gcFechaInicial.setText(dateFormat1.format(myCalendar.getTime()));
        };

        gcFechaInicial.setOnLongClickListener(view -> {
            gcFechaInicial.setText("");
            return false;
        });
        gcFechaInicial.setOnClickListener(view -> new DatePickerDialog(getContext(),date, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        DatePickerDialog.OnDateSetListener dateFinal = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            gcFechaFinal.setText(dateFormat1.format(myCalendar.getTime()));
        };

        gcFechaFinal.setOnLongClickListener(view -> {
            gcFechaFinal.setText("");
            return false;
        });
        gcFechaFinal.setOnClickListener(view -> new DatePickerDialog(getContext(),dateFinal, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        gcActualizar.setOnClickListener(view -> {
            textoActualizar = "Actualizando...";
            DescargarGraficasCalidadPie();
            DescargarGraficasCalidadBar(textoActualizar);
        });
        return graficas;
    }

    private void DescargarGraficasCalidadBar(String textoActualizar) {
        String urlAprobacion;
        Date date = Calendar.getInstance().getTime();
        String dateTime = dateFormat1.format(date.getTime());

        if (!gcFechaInicial.getText().toString().equals("") && !gcFechaFinal.getText().toString().equals("")) {
            urlAprobacion = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_TiemposAprobacion.php?FechaInicial="
                    +gcFechaInicial.getText()+" 00:00:00&FechaFinal="+gcFechaFinal.getText()+" 23:59:59").replace(" ", "%20");
        }else if (!gcFechaInicial.getText().toString().equals("") && gcFechaFinal.getText().toString().equals("")) {
            urlAprobacion = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_TiemposAprobacion.php?FechaInicial="
                    +gcFechaInicial.getText()+" 00:00:00&FechaFinal="+dateTime+" 23:59:59").replace(" ", "%20");
        }else if (gcFechaInicial.getText().toString().equals("") && !gcFechaFinal.getText().toString().equals("")) {
            urlAprobacion = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_TiemposAprobacion.php?FechaInicial=2015-01-01 00:00:00"
                    +"&FechaFinal="+gcFechaFinal.getText()+" 23:59:59").replace(" ", "%20");
        } else {
            urlAprobacion = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_TiemposAprobacion.php?FechaInicial=2015-01-01 00:00:00"
                    +"&FechaFinal="+dateTime+" 23:59:59").replace(" ", "%20");
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
                        if (!gcLimiteInferior.getText().toString().equals("")) {
                            limiteInferior = Double.parseDouble(gcLimiteInferior.getText().toString());
                        }else{ limiteInferior = -30000; }
                        if (!gcLimiteSuperior.getText().toString().equals("")) {
                            limiteSuperior = Double.parseDouble(gcLimiteSuperior.getText().toString());
                        }else{ limiteSuperior = 20000; }

                        ArrayList<BarEntry> entries = new ArrayList<>();

                        //fit the data into a bar
                        for (int i=0; i<jsonArray.length(); i++) {
                            double horas = jsonArray.getJSONObject(i).getDouble("Horas");
                            if (limiteInferior <= horas && horas <= limiteSuperior){
                                BarEntry barEntry = new BarEntry(i, (float) jsonArray.getJSONObject(i).getDouble("Horas"));
                                entries.add(barEntry);
                            }
                        }
                        final ArrayList<String> xAxisLabel = new ArrayList<>();
                        for (int i=0; i<jsonArray.length(); i++) { xAxisLabel.add(String.valueOf(jsonArray.getJSONObject(i).getInt("Lotes"))); }
                        gcAprobacion.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
                        gcAprobacion.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                        YAxis yAxisRight = gcAprobacion.getAxisRight();
                        yAxisRight.setEnabled(false);

                        BarDataSet barDataSet = new BarDataSet(entries, "Tiempos de Aprobación");
                        BarData data = new BarData(barDataSet);
                        gcAprobacion.getXAxis().setGranularity(1);
                        gcAprobacion.getXAxis().setGranularityEnabled(true);
                        gcAprobacion.getDescription().setEnabled(false);
                        gcAprobacion.setData(data);
                        gcAprobacion.notifyDataSetChanged();
                        gcAprobacion.invalidate();

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

    private void DescargarGraficasCalidadPie() {
        String urlATiempo, urlAjuste;
        Date date = Calendar.getInstance().getTime();
        String dateTime = dateFormat1.format(date.getTime());
        if (!gcFechaInicial.getText().toString().equals("") && !gcFechaFinal.getText().toString().equals("")) {
            urlATiempo = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_ATiempo.php?FechaInicial="+gcFechaInicial.getText()+" 00:00:00"
                    +"&FechaFinal="+gcFechaFinal.getText()+" 23:59:59").replace(" ", "%20");
            urlAjuste = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_Ajustes.php?FechaInicial="+gcFechaInicial.getText()+" 00:00:00"
                    +"&FechaFinal="+gcFechaFinal.getText()+" 23:59:59").replace(" ", "%20");
        }else if (!gcFechaInicial.getText().toString().equals("") && gcFechaFinal.getText().toString().equals("")) {
            urlATiempo = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_ATiempo.php?FechaInicial="+gcFechaInicial.getText()+" 00:00:00"
                    +"&FechaFinal="+dateTime+" 23:59:59").replace(" ", "%20");
            urlAjuste = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_Ajustes.php?FechaInicial="+gcFechaInicial.getText()+" 00:00:00"
                    +"&FechaFinal="+dateTime+" 23:59:59").replace(" ", "%20");
        }else if (gcFechaInicial.getText().toString().equals("") && !gcFechaFinal.getText().toString().equals("")) {
            urlATiempo = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_ATiempo.php?FechaInicial=2015-01-01 00:00:00 "
                    +"&FechaFinal="+gcFechaFinal.getText()+" 23:59:59").replace(" ", "%20");
            urlAjuste = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_Ajustes.php?FechaInicial=2015-01-01 00:00:00 "
                    +"&FechaFinal="+gcFechaFinal.getText()+" 23:59:59").replace(" ", "%20");
        } else {
            urlATiempo = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_ATiempo.php?FechaInicial=2015-01-01 00:00:00 "
                +"&FechaFinal="+dateTime+" 23:59:59").replace(" ", "%20");
            urlAjuste = ("http://websion.hol.es/Aplicacion_DispositivoMovil/Graficas/Gra_Calidad_Ajustes.php?FechaInicial=2015-01-01 00:00:00 "
                    +"&FechaFinal="+dateTime+" 23:59:59").replace(" ", "%20");
        }

        asyncHttpClient.post(urlAjuste, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        final JSONArray jsonArray = new JSONArray(new String(responseBody));
                        ArrayList<PieEntry> pieEntries = new ArrayList<>();
                        Map<String, Integer> data = new HashMap<>();
                        for (int i=0; i<jsonArray.length(); i++) {
                            double total = (jsonArray.getJSONObject(i).getInt("LotesAjustados")+jsonArray.getJSONObject(i).getInt("LotesSinAjuste"));
                            double dentroTiempo = (Double.parseDouble(String.valueOf(jsonArray.getJSONObject(i).getInt("LotesAjustados")))/total)*100;
                            double fueraTiempo = (Double.parseDouble(String.valueOf(jsonArray.getJSONObject(i).getInt("LotesSinAjuste")))/total)*100;
                            data.put("Lotes Ajustados "+String.format("%.1f", dentroTiempo)+"%",jsonArray.getJSONObject(i).getInt("LotesAjustados"));
                            data.put("Lotes Sin Ajuste "+String.format("%.1f", fueraTiempo)+"%",jsonArray.getJSONObject(i).getInt("LotesSinAjuste"));
                        }

                        //initializing colors for the entries
                        ArrayList<Integer> colors = new ArrayList<>();
                        for (int i=0; i<2; i++) {
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
                        gcAjustes.getDescription().setText("Ajustes");
                        gcAjustes.setDrawHoleEnabled(false);
                        gcAjustes.setData(pieData);
                        gcAjustes.invalidate();
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

        asyncHttpClient.post(urlATiempo, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        final JSONArray jsonArray = new JSONArray(new String(responseBody));
                        ArrayList<PieEntry> pieEntries = new ArrayList<>();
                        Map<String, Integer> data = new HashMap<>();
                        for (int i=0; i<jsonArray.length(); i++) {
                            double total = (jsonArray.getJSONObject(i).getInt("DentroDeTiempo")+jsonArray.getJSONObject(i).getInt("FueraDeTiempo"));
                            double dentroTiempo = (Double.parseDouble(String.valueOf(jsonArray.getJSONObject(i).getInt("DentroDeTiempo")))/total)*100;
                            double fueraTiempo = (Double.parseDouble(String.valueOf(jsonArray.getJSONObject(i).getInt("FueraDeTiempo")))/total)*100;
                            data.put("Dentro De Tiempo "+String.format("%.1f", dentroTiempo)+"%",jsonArray.getJSONObject(i).getInt("DentroDeTiempo"));
                            data.put("Fuera De Tiempo "+String.format("%.1f", fueraTiempo)+"%",jsonArray.getJSONObject(i).getInt("FueraDeTiempo"));
                        }

                        //initializing colors for the entries
                        ArrayList<Integer> colors = new ArrayList<>();
                        for (int i=0; i<2; i++) {
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
                        gcATiempo.getDescription().setText("A Tiempos");
                        gcATiempo.setDrawHoleEnabled(false);
                        gcATiempo.setData(pieData);
                        gcATiempo.invalidate();
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