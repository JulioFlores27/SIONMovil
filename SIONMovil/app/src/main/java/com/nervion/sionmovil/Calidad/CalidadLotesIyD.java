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

public class CalidadLotesIyD extends Fragment {

    public CalidadLotesIyD() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private ImageButton cidActualizar;
    private EditText cidLote;
    private String textoActualizar = "";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View calidad = inflater.inflate(R.layout.fragment_calidad_lotes_iyd, container, false);

        cidActualizar = calidad.findViewById(R.id.cidActualizar);
        cidLote = calidad.findViewById(R.id.cidLote);
        recyclerView = calidad.findViewById(R.id.rvCalidadLotesIyD);

        cidActualizar.setOnClickListener(view -> {
            textoActualizar = "Actualizar";
            DescargarCalidadLotesIyD(textoActualizar);
        });
        DescargarCalidadLotesIyD(textoActualizar);
        return calidad;
    }

    private void DescargarCalidadLotesIyD(String textoActualizar) {
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
        String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Calidad/Cal_Lotes_IyD.php?LoteIyD="+cidLote.getText();
        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaCalidadLotesIyD(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"No hay conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ListaCalidadLotesIyD(String respuesta) {
        List<CalidadLotesIyD_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                CalidadLotesIyD_Constructor listaCalidad = new CalidadLotesIyD_Constructor();
                listaCalidad.setCidInvLote(jsonArray.getJSONObject(i).getString("Observaciones"));
                listaCalidad.setCidProducto(jsonArray.getJSONObject(i).getString("InvProducto"));
                listaCalidad.setCidFecha(jsonArray.getJSONObject(i).getString("InvFecha"));
                list.add(listaCalidad);
            }

            CalidadLotesIyD_Adapter adapter = new CalidadLotesIyD_Adapter(getContext(), list);
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