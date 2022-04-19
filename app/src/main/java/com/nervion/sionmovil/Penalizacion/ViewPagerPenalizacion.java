package com.nervion.sionmovil.Penalizacion;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerPenalizacion extends FragmentStateAdapter {

    public ViewPagerPenalizacion (FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            default:
                return new PenalizacionAlmacen();
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
