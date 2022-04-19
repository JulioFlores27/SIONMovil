package com.nervion.sionmovil.Vigilancia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nervion.sionmovil.Buscador.ViewPagerBuscador;
import com.nervion.sionmovil.R;

public class VigilanciaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vigilancia_activity);

        TabLayout tabLayout = findViewById(R.id.vigilanciaTab);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerVigilancia adapterVigilancia = new ViewPagerVigilancia(this);
        viewPager2.setAdapter(adapterVigilancia);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 1:
                    tab.setText("Salidas Vigilancia");
                    break;
                default:
                    tab.setText("Entradas Almacén");
                    break;
            }
        }).attach();
    }
}