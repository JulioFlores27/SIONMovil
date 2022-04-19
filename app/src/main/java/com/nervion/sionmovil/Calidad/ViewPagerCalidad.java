package com.nervion.sionmovil.Calidad;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerCalidad  extends FragmentStateAdapter {

    public ViewPagerCalidad (FragmentActivity fragmentActivity) { super(fragmentActivity); }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:
                    return new CalidadPlanta();
                case 2:
                    return new CalidadHistorial();
                case 3:
                    return new CalidadLotesIyD();
                default:
                    return new Calidad();
            }
        }

        @Override
        public int getItemCount() { return 4; }
}
