package com.nervion.sionmovil.Graficas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nervion.sionmovil.R;

public class GraficasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graficas_activity);
        TabLayout tabLayout = findViewById(R.id.graficasTab);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerGraficas adapterGraficas = new ViewPagerGraficas(this);
        viewPager2.setAdapter(adapterGraficas);
        viewPager2.setUserInputEnabled(false);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 1:
                    View tab1 = getLayoutInflater().inflate(R.layout.custom_tab, null);
                    tab1.findViewById(R.id.iconTab).setBackgroundResource(R.drawable.graficas_entinte);
                    tab.setCustomView(tab1);
                    break;
                case 2:
                    View tab2 = getLayoutInflater().inflate(R.layout.custom_tab, null);
                    tab2.findViewById(R.id.iconTab).setBackgroundResource(R.drawable.graficas_entregas);
                    tab.setCustomView(tab2);
                    break;
                default:
                    View tab3 = getLayoutInflater().inflate(R.layout.custom_tab, null);
                    tab3.findViewById(R.id.iconTab).setBackgroundResource(R.drawable.graficas_calidad);
                    tab.setCustomView(tab3);
                    break;
            }
        }).attach();
    }
}