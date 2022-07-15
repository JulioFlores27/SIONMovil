package com.nervion.sionmovil.Surtir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.nervion.sionmovil.R;

public class SurtirGlobal extends Fragment {

    public SurtirGlobal() { /*Required empty public constructor*/ }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View surtir = inflater.inflate(R.layout.fragment_surtir_global, container, false);

        return surtir;
    }
}
