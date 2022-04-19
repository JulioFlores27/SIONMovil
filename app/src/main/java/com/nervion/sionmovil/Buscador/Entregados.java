package com.nervion.sionmovil.Buscador;

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

public class Entregados extends Fragment {

    public Entregados() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private RecyclerView recyclerView;
    private ImageButton beActualizar;
    private EditText bePedido;

    private ProgressBar progressBar;
    private String textoActualizar = "";
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View buscador = inflater.inflate(R.layout.fragment_buscador_entregados, container, false);

        recyclerView = buscador.findViewById(R.id.rvEntregados);
        bePedido = buscador.findViewById(R.id.bePedido);
        beActualizar = buscador.findViewById(R.id.beActualizar);

        beActualizar.setOnClickListener(v -> {
            textoActualizar = "Actualizando...";
            DescargarEntregados(textoActualizar);
        });

        DescargarEntregados(textoActualizar);
        return buscador;
    }

    private void DescargarEntregados(String textoActualizar) {
        String url;
        if (!bePedido.getText().toString().equals("")) {
            url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Buscador/Bus_Entregados.php?Observaciones2="+bePedido.getText();
        } else { url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Buscador/Bus_Entregados.php"; }

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
                if (statusCode == 200) { ListaEntregados(new String(responseBody)); }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }
        });
    }

    public void ListaEntregados(String respuesta) {
        List<Entregados_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                Entregados_Constructor listaSurtir = new Entregados_Constructor();
                listaSurtir.setBePedido(jsonArray.getJSONObject(i).getString("Solicitud"));
                listaSurtir.setBeUsuario(jsonArray.getJSONObject(i).getString("Nombre_Entrega"));
                listaSurtir.setBeFecha(jsonArray.getJSONObject(i).getString("Fecha"));
                listaSurtir.setBeLatitud(jsonArray.getJSONObject(i).getString("Latitud"));
                listaSurtir.setBeLongitud(jsonArray.getJSONObject(i).getString("Longitud"));
                listaSurtir.setBeHora(jsonArray.getJSONObject(i).getString("Hora"));
                list.add(listaSurtir);
            }

            Entregados_Adapter adapter = new Entregados_Adapter(getContext(), list);
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