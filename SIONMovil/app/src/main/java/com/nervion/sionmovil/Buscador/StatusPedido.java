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

public class StatusPedido extends Fragment {

    public StatusPedido() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private RecyclerView recyclerView;
    private ImageButton spActualizar;
    private EditText spPedido;

    private ProgressBar progressBar;
    private String textoActualizar = "";
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View buscador = inflater.inflate(R.layout.fragment_buscador_status_pedido, container, false);

        recyclerView = buscador.findViewById(R.id.rvStatusPedido);
        spPedido = buscador.findViewById(R.id.spPedido);
        spActualizar = buscador.findViewById(R.id.spActualizar);

        spActualizar.setOnClickListener(v -> {
            textoActualizar = "Actualizando...";
            DescargarStatusPedido(textoActualizar);
        });

        DescargarStatusPedido(textoActualizar);
        return buscador;
    }

    private void DescargarStatusPedido(String textoActualizar) {
        String url;
        if (!spPedido.getText().toString().equals("")) {
            url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Buscador/Bus_Status_Pedido.php?Observaciones2="+spPedido.getText();
        } else { url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Buscador/Bus_Status_Pedido.php"; }

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
                if (statusCode == 200) { ListaStatusPedido(new String(responseBody)); }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }
        });
    }

    public void ListaStatusPedido(String respuesta) {
        List<StatusPedido_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                StatusPedido_Constructor listaSurtir = new StatusPedido_Constructor();
                listaSurtir.setSpProducto(jsonArray.getJSONObject(i).getString("Producto"));
                listaSurtir.setSpEnvase(jsonArray.getJSONObject(i).getString("Envase"));
                listaSurtir.setSpCantidad(jsonArray.getJSONObject(i).getInt("Cantidad"));
                listaSurtir.setSpObservaciones2(jsonArray.getJSONObject(i).getString("Observaciones2"));
                listaSurtir.setSpFecha(jsonArray.getJSONObject(i).getString("Fecha"));
                listaSurtir.setSpHora(jsonArray.getJSONObject(i).getString("Hora"));
                list.add(listaSurtir);
            }

            StatusPedido_Adapter adapter = new StatusPedido_Adapter(getContext(), list);
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