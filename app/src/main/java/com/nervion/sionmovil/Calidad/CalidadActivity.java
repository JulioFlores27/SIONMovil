package com.nervion.sionmovil.Calidad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nervion.sionmovil.R;

public class CalidadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calidad_activity);

        TabLayout tabLayout = findViewById(R.id.calidadTab);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerCalidad adapterCalidad = new ViewPagerCalidad(this);
        viewPager2.setAdapter(adapterCalidad);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 1:
                    tab.setText("Planta");
                    break;
                case 2:
                    tab.setText("Historial");
                    break;
                case 3:
                    tab.setText("Lotes IyD");
                    break;
                default:
                    tab.setText("Calidad");
                    break;
            }
        }).attach();
    }
}