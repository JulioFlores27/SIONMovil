package com.nervion.sionmovil.Buscador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nervion.sionmovil.R;

public class BuscadorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);

        TabLayout tabLayout = findViewById(R.id.buscadorTab);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerBuscador adapterBuscador = new ViewPagerBuscador(this);
        viewPager2.setAdapter(adapterBuscador);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 1:
                    tab.setText("Status Pedido");
                    break;
                case 2:
                    tab.setText("Entregados");
                    break;
                default:
                    tab.setText("Existencias Globales");
                    break;
            }
        }).attach();
    }
}