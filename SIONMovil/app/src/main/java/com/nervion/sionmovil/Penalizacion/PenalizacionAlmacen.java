package com.nervion.sionmovil.Penalizacion;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class PenalizacionAlmacen extends Fragment {

    public PenalizacionAlmacen() { /*Required empty public constructor*/ }

    private final FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
    private final String correo = firebaseAuth.getEmail();

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private AutoCompleteTextView autoCompleteTV;
    private RecyclerView recyclerView;
    private EditText numeroEscaneos, observaciones;
    private ImageButton paActualizar;
    private Button aplicar;
    private String textoActualizar = "";

    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View penalizacion = inflater.inflate(R.layout.fragment_penalizacionalmacen, container, false);

        autoCompleteTV = penalizacion.findViewById(R.id.autoCorreo);
        numeroEscaneos = penalizacion.findViewById(R.id.numeroEscaneos);
        observaciones = penalizacion.findViewById(R.id.observaciones);
        recyclerView = penalizacion.findViewById(R.id.paRecyclerView);
        paActualizar = penalizacion.findViewById(R.id.paActualizar);
        aplicar = penalizacion.findViewById(R.id.paAplicar);

        FirebaseFirestore.getInstance().collection("usuarios").addSnapshotListener((value, error) -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line);

            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    String correos = documentChange.getDocument().getData().get("correo").toString();
                    adapter.add(correos);
                }
            }
            autoCompleteTV.setAdapter(adapter);
            autoCompleteTV.setOnFocusChangeListener((v, hasFocus) -> autoCompleteTV.showDropDown());
        });

        paActualizar.setOnClickListener(v -> {
            textoActualizar = "Actualizando...";
            DescargarPenalizacionesAlmacen(textoActualizar);
        });

        aplicar.setOnClickListener(v -> {
            if(correo.equals("felipe.moreno@nervion.com.mx") || correo.equals("julio@nervion.com.mx")){
                if(!autoCompleteTV.getText().toString().equals("") && !numeroEscaneos.getText().toString().equals("") && !observaciones.getText().toString().equals("")){
                    Date date = Calendar.getInstance().getTime();
                    String dateTime = dateFormat1.format(date.getTime());

                    String usuarioPenalizado = autoCompleteTV.getText().toString();
                    String numeroPenalizado = numeroEscaneos.getText().toString();
                    String comentarios = observaciones.getText().toString();

                    String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Penalizacion/PenalizacionAlmacen_AplicarPenalizacion.php?Usuario="+ correo
                            +"&UsuarioPenalizado="+ usuarioPenalizado +"&Cantidad="+ numeroPenalizado +"&DateTime="+ dateTime
                            +"&Observaciones="+ comentarios).replaceAll(" ","%20");

                    asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                                autoCompleteTV.setText("");
                                numeroEscaneos.setText("");
                                observaciones.setText("");
                                textoActualizar = "Actualizando...";
                                DescargarPenalizacionesAlmacen(textoActualizar);
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{ Toast.makeText(getActivity(),"Los campos estan vacíos!", Toast.LENGTH_SHORT).show(); }
            }else{ Toast.makeText(getActivity(),"Usted No Tiene Acceso Para Registrar!", Toast.LENGTH_SHORT).show(); }
        });

        DescargarPenalizacionesAlmacen(textoActualizar);
        return penalizacion;
    }

    private void DescargarPenalizacionesAlmacen(String textoActualizar) {
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
        String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Penalizacion/PenalizacionAlmacen.php";
        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaPenalizacionesAlmacen(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"No hay conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ListaPenalizacionesAlmacen(String respuesta) {
        List<PenalizacionAlmacen_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                PenalizacionAlmacen_Constructor listaPenalizaciones = new PenalizacionAlmacen_Constructor();
                listaPenalizaciones.setPaFecha(jsonArray.getJSONObject(i).getString("Fecha"));
                listaPenalizaciones.setPaHora(jsonArray.getJSONObject(i).getString("Hora"));
                listaPenalizaciones.setPaCantidad(jsonArray.getJSONObject(i).getInt("Cantidad"));
                listaPenalizaciones.setPaUsuarioPenalizado(jsonArray.getJSONObject(i).getString("UsuarioPenalizado"));
                listaPenalizaciones.setPaCantidadEscaneada(jsonArray.getJSONObject(i).getInt("CantidadEscaneada"));
                listaPenalizaciones.setPaObservaciones(jsonArray.getJSONObject(i).getString("Observaciones"));
                listaPenalizaciones.setPaRestantes(jsonArray.getJSONObject(i).getInt("Restantes"));
                list.add(listaPenalizaciones);
            }

            PenalizacionAlmacen_Adaptor adapter = new PenalizacionAlmacen_Adaptor(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();

            adapter.setOnItemClickListener((view, position) -> {
                if(correo.equals("felipe.moreno@nervion.com.mx") || correo.equals("julio@nervion.com.mx")){
                }else{ Toast.makeText(getActivity(),"Usted No Tiene Acceso Para Registrar!", Toast.LENGTH_SHORT).show(); }
            });
        }catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }
}
