package com.nervion.sionmovil.Calidad;

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

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CalidadHistorial extends Fragment {

    public CalidadHistorial() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private ImageButton chActualizar;
    private EditText chLote;
    private String textoActualizar = "";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View calidad = inflater.inflate(R.layout.fragment_calidad_historial, container, false);

        chActualizar = calidad.findViewById(R.id.chActualizar);
        chLote = calidad.findViewById(R.id.chLote);
        recyclerView = calidad.findViewById(R.id.rvCalidadHistorial);

        chActualizar.setOnClickListener(view -> {
            textoActualizar = "Actualizar";
            DescargarCalidadHistorial(textoActualizar);
        });
        return calidad;
    }

    private void DescargarCalidadHistorial(String textoActualizar) {
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
        String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Calidad/Cal_Historial.php?Lote="+chLote.getText();
        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaCalidadHistorial(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"No hay conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ListaCalidadHistorial(String respuesta) {
        List<CalidadHistorial_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                CalidadHistorial_Constructor listaCalidad = new CalidadHistorial_Constructor();
                listaCalidad.setChLote(jsonArray.getJSONObject(i).getInt("Lote"));
                listaCalidad.setChObservacion(jsonArray.getJSONObject(i).getString("Observaciones"));
                listaCalidad.setChCantidad(jsonArray.getJSONObject(i).getDouble("Cantidad"));
                listaCalidad.setChUnidad(jsonArray.getJSONObject(i).getString("Unidad"));
                listaCalidad.setChMP(jsonArray.getJSONObject(i).getString("MP"));
                listaCalidad.setChFecha(jsonArray.getJSONObject(i).getString("Fecha"));

                listaCalidad.setChUsuario(jsonArray.getJSONObject(i).getString("Usuario"));
                listaCalidad.setChHora(jsonArray.getJSONObject(i).getString("Hora"));

                listaCalidad.setChViscosidad(jsonArray.getJSONObject(i).getDouble("Viscosidad"));
                listaCalidad.setChViscUnidad(jsonArray.getJSONObject(i).getString("UnidadVisc"));
                listaCalidad.setChDensidad(jsonArray.getJSONObject(i).getDouble("Densidad"));
                listaCalidad.setChSolidos(jsonArray.getJSONObject(i).getDouble("Solidos"));
                listaCalidad.setChBrillo(jsonArray.getJSONObject(i).getDouble("Brillo"));

                listaCalidad.setChObservaciones2(jsonArray.getJSONObject(i).getString("Observaciones2"));
                list.add(listaCalidad);
            }

            CalidadHistorial_Adapter adapter = new CalidadHistorial_Adapter(getContext(), list);
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