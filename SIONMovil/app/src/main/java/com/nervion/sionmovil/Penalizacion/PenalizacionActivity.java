package com.nervion.sionmovil.Penalizacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nervion.sionmovil.R;

public class PenalizacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.penalizacion_activity);

        TabLayout tabLayout = findViewById(R.id.penalizacionTab);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerPenalizacion adapterPenalizacion = new ViewPagerPenalizacion(this);
        viewPager2.setAdapter(adapterPenalizacion);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                default:
                    tab.setText("Almacen");
                    break;
            }
        }).attach();
    }
}
