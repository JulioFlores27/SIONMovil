package com.nervion.sionmovil.Entinte;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.nervion.sionmovil.Vigilancia.EntradasAlmacen;
import com.nervion.sionmovil.Vigilancia.SalidasVigilancia;

public class ViewPagerEntinte extends FragmentStateAdapter {

    public ViewPagerEntinte (FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new EntinteHistorial();
            default:
                return new EntinteAsignar();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
