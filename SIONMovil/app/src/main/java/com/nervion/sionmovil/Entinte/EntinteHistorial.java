package com.nervion.sionmovil.Entinte;

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
import com.nervion.sionmovil.Calidad.CalidadPlanta_Adapter;
import com.nervion.sionmovil.Calidad.CalidadPlanta_Constructor;
import com.nervion.sionmovil.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class EntinteHistorial extends Fragment {

    public EntinteHistorial() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private EditText ehPedido;
    private ImageButton ehActualizar;
    private RecyclerView recyclerView;
    private String textoActualizar = "";

    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View entinte = inflater.inflate(R.layout.fragment_entinte_historial, container, false);

        ehPedido = entinte.findViewById(R.id.ehPedido);
        ehActualizar = entinte.findViewById(R.id.ehActualizar);
        recyclerView = entinte.findViewById(R.id.rvEntinteHistorial);

        ehActualizar.setOnClickListener(view -> {
            if (!ehPedido.getText().toString().equals("")) {
                textoActualizar = "Actualizando...";
                DescargarEntinteHistorial(textoActualizar);
            }else { Toast.makeText(getActivity(),"Ingrese el Número de Pedido!", Toast.LENGTH_SHORT).show(); }
        });
        return entinte;
    }

    private void DescargarEntinteHistorial(String textoActualizar) {
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

        String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Entinte/Ent_Historial.php?Pedido="+ehPedido.getText();

        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaEntinteHistorial(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void ListaEntinteHistorial(String respuesta) {
        List<EntinteHistorial_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                EntinteHistorial_Constructor listaCalidad = new EntinteHistorial_Constructor();
                listaCalidad.setEhPedido(jsonArray.getJSONObject(i).getInt("Pedido"));
                listaCalidad.setEhUsuario(jsonArray.getJSONObject(i).getString("Entonador"));
                listaCalidad.setEhFecha(jsonArray.getJSONObject(i).getString("Fecha"));
                listaCalidad.setEhTono(jsonArray.getJSONObject(i).getInt("Tonos"));
                listaCalidad.setEhDateTime(jsonArray.getJSONObject(i).getString("Terminado"));
                listaCalidad.setEhHora(jsonArray.getJSONObject(i).getString("Hora"));
                listaCalidad.setEhClave(jsonArray.getJSONObject(i).getString("MP"));
                listaCalidad.setEhEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaCalidad.setEhLote(jsonArray.getJSONObject(i).getInt("Lote"));
                listaCalidad.setEhFechaSalida(jsonArray.getJSONObject(i).getString("FechaSalida"));
                listaCalidad.setEhDiferencia(jsonArray.getJSONObject(i).getDouble("Diferencia"));
                listaCalidad.setEhHoraSalida(jsonArray.getJSONObject(i).getString("HoraSalida"));
                list.add(listaCalidad);
            }

            EntinteHistorial_Adapter adapter = new EntinteHistorial_Adapter(getContext(), list);
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