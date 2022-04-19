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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nervion.sionmovil.R;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CalidadPlanta extends Fragment {

    public CalidadPlanta() {}

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private final String correo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private Button cpActualizar;
    private RecyclerView recyclerView;
    private String textoActualizar = "";

    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    private String[] spinnerUnidad = {"Elige","g","kg","L","ml"};

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat3 = new SimpleDateFormat("kk:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View calidad = inflater.inflate(R.layout.fragment_calidad_planta, container, false);

        cpActualizar = calidad.findViewById(R.id.cpActualizar);
        recyclerView = calidad.findViewById(R.id.rvCalidadPlanta);

        cpActualizar.setOnClickListener(view -> {
            textoActualizar = "Actualizando...";
            DescargarCalidadPlanta(textoActualizar);
        });

        DescargarCalidadPlanta(textoActualizar);

        return calidad;
    }

    private void DescargarCalidadPlanta(String textoActualizar) {
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

        String url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Calidad/Cal_Planta_Ajustes.php";

        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaCalidadPlanta(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"No hay conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ListaCalidadPlanta(String respuesta) {
        List<CalidadPlanta_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                CalidadPlanta_Constructor listaCalidad = new CalidadPlanta_Constructor();
                listaCalidad.setCpID(jsonArray.getJSONObject(i).getInt("ID"));
                listaCalidad.setCpLote(jsonArray.getJSONObject(i).getInt("Lote"));
                listaCalidad.setCpUsuario(jsonArray.getJSONObject(i).getString("Usuario"));
                listaCalidad.setCpUnidad(jsonArray.getJSONObject(i).getString("Unidad"));
                listaCalidad.setCpMP(jsonArray.getJSONObject(i).getString("MP"));
                listaCalidad.setCpObservacion(jsonArray.getJSONObject(i).getString("Observaciones"));
                listaCalidad.setCpCantidad(jsonArray.getJSONObject(i).getDouble("Cantidad"));
                listaCalidad.setCpFecha(jsonArray.getJSONObject(i).getString("Fecha"));
                listaCalidad.setCpHora(jsonArray.getJSONObject(i).getString("Hora"));
                list.add(listaCalidad);
            }

            CalidadPlanta_Adapter adapter = new CalidadPlanta_Adapter(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();

            adapter.setOnItemClickListener((view, position) -> {
                CalidadPlanta_Constructor getCalidad = list.get(position);
                final String cpLote = String.valueOf(getCalidad.getCpLote());
                final String cpCantidad = String.valueOf(getCalidad.getCpCantidad());
                final String cpMateriaPrima = getCalidad.getCpMP();
                final String cpUnidad = getCalidad.getCpUnidad();
                final String cpID = String.valueOf(getCalidad.getCpID());
                cpClickBuilder(cpLote, cpCantidad, cpMateriaPrima, cpUnidad, cpID);
            });

            adapter.setOnItemLongClickListener((view, position) -> {
                CalidadPlanta_Constructor getCalidad = list.get(position);
                final String cpLote = String.valueOf(getCalidad.getCpLote());
                final String cpCantidad = String.valueOf(getCalidad.getCpCantidad());
                final String cpMateriaPrima = getCalidad.getCpMP();
                final String cpID = String.valueOf(getCalidad.getCpID());
                cpLongClickBuilder(cpLote, cpCantidad, cpMateriaPrima, cpID);
                return false;
            });
        }catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }

    private void cpClickBuilder(String cpLote, String cpCantidad, String cpMateriaPrima, String cpUnidad, String cpID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.calidad_planta_click_builder, null);
        dialogBuilder.setView(dialogView);
        //INICIO CONTENIDO DEL ALERT
        Button botonCancelar = dialogView.findViewById(R.id.botonCancelar);
        Button botonEntregar = dialogView.findViewById(R.id.botonEntregar);
        Button botonProceso = dialogView.findViewById(R.id.botonProceso);

        AutoCompleteTextView cpUsuario = dialogView.findViewById(R.id.cpUsuario);
        FirebaseFirestore.getInstance().collection("usuarios").whereIn("area", Arrays.asList("Almacén", "Producción")).addSnapshotListener((value, error) -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line);
            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    String nombres = documentChange.getDocument().getData().get("nombre").toString();
                    adapter.add(nombres);
                }
            }
            cpUsuario.setAdapter(adapter);
            cpUsuario.setOnFocusChangeListener((v, hasFocus) -> cpUsuario.showDropDown());
        });

        Date date = Calendar.getInstance().getTime();
        String dateTime = dateFormat1.format(date.getTime());
        String fecha = dateFormat2.format(date.getTime());
        String hora = dateFormat3.format(date.getTime());

        botonEntregar.setOnClickListener(view -> {
            if (!cpUsuario.getText().toString().equals("")) {
                String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Calidad/Cal_Planta_AlertPress_EntregarMuestraYEnProceso.php?ID="+cpID
                        +"&Lote="+cpLote+"&Usuario="+cpUsuario.getText()+"&Observaciones=Realizado&Cantidad="+cpCantidad+"&Unidad="+cpUnidad
                        +"&MP="+cpMateriaPrima+"&Fecha="+fecha+"&Hora="+hora+"&DepartamentoOriginal=Planta&DepartamentoActual=Calidad&DateTime="+dateTime
                        +"&UsuarioApp="+correo).replaceAll(" ","%20");
                asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            textoActualizar = "Actualizando...";
                            DescargarCalidadPlanta(textoActualizar);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                    }
                });
            }else { Toast.makeText(getActivity(),"Favor de ingresar el(la) usuario!", Toast.LENGTH_LONG).show(); }
        });

        botonProceso.setOnClickListener(view -> {
            if (!cpUsuario.getText().toString().equals("")) {
                String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Calidad/Cal_Planta_AlertPress_EntregarMuestraYEnProceso.php?ID="+cpID
                        +"&Lote="+cpLote+"&Usuario="+cpUsuario.getText()+"&Observaciones=En proceso&Cantidad="+cpMateriaPrima+"&Unidad="+cpCantidad
                        +"&MP="+cpUnidad+"&Fecha="+fecha+"&Hora="+hora+"&DepartamentoOriginal=Planta ajuste&DepartamentoActual=Planta ajuste&DateTime="+dateTime
                        +"&UsuarioApp="+correo).replaceAll(" ","%20");
                asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            textoActualizar = "Actualizando...";
                            DescargarCalidadPlanta(textoActualizar);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                    }
                });
            }else { Toast.makeText(getActivity(),"Favor de ingresar el(la) usuario!", Toast.LENGTH_LONG).show(); }
        });

        botonCancelar.setOnClickListener(view -> alertDialog.dismiss());
        //FIN CONTENIDO DEL ALERT
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void cpLongClickBuilder(String cpLote, String cpCantidad, String cpMateriaPrima, String cpID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.calidad_planta_longclick_builder, null);
        dialogBuilder.setView(dialogView);
        //INICIO CONTENIDO DEL ALERT
        Button botonGuardar = dialogView.findViewById(R.id.botonGuardar);
        Button botonCancelar = dialogView.findViewById(R.id.botonCancelar);

        EditText cpCantidadCorregida = dialogView.findViewById(R.id.cpCantidadCorregida);
        cpCantidadCorregida.setText(cpCantidad);

        EditText cpMateriaPrimaCorregida = dialogView.findViewById(R.id.cpMateriaPrimaCorregida);
        cpMateriaPrimaCorregida.setText(cpMateriaPrima);

        Spinner cpUnidadCorregida = dialogView.findViewById(R.id.cpUnidadCorregida);
        cpUnidadCorregida.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerUnidad));

        AutoCompleteTextView cpUsuarioCorregida = dialogView.findViewById(R.id.cpUsuarioCorregida);
        FirebaseFirestore.getInstance().collection("usuarios").whereEqualTo("area","Calidad").addSnapshotListener((value, error) -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line);
            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    String nombres = documentChange.getDocument().getData().get("nombre").toString();
                    adapter.add(nombres);
                }
            }
            cpUsuarioCorregida.setAdapter(adapter);
            cpUsuarioCorregida.setOnFocusChangeListener((v, hasFocus) -> cpUsuarioCorregida.showDropDown());
        });

        botonGuardar.setOnClickListener(view -> {
            if (!cpCantidadCorregida.getText().toString().equals("")) {
                if (!cpUnidadCorregida.getSelectedItem().equals("Elige")) {
                    if (!cpUsuarioCorregida.getText().toString().equals("")) {
                        Date date = Calendar.getInstance().getTime();
                        String dateTime = dateFormat1.format(date.getTime());
                        String fecha = dateFormat2.format(date.getTime());
                        String hora = dateFormat3.format(date.getTime());

                        String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Calidad/Cal_Planta_AlertLongPress_GuardarCambios.php?ID="+cpID
                                +"&Lote="+cpLote+"&Usuario="+cpUsuarioCorregida.getText()+"&Observaciones=Ajuste editado&Cantidad="+cpCantidadCorregida.getText()
                                +"&Unidad="+cpUnidadCorregida.getSelectedItem()+"&MP="+cpMateriaPrimaCorregida.getText()+"&Fecha="+fecha+"&Hora="+hora
                                +"&DepartamentoOriginal=Planta ajuste&DepartamentoActual=Planta ajuste&DateTime="+dateTime
                                +"&UsuarioApp="+correo).replaceAll(" ","%20");
                        asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode == 200) {
                                    Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                    textoActualizar = "Actualizando...";
                                    DescargarCalidadPlanta(textoActualizar);
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else { Toast.makeText(getActivity(),"Favor de ingresar el(la) analista!", Toast.LENGTH_LONG).show(); }
                }else { Toast.makeText(getActivity(),"Favor de agregar una Unidad!", Toast.LENGTH_LONG).show(); }
            }else { Toast.makeText(getActivity(),"Favor de ingresar la Cantidad!", Toast.LENGTH_LONG).show(); }
        });

        botonCancelar.setOnClickListener(view -> alertDialog.dismiss());
        //FIN CONTENIDO DEL ALERT
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}