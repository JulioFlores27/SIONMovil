package com.nervion.sionmovil.Inventarios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nervion.sionmovil.R;

public class InventariosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventarios_activity);

        TabLayout tabLayout = findViewById(R.id.inventariosTab);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerInventarios adapterInventarios = new ViewPagerInventarios(this);
        viewPager2.setAdapter(adapterInventarios);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 1:
                    tab.setText("Conteos");
                    break;
                case 2:
                    tab.setText("Reacomodo");
                    break;
                case 3:
                    tab.setText("Existencias Otra Fecha");
                    break;
                default:
                    tab.setText("Salidas");
                    break;
            }
        }).attach();
    }
}
