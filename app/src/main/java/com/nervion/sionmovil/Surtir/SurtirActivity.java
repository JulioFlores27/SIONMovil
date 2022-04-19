package com.nervion.sionmovil.Surtir;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nervion.sionmovil.Penalizacion.ViewPagerPenalizacion;
import com.nervion.sionmovil.R;

public class SurtirActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surtir_activity);

        TabLayout tabLayout = findViewById(R.id.penalizacionTab);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerSurtir adapterSurtir = new ViewPagerSurtir(this);
        viewPager2.setAdapter(adapterSurtir);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 1:
                    tab.setText("Global");
                    break;
                default:
                    tab.setText("Local");
                    break;
            }
        }).attach();
    }
}
