package com.nervion.sionmovil.Calidad;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class Calidad extends Fragment {

    public Calidad() { }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private final String isCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    private AutoCompleteTextView ccNombre;

    private RadioGroup ccOpciones;
    private RadioButton radioButton = null;
    private Button ccAgregar;
    private ImageButton ccFiltrar, ccActualizar;
    private EditText ccLote;
    private String textoActualizar = "", textoRadioGroup="", checkFiltrar = "", checkMoler = "", checkPNC = "";
    private RecyclerView recyclerView;

    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    private final DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat dateFormat3 = new SimpleDateFormat("kk:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View calidad = inflater.inflate(R.layout.fragment_calidad, container, false);

        ccOpciones = calidad.findViewById(R.id.ccOpciones);
        ccAgregar = calidad.findViewById(R.id.ccAgregar);
        ccNombre = calidad.findViewById(R.id.ccNombre);
        ccLote = calidad.findViewById(R.id.ccLote);
        ccFiltrar = calidad.findViewById(R.id.ccFiltrar);
        ccActualizar = calidad.findViewById(R.id.ccActualizar);
        recyclerView = calidad.findViewById(R.id.rvCalidad);

        FirebaseFirestore.getInstance().collection("usuarios").whereIn("area", Arrays.asList("Almacén", "Producción"))
                .addSnapshotListener((value, error) -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line);
            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    String nombres = documentChange.getDocument().getData().get("nombre").toString();
                    adapter.add(nombres);
                }
            }
            ccNombre.setAdapter(adapter);
            ccNombre.setOnFocusChangeListener((v, hasFocus) -> ccNombre.showDropDown());
        });

        ccAgregar.setOnClickListener(view -> {
            int selectedId = ccOpciones.getCheckedRadioButtonId();
            radioButton = calidad.findViewById(selectedId);
            if (radioButton != null) {
                if (!ccNombre.getText().toString().equals("") && !ccLote.getText().toString().equals("")) {
                    if (ccLote.length() == 5) {
                        Date date = Calendar.getInstance().getTime();
                        String dateTime = dateFormat1.format(date.getTime());
                        String fecha = dateFormat2.format(date.getTime());
                        String hora = dateFormat3.format(date.getTime());

                        if (radioButton.getText().equals("D")) { textoRadioGroup = "Dispersión";
                        }else if (radioButton.getText().equals("M")) { textoRadioGroup = "Molienda";
                        }else if (radioButton.getText().equals("T")) { textoRadioGroup = "Terminación"; }

                        String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Calidad/Cal_Calidad_AgregarMuestra.php?Lote="+ccLote.getText()
                                +"&Usuario="+ccNombre.getText()+"&Observaciones="+textoRadioGroup+"&Fecha="+fecha+"&Hora="+hora
                                +"&DateTime="+dateTime+"&UsuarioApp="+isCorreo).replaceAll(" ","%20");

                        asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode == 200) {
                                    Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                                    ccNombre.setText("");
                                    ccLote.setText("");
                                    ccOpciones.clearCheck();
                                    textoActualizar = "Actualizando...";
                                    DescargarCalidad(textoActualizar);
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else { Toast.makeText(getContext(), "Favor de Verificar el Lote!", Toast.LENGTH_SHORT).show(); }
                }else{ Toast.makeText(getContext(), "Favor de Verificar sus Datos", Toast.LENGTH_SHORT).show(); }
            }else{ Toast.makeText(getContext(), "Favor de Verificar sus Datos", Toast.LENGTH_SHORT).show(); }
        });

        ccFiltrar.setOnClickListener(view -> {
            textoActualizar = "Actualizando\nFiltro...";
            DescargarCalidad(textoActualizar);
        });
        ccActualizar.setOnClickListener(view -> {
            textoActualizar = "Actualizando...";
            DescargarCalidad(textoActualizar);
        });

        DescargarCalidad(textoActualizar);

        return calidad;
    }

    private void DescargarCalidad(String textoActualizar) {
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
        String url;
        if (textoActualizar.equals("Actualizando\nFiltro...")) {
            url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Calidad/Cal_Calidad_PNC.php";
        }else { url = "http://websion.hol.es/Aplicacion_DispositivoMovil/Calidad/Cal_Calidad.php"; }
        asyncHttpClient.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) { ListaCalidad(new String(responseBody)); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"No hay conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ListaCalidad(String respuesta) {
        List<Calidad_Constructor> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(respuesta);
            for (int i=0; i<jsonArray.length(); i++) {
                Calidad_Constructor listaCalidad = new Calidad_Constructor();
                listaCalidad.setCcID(jsonArray.getJSONObject(i).getInt("ID"));
                listaCalidad.setCcLote(jsonArray.getJSONObject(i).getInt("Lote"));
                listaCalidad.setCcUsuario(jsonArray.getJSONObject(i).getString("Usuario"));
                listaCalidad.setCcUnidad(jsonArray.getJSONObject(i).getString("Unidad"));
                listaCalidad.setCcMP(jsonArray.getJSONObject(i).getString("MP"));
                listaCalidad.setCcObservacion(jsonArray.getJSONObject(i).getString("Observaciones"));
                listaCalidad.setCcCantidad(jsonArray.getJSONObject(i).getDouble("Cantidad"));
                listaCalidad.setCcFecha(jsonArray.getJSONObject(i).getString("Fecha"));
                listaCalidad.setCcHora(jsonArray.getJSONObject(i).getString("Hora"));
                list.add(listaCalidad);
            }

            Calidad_Adapter adapter = new Calidad_Adapter(getContext(), list);
            recyclerView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();

            adapter.setOnItemClickListener((view, position) -> {
                Calidad_Constructor getCalidad = list.get(position);
                final String ccLote = String.valueOf(getCalidad.getCcLote());
                final String ccID = String.valueOf(getCalidad.getCcID());
                final String ccComentarioCalidad = getCalidad.getCcObservacion();
                ccClickBuilder(ccID, ccLote, ccComentarioCalidad);
            });
        }catch (Exception e) {
            Toast.makeText(getActivity(),"\n\n\n\n\n\n\n\n\n\nError: No hay conexion al sistema\n\n\n\n\n\n\n\n\n\n", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.dismiss();
        }
    }

    private void ccClickBuilder(String ccID, String ccLote, String ccComentarioCalidad) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.calidad_click_builder, null);
        dialogBuilder.setView(dialogView);
        //INICIO CONTENIDO DEL ALERT
        TextView ccAnalisisLote = dialogView.findViewById(R.id.ccAnalisisLote);
        ccAnalisisLote.setText("Análisis Lote: "+ccLote);

        String[] spinnerViscosidad = {"--","s","cP"};
        Spinner ccSpinnerViscosidad = dialogView.findViewById(R.id.ccSpinnerViscosidad);
        ccSpinnerViscosidad.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerViscosidad));

        String[] spinnerCantidad = {"Elige","g","kg","L","ml"};
        Spinner ccSpinnerCantidad = dialogView.findViewById(R.id.ccSpinnerCantidad);
        ccSpinnerCantidad.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerCantidad));

        AutoCompleteTextView ccAnalista = dialogView.findViewById(R.id.ccAnalista);
        FirebaseFirestore.getInstance().collection("usuarios").whereEqualTo("area", "Calidad").addSnapshotListener((value, error) -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line);
            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    String nombres = documentChange.getDocument().getData().get("nombre").toString();
                    adapter.add(nombres);
                }
            }
            ccAnalista.setAdapter(adapter);
            ccAnalista.setOnFocusChangeListener((v, hasFocus) -> ccAnalista.showDropDown());
        });

        EditText ccViscosidad = dialogView.findViewById(R.id.ccViscosidad);
        EditText ccDensidad = dialogView.findViewById(R.id.ccDensidad);
        EditText ccSolidos = dialogView.findViewById(R.id.ccSolidos);
        EditText ccBrillo = dialogView.findViewById(R.id.ccBrillo);
        EditText ccCantidad = dialogView.findViewById(R.id.ccCantidad);
        EditText ccMateriaPrima = dialogView.findViewById(R.id.ccMateriaPrima);

        ccViscosidad.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) { if (keyCode == KeyEvent.KEYCODE_ENTER) { if (ccViscosidad.length() == 0) { ccViscosidad.setText("0"); } } }
            return false;
        });
        ccDensidad.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) { if (keyCode == KeyEvent.KEYCODE_ENTER) { if (ccDensidad.length() == 0) { ccDensidad.setText("0"); } } }
            return false;
        });
        ccSolidos.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) { if (keyCode == KeyEvent.KEYCODE_ENTER) { if (ccSolidos.length() == 0) { ccSolidos.setText("0"); } } }
            return false;
        });
        ccBrillo.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) { if (keyCode == KeyEvent.KEYCODE_ENTER) { if (ccBrillo.length() == 0) { ccBrillo.setText("0"); } } }
            return false;
        });

        CheckBox ccCheckFiltrar = dialogView.findViewById(R.id.ccCheckFiltrar);
        CheckBox ccCheckMoler = dialogView.findViewById(R.id.ccCheckMoler);
        CheckBox ccCheckPNC = dialogView.findViewById(R.id.ccCheckPNC);
        ccCheckFiltrar.setOnCheckedChangeListener((compoundButton, isCheck) -> {
            if (isCheck){
                checkFiltrar = ccCheckFiltrar.getText().toString();
                checkPNC = "";
                ccCheckPNC.setChecked(false);
            }else { checkFiltrar = ""; }
        });
        ccCheckMoler.setOnCheckedChangeListener((compoundButton, isCheck) -> {
            if (isCheck){
                checkMoler = ccCheckMoler.getText().toString();
                checkPNC = "";
                ccCheckPNC.setChecked(false);
            }else { checkMoler = ""; }
        });
        ccCheckPNC.setOnCheckedChangeListener((compoundButton, isCheck) -> {
            if (isCheck) {
                checkPNC = ccCheckPNC.getText().toString();
                checkFiltrar = "";
                checkMoler = "";
                ccCantidad.setText("");
                ccMateriaPrima.setText("");
                ccSpinnerCantidad.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerCantidad));
                ccCantidad.setEnabled(false);
                ccMateriaPrima.setEnabled(false);
                ccSpinnerCantidad.setEnabled(false);
                ccCheckFiltrar.setChecked(false);
                ccCheckMoler.setChecked(false);
            }else{
                checkPNC = "";
                ccCantidad.setEnabled(true);
                ccMateriaPrima.setEnabled(true);
                ccSpinnerCantidad.setEnabled(true);
            }
        });

        EditText ccObservaciones = dialogView.findViewById(R.id.ccObservaciones);

        Button botonAjuste = dialogView.findViewById(R.id.botonAjuste);
        Button botonPreaprobar = dialogView.findViewById(R.id.botonPreaprobar);
        Button botonLiberar = dialogView.findViewById(R.id.botonLiberar);

        botonLiberar.setOnClickListener(view -> {
            Date date = Calendar.getInstance().getTime();
            String dateTime = dateFormat1.format(date.getTime());
            String fecha = dateFormat2.format(date.getTime());
            String hora = dateFormat3.format(date.getTime());
            if (ccComentarioCalidad.equals("Realizado")){
                if (!ccViscosidad.getText().toString().equals("")) {
                    if (!ccMateriaPrima.getText().toString().equals("") || checkFiltrar.equals("Filtrar") || checkMoler.equals("Moler")) {
                        if (!ccAnalista.getText().toString().equals("")) {
                            if (!ccDensidad.getText().toString().equals("") || !ccSolidos.getText().toString().equals("") || !ccBrillo.getText().toString().equals("")) {
                                String mP = (ccMateriaPrima.getText()+" "+checkFiltrar+" "+checkMoler).trim();
                                String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Calidad/Cal_Calidad_AlertPress_EnviarAjusteLiberarYPreaprobarLote.php?ID="+ccID
                                        +"&Lote="+ccLote+"&Usuario="+ccAnalista.getText()+"&Observaciones=Aprobado&Cantidad=0&Unidad=&MP="+mP
                                        +"&Viscosidad="+ccViscosidad.getText()+"&UnidadVisc="+ccSpinnerViscosidad.getSelectedItem()
                                        +"&Densidad="+ccDensidad.getText()+"&Solidos="+ccSolidos.getText()+"&Brillo="+ccBrillo.getText()
                                        +"&Observaciones2="+ccObservaciones.getText()+"&Fecha="+fecha+"&Hora="+hora+"&Aprobado=1&DepartamentoOriginal=Planta"
                                        +"&DepartamentoActual=Planta aprobado&DateTime="+dateTime+"&UsuarioApp="+isCorreo).replaceAll(" ","%20");
                                asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        if (statusCode == 200) {
                                            Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                                            ccOpciones.clearCheck();
                                            alertDialog.dismiss();
                                            textoActualizar = "Actualizando...";
                                            DescargarCalidad(textoActualizar);
                                        }
                                    }
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else {
                                Toast.makeText(getActivity(), "Ingresar cantidad 0 o mayor en los siguientes campos:" +
                                        "\n\"Densidad\"\n\"Sólidos\"\n\"Brillo\"", Toast.LENGTH_LONG).show();
                            }
                        }else { Toast.makeText(getActivity(),"Tienes que ingresar el(la) Analista", Toast.LENGTH_LONG).show(); }
                    }else { Toast.makeText(getActivity(), "Favor de llenar el campo de Materia Prima o eligir la opciones de Moler y/o Filtrar!", Toast.LENGTH_LONG).show(); }
                }else { Toast.makeText(getActivity(),"Ingresar cantidad 0 o mayor en el campo de Viscosidad!", Toast.LENGTH_LONG).show(); }
            }else{
                String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Calidad/Cal_Calidad_AlertPress_EnviarAjusteLiberarYPreaprobarLote.php?ID="+ccID
                        +"&Lote="+ccLote+"&Usuario="+ccAnalista.getText()+"&Observaciones=Aprobado&Cantidad=0&Unidad=&MP=&Viscosidad="+ccViscosidad.getText()
                        +"&UnidadVisc="+ccSpinnerViscosidad.getSelectedItem()+"&Densidad="+ccDensidad.getText()+"&Solidos="+ccSolidos.getText()
                        +"&Brillo="+ccBrillo.getText()+"&Observaciones2="+ccObservaciones.getText()+"&Fecha="+fecha+"&Hora="+hora
                        +"&Aprobado=1&DepartamentoOriginal=Calidad&DepartamentoActual=Planta aprobado&DateTime="+dateTime
                        +"&UsuarioApp="+isCorreo).replaceAll(" ","%20");
                asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                            ccOpciones.clearCheck();
                            alertDialog.dismiss();
                            textoActualizar = "Actualizando...";
                            DescargarCalidad(textoActualizar);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        botonPreaprobar.setOnClickListener(view -> {
            Date date = Calendar.getInstance().getTime();
            String dateTime = dateFormat1.format(date.getTime());
            String fecha = dateFormat2.format(date.getTime());
            String hora = dateFormat3.format(date.getTime());
            if (!ccAnalista.getText().toString().equals("")) {
                String unidad = String.valueOf(ccSpinnerCantidad.getSelectedItem());

                if (unidad.equals("Elige")){ unidad = ""; }

                String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Calidad/Cal_Calidad_AlertPress_EnviarAjusteLiberarYPreaprobarLote.php?ID="+ccID
                        +"&Lote="+ccLote+"&Usuario="+ccAnalista.getText()+"&Observaciones=Preaprobado&Cantidad=0&Unidad="+unidad+"&MP=&Viscosidad="
                        +"&UnidadVisc=&Densidad=&Solidos=&Brillo=&Observaciones2=&Fecha="+fecha+"&Hora="+hora+"&Aprobado=2&DepartamentoOriginal=Calidad"
                        + "&DepartamentoActual=Calidad&DateTime="+dateTime+"&UsuarioApp="+isCorreo).replaceAll(" ","%20");

                asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                            ccOpciones.clearCheck();
                            alertDialog.dismiss();
                            textoActualizar = "Actualizando...";
                            DescargarCalidad(textoActualizar);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                    }
                });
            }else { Toast.makeText(getActivity(),"Tienes que ingresar el(la) Analista", Toast.LENGTH_LONG).show(); }
        });

        botonAjuste.setOnClickListener(view -> {
            Date date = Calendar.getInstance().getTime();
            String dateTime = dateFormat1.format(date.getTime());
            String fecha = dateFormat2.format(date.getTime());
            String hora = dateFormat3.format(date.getTime());
            if (checkPNC.equals("PNC")) {
                if (!ccAnalista.getText().toString().equals("")) {
                    String unidad = String.valueOf(ccSpinnerCantidad.getSelectedItem());

                    if (unidad.equals("Elige")){ unidad = ""; }

                    String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Calidad/Cal_Calidad_AlertPress_EnviarAjusteLiberarYPreaprobarLote.php?ID="+ccID
                            +"&Lote="+ccLote+"&Usuario="+ccAnalista.getText()+"&Observaciones=PNC&Cantidad="+ccCantidad.getText()
                            +"&Unidad="+unidad+"&MP="+ccMateriaPrima.getText()+"&Viscosidad="+ccViscosidad.getText()
                            +"&UnidadVisc="+ccSpinnerViscosidad.getSelectedItem()+"&Densidad="+ccDensidad.getText()+"&Solidos="+ccSolidos.getText()
                            +"&Brillo="+ccBrillo.getText()+"&Observaciones2="+ccObservaciones.getText()+"&Fecha="+fecha+"&Hora="+hora
                            +"&Aprobado=0&DepartamentoOriginal=Planta&DepartamentoActual=Planta ajuste&DateTime="+dateTime
                            +"&UsuarioApp="+isCorreo).replaceAll(" ","%20");

                    asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                                ccOpciones.clearCheck();
                                alertDialog.dismiss();
                                textoActualizar = "Actualizando...";
                                DescargarCalidad(textoActualizar);
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                        }
                    });
                }else { Toast.makeText(getActivity(),"Tienes que ingresar el(la) Analista", Toast.LENGTH_LONG).show(); }
            }else {
                if (!ccViscosidad.getText().toString().equals("")) {
                    if (!ccCantidad.getText().toString().equals("")) {
                        String unidad = String.valueOf(ccSpinnerCantidad.getSelectedItem());
                        if (!unidad.equals("Elige")) {
                            if (!ccMateriaPrima.getText().toString().equals("") || checkFiltrar.equals("Filtrar") || checkMoler.equals("Moler")) {
                                if (!ccAnalista.getText().toString().equals("")) {
                                    String mP = (ccMateriaPrima.getText()+" "+checkFiltrar+" "+checkMoler).trim();
                                    String insertUrl = ("https://sionm.tech/Aplicacion_DispositivoMovil/Calidad/Cal_Calidad_AlertPress_EnviarAjusteLiberarYPreaprobarLote.php?ID="+ccID
                                            +"&Lote="+ccLote+"&Usuario="+ccAnalista.getText()+"&Observaciones=Otro ajuste&Cantidad="+ccCantidad.getText()
                                            +"&Unidad="+unidad+"&MP="+mP+"&Viscosidad="+ccViscosidad.getText()+"&UnidadVisc="+ccSpinnerViscosidad.getSelectedItem()
                                            +"&Densidad="+ccDensidad.getText()+"&Solidos="+ccSolidos.getText()+"&Brillo="+ccBrillo.getText()
                                            +"&Observaciones2="+ccObservaciones.getText()+"&Fecha="+fecha+"&Hora="+hora+"&Aprobado=0&DepartamentoOriginal=Planta"
                                            +"&DepartamentoActual=Planta ajuste&DateTime="+dateTime+"&UsuarioApp="+isCorreo).replaceAll(" ","%20");
                                    asyncHttpClient.post(insertUrl, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            if (statusCode == 200) {
                                                Toast.makeText(getActivity(), "Se han registrado los datos",Toast.LENGTH_SHORT).show();
                                                ccOpciones.clearCheck();
                                                alertDialog.dismiss();
                                                textoActualizar = "Actualizando...";
                                                DescargarCalidad(textoActualizar);
                                            }
                                        }
                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            Toast.makeText(getActivity(),"NO SE REGISTRARON LOS DATOS", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else { Toast.makeText(getActivity(),"Tienes que ingresar el(la) Analista", Toast.LENGTH_LONG).show(); }
                            }else {
                                Toast.makeText(getActivity(),"Favor de llenar el campo de Materia Prima o"
                                        +"eligir la opciones de Moler y/o Filtrar!", Toast.LENGTH_LONG).show();
                            }
                        }else { Toast.makeText(getActivity(),"Favor de seleccionar la Unidad deseada para la Cantidad Ingresada!", Toast.LENGTH_LONG).show(); }
                    }else { Toast.makeText(getActivity(),"Ingresar cantidad 0 o mayor en el campo de Cantidad!", Toast.LENGTH_LONG).show(); }
                }else { Toast.makeText(getActivity(),"Ingresar cantidad 0 o mayor en el campo de Viscosidad!", Toast.LENGTH_LONG).show(); }
            }
        });
        //FIN CONTENIDO DEL ALERT
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}