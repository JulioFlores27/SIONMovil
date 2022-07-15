package com.nervion.sionmovil.Movimientos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nervion.sionmovil.R;

public class MovimientosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        TabLayout tabLayout = findViewById(R.id.movimientosTab);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerMovimientos adapterMovimientos = new ViewPagerMovimientos(this);
        viewPager2.setAdapter(adapterMovimientos);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 1:
                    tab.setText("Sol. Por Fecha");
                    break;
                case 2:
                    tab.setText("Mov. Por Fecha");
                    break;
                case 3:
                    tab.setText("Mov. Por Producto");
                    break;
                default:
                    tab.setText("Por Solicitud");
                    break;
            }
        }).attach();
    }
}