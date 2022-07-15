package com.nervion.sionmovil.Surtir;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerSurtir extends FragmentStateAdapter {

    public ViewPagerSurtir (FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new SurtirGlobal();
            default:
                return new SurtirLocal();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
