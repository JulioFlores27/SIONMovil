package com.nervion.sionmovil.Graficas;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerGraficas extends FragmentStateAdapter {

    public ViewPagerGraficas (FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new GraficasEntinte();
            case 2:
                return new GraficasEntregas();
            default:
                return new GraficasCalidad();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
