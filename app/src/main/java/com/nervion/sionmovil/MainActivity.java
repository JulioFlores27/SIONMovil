package com.nervion.sionmovil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nervion.sionmovil.Buscador.BuscadorActivity;
import com.nervion.sionmovil.Calidad.CalidadActivity;
import com.nervion.sionmovil.Entinte.EntinteActivity;
import com.nervion.sionmovil.Entregas.EntregasActivity;
import com.nervion.sionmovil.Graficas.GraficasActivity;
import com.nervion.sionmovil.Inventarios.InventariosActivity;
import com.nervion.sionmovil.Movimientos.MovimientosActivity;
import com.nervion.sionmovil.Penalizacion.PenalizacionActivity;
import com.nervion.sionmovil.Surtir.SurtirActivity;
import com.nervion.sionmovil.Vigilancia.VigilanciaActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView mainText, userText;
    private AlertDialog alertDialog;
    public String tiendaGlobal;
    private final FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userText = findViewById(R.id.usuarioTextView);
        if (firebaseAuth != null) {
            userText.setText("Bienvenido:\n"+firebaseAuth.getDisplayName());

            firebaseAuth.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                String tokenID = getTokenResult.getToken();
            });
        }

        SharedPreferences resultadoTienda = getSharedPreferences("Tienda", Context.MODE_PRIVATE);
        tiendaGlobal = resultadoTienda.getString("TiendaGlobal", "");
        mainText = findViewById(R.id.tiendaTextView);
        if(!tiendaGlobal.equals("")){ mainText.setText("Tienda: "+tiendaGlobal); }else{ alertTienda(); }
        recyclerView = findViewById(R.id.rvModulo);

        List<MainActivity_Constructor> modulos = new ArrayList<>();
        modulos.add(new MainActivity_Constructor("Entregas", R.drawable.entregas));
        modulos.add(new MainActivity_Constructor("Inventarios", R.drawable.inventarios));
        modulos.add(new MainActivity_Constructor("Buscador", R.drawable.buscador));
        modulos.add(new MainActivity_Constructor("Movimientos", R.drawable.movimientos));
        modulos.add(new MainActivity_Constructor("Vigilancia", R.drawable.vigilancia));
        modulos.add(new MainActivity_Constructor("Entinte", R.drawable.entinte));
        modulos.add(new MainActivity_Constructor("Surtir", R.drawable.surtir));
        modulos.add(new MainActivity_Constructor("Calidad", R.drawable.calidad));
        modulos.add(new MainActivity_Constructor("Gráficas", R.drawable.graficas));
        modulos.add(new MainActivity_Constructor("Penalización", R.drawable.penalizacion));
        modulos.add(new MainActivity_Constructor("Configuración", R.drawable.configuracion));
        if (firebaseAuth.getEmail().equals("julio@nervion.com.mx") || firebaseAuth.getEmail().equals("javierb@nervion.com.mx")) {
            modulos.add(new MainActivity_Constructor("Crear Usuario", R.drawable.usuarios));
        }

        MainActivity_Adaptor adapter = new MainActivity_Adaptor(this, modulos);
        recyclerView.setAdapter(adapter);
        layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setOnItemClickListener((v, position) -> {
            int opciones = recyclerView.getChildAdapterPosition(v);
            switch (opciones){
                case 0:
                    startActivity(new Intent(MainActivity.this, EntregasActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(MainActivity.this, InventariosActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(MainActivity.this, BuscadorActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(MainActivity.this, MovimientosActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(MainActivity.this, VigilanciaActivity.class));
                    break;
                case 5:
                    startActivity(new Intent(MainActivity.this, EntinteActivity.class));
                    break;
                case 6:
                    startActivity(new Intent(MainActivity.this, SurtirActivity.class));
                    break;
                case 7:
                    startActivity(new Intent(MainActivity.this, CalidadActivity.class));
                    break;
                case 8:
                    startActivity(new Intent(MainActivity.this, GraficasActivity.class));
                    break;
                case 9:
                    startActivity(new Intent(MainActivity.this, PenalizacionActivity.class));
                    break;
                case 10:
                    alertTienda();
                    break;
                case 11:
                    startActivity(new Intent(MainActivity.this, UsuarioActivity.class));
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Módulo No Seleccionado", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void alertTienda(){
        String[] tiendas = {"Seleciona:", "01", "55", "56", "58", "A0", "A3", "A5", "A8", "AG", "AI", "AK", "AL", "AM", "Pruebas"};

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.configuracion_activity, null);
        dialogBuilder.setView(dialogView);

        //INICIO CONTENIDO DEL ALERT
        TextView tiendaTV = dialogView.findViewById(R.id.tituloText);
        Spinner spinner = dialogView.findViewById(R.id.tiendaSpinner);
        Button button = dialogView.findViewById(R.id.tiendaButton);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.configuracion_adapter, tiendas);
        spinner.setAdapter(arrayAdapter);

        if(!tiendaGlobal.equals("")){ tiendaTV.setText("Seleccione Tienda: "+tiendaGlobal); }

        button.setOnClickListener(v9 -> {
            String selectTienda = spinner.getSelectedItem().toString();
            if(!selectTienda.equals("Seleciona:")){
                SharedPreferences sharedPreferences = getSharedPreferences("Tienda", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("TiendaGlobal", selectTienda);
                editor.apply();
                alertDialog.dismiss();
                /*ESTE CODIGO PERMITE REINICIAR LA APLICACION SIN PARPADEAR LA ACTIVIDAD QUE ESTA*/
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                /*FIN DEL CODIGO*/
            }else{ Toast.makeText(MainActivity.this, "Tiene que seleccionar una tienda", Toast.LENGTH_SHORT).show(); }
        });
        //FIN CONTENIDO DEL ALERT

        alertDialog = dialogBuilder.create();
        if(tiendaGlobal.equals("")){ alertDialog.setCancelable(false); }
        alertDialog.show();
        //ESTE alertDialog, es para ampliar un poco mas la vista y asi tener un diseño adecuado
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}