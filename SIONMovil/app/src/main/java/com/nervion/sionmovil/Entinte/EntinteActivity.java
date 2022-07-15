package com.nervion.sionmovil.Entinte;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nervion.sionmovil.R;

public class EntinteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entinte_activity);
        TabLayout tabLayout = findViewById(R.id.entinteTab);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerEntinte adapterEntinte = new ViewPagerEntinte(this);
        viewPager2.setAdapter(adapterEntinte);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 1:
                    tab.setText("Historial");
                    break;
                default:
                    tab.setText("Asignar");
                    break;
            }
        }).attach();
    }
}