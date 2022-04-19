package com.nervion.sionmovil.Buscador;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerBuscador extends FragmentStateAdapter {

    public ViewPagerBuscador (FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new StatusPedido();
            case 2:
                return new Entregados();
            default:
                return new ExistenciasGlobales();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
