package com.nervion.sionmovil.Vigilancia;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerVigilancia extends FragmentStateAdapter {

    public ViewPagerVigilancia (FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new EntradasAlmacen();
            default:
                return new SalidasVigilancia();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
