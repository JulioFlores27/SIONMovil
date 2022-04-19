package com.nervion.sionmovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UsuarioActivity extends AppCompatActivity {

    private String firebaseEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario_activity);

        EditText crearCorreo = findViewById(R.id.crearCorreo);
        EditText crearPassword = findViewById(R.id.crearPassword);
        EditText crearNombre= findViewById(R.id.crearNombre);

        TextView crearCorreoTexto = findViewById(R.id.crearCorreoTexto);
        TextView crearPasswordTexto = findViewById(R.id.crearPasswordTexto);
        TextView crearNombreTexto = findViewById(R.id.crearNombreTexto);
        TextView crearAreaTexto = findViewById(R.id.crearAreaTexto);

        Spinner crearArea = findViewById(R.id.crearArea);

        String areas[] = {"Selecciona su Área:","Almacén","Calidad","Operaciones","Producción","Sistemas","Ventas"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.configuracion_adapter, areas);
        crearArea.setAdapter(arrayAdapter);

        Button crearButton = findViewById(R.id.crearButton);

        crearCorreo.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) { crearCorreoTexto.setTextColor(Color.parseColor("#003C8F"));
            }else { crearCorreoTexto.setTextColor(Color.parseColor("#CFCFCF")); }
        });
        crearPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) { crearPasswordTexto.setTextColor(Color.parseColor("#003C8F"));
            }else { crearPasswordTexto.setTextColor(Color.parseColor("#CFCFCF")); }
        });
        crearNombre.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) { crearNombreTexto.setTextColor(Color.parseColor("#003C8F"));
            }else { crearNombreTexto.setTextColor(Color.parseColor("#CFCFCF")); }
        });

        crearArea.setFocusable(true);
        crearArea.setFocusableInTouchMode(true);
        crearArea.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                crearArea.performClick();
                crearAreaTexto.setTextColor(Color.parseColor("#003C8F"));
            }else { crearAreaTexto.setTextColor(Color.parseColor("#CFCFCF")); }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        crearButton.setOnClickListener(v -> {
            String correoTexto = crearCorreo.getText().toString();
            String passwordTexto = crearPassword.getText().toString();
            String nombreTexto = crearNombre.getText().toString();
            String selectArea = crearArea.getSelectedItem().toString();

            if (correoTexto.equals("") || passwordTexto.equals("") || nombreTexto.equals("") || selectArea.equals("Selecciona su Área:")){
                Toast.makeText(this, "Los campos estan vacíos!", Toast.LENGTH_LONG).show();
            }else{
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.usuario_builder, null);
                dialogBuilder.setView(dialogView);

                //INICIO CONTENIDO DEL ALERT
                EditText confirmarPassword = dialogView.findViewById(R.id.confirmarPassword);

                TextView textView = dialogView.findViewById(R.id.tituloText);
                textView.setText(correoTexto);

                Button buttonConfirmar = dialogView.findViewById(R.id.botonConfirmar);
                buttonConfirmar.setOnClickListener(v1 -> {
                    final String passwordBuilder = confirmarPassword.getText().toString();

                    if (!passwordBuilder.equals("")) {
                        firebaseAuth.signInWithEmailAndPassword(firebaseEmail, passwordBuilder).addOnCompleteListener(task -> {
                            if (task.isComplete()){
                                firebaseAuth.createUserWithEmailAndPassword(correoTexto, passwordTexto).addOnSuccessListener(authResult -> {

                                    FirebaseUser correoCreada = authResult.getUser();
                                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(nombreTexto).build();
                                    correoCreada.updateProfile(request).addOnSuccessListener(unused -> {
                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        Map<String, Object> campos = new HashMap<>();
                                        String decodedArea = new String(selectArea.getBytes(StandardCharsets.UTF_8));
                                        campos.put("area", decodedArea);
                                        campos.put("correo", correoTexto);
                                        campos.put("idtoken", correoCreada.getUid());
                                        String decoded = new String(nombreTexto.getBytes(StandardCharsets.UTF_8));
                                        campos.put("nombre", decoded);

                                        firestore.collection("usuarios").document(correoTexto).set(campos).addOnSuccessListener(documentReference -> {
                                            firebaseAuth.signInWithEmailAndPassword(firebaseEmail, passwordBuilder).addOnCompleteListener(task1 -> {
                                                if (task1.isComplete()){
                                                    Toast.makeText(this, "Cuenta Creada", Toast.LENGTH_SHORT).show();
                                                    alertDialog.dismiss();
                                                    finish();
                                                }
                                            });
                                        });
                                    }).addOnFailureListener(e -> Toast.makeText(UsuarioActivity.this, "No se pudo guardar el nombre", Toast.LENGTH_LONG).show());

                                }).addOnFailureListener(e -> {
                                    Toast.makeText(UsuarioActivity.this, "Error: "+e, Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                });
                            }else{ Toast.makeText(this, "Contraseña Equivocada", Toast.LENGTH_LONG).show(); }
                        });
                    }else { Toast.makeText(this, "El campo esta vacío!", Toast.LENGTH_LONG).show(); }
                });

                Button botonCancelar = dialogView.findViewById(R.id.botonCancelar);
                botonCancelar.setOnClickListener(v2 -> {
                    alertDialog.dismiss();
                });
                //FIN CONTENIDO DEL ALERT

                alertDialog = dialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
    }
}