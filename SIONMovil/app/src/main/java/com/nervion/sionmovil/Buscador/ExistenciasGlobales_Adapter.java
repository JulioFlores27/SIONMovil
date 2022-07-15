package com.nervion.sionmovil.Buscador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class ExistenciasGlobales_Adapter extends RecyclerView.Adapter<ExistenciasGlobales_Adapter.ViewHolder> {

    protected List<ExistenciasGlobales_Constructor> listaBuscador;
    private final Context contexto;

    public ExistenciasGlobales_Adapter(Context contexto, List<ExistenciasGlobales_Constructor> listaBuscador){
        this.listaBuscador = listaBuscador;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView producto, envase, cantidad, tienda;

        public ViewHolder(View itemView) {
            super(itemView);
            producto = itemView.findViewById(R.id.egProducto);
            envase = itemView.findViewById(R.id.egEnvase);
            cantidad = itemView.findViewById(R.id.egCantidad);
            tienda = itemView.findViewById(R.id.egTienda);
        }
    }

    @Override
    public ExistenciasGlobales_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.buscador_existencias_globales_adapter, null);
        return new ExistenciasGlobales_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ExistenciasGlobales_Adapter.ViewHolder holder, int position) {
        ExistenciasGlobales_Constructor existenciasGlobal = listaBuscador.get(position);
        holder.producto.setText(existenciasGlobal.getEgProducto());
        holder.envase.setText(existenciasGlobal.getEgEnvase());
        holder.cantidad.setText(String.valueOf(existenciasGlobal.getEgCantidad()));
        holder.tienda.setText(existenciasGlobal.getEgTienda());
    }

    @Override
    public int getItemCount() {
        return listaBuscador.size();
    }
}
