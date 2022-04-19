package com.nervion.sionmovil.Movimientos;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerMovimientos extends FragmentStateAdapter {

    public ViewPagerMovimientos (FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new SolicitudPorFecha();
            case 2:
                return new MovimientosPorFecha();
            case 3:
                return new MovimientosPorProducto();
            default:
                return new PorSolicitud();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
