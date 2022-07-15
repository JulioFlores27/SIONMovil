package com.nervion.sionmovil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Login_Activity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
        }

        EditText loginCorreo = findViewById(R.id.loginCorreo);
        EditText loginPassword = findViewById(R.id.loginPassword);

        TextView loginCorreoTexto = findViewById(R.id.loginCorreoTexto);
        TextView loginPasswordTexto = findViewById(R.id.loginPasswordTexto);

        Button loginButton = findViewById(R.id.loginButton);

        loginCorreo.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) { loginCorreoTexto.setTextColor(Color.parseColor("#003C8F"));
            }else { loginCorreoTexto.setTextColor(Color.parseColor("#CFCFCF")); }
        });
        loginPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) { loginPasswordTexto.setTextColor(Color.parseColor("#003C8F"));
            }else { loginPasswordTexto.setTextColor(Color.parseColor("#CFCFCF")); }
        });

        loginButton.setOnClickListener(v -> {
            String correoTexto = loginCorreo.getText().toString();
            String passwordTexto = loginPassword.getText().toString();
            if (correoTexto.equals("") || passwordTexto.equals("")){
                Toast.makeText(this, "Los campos estan vacíos!", Toast.LENGTH_LONG).show();
            }else{
                firebaseAuth.signInWithEmailAndPassword(correoTexto, passwordTexto)
                        .addOnSuccessListener(authResult -> {
                            finish();
                            startActivity(new Intent(Login_Activity.this, MainActivity.class));
                        }).addOnFailureListener(e -> Toast.makeText(Login_Activity.this, "Cuenta No Existe", Toast.LENGTH_LONG).show());
            }
        });
    }
}
