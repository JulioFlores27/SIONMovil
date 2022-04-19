package com.nervion.sionmovil.Inventarios;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerInventarios extends FragmentStateAdapter {

    public ViewPagerInventarios (FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new InventariosConteo();
            case 2:
                return new InventariosReacomodo();
            case 3:
                return new InventariosExistencias();
            default:
                return new InventariosSalidas();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
