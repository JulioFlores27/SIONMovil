package com.nervion.sionmovil.Inventarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nervion.sionmovil.R;

import java.util.List;

public class InventariosExistencias_Adapter extends RecyclerView.Adapter<InventariosExistencias_Adapter.ViewHolder> {

    protected List<InventariosExistencias_Constructor> listaInventarios;
    private Context contexto;

    public InventariosExistencias_Adapter(Context contexto, List<InventariosExistencias_Constructor> listaInventarios){

        this.listaInventarios = listaInventarios;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView producto, envase, cantidad;

        public ViewHolder(View itemView) {
            super(itemView);
            producto = itemView.findViewById(R.id.ieClave);
            envase = itemView.findViewById(R.id.ieEnvase);
            cantidad = itemView.findViewById(R.id.ieCantidad);
        }
    }

    @Override
    public InventariosExistencias_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.inventarios_existencias_adapter, null);
        return new InventariosExistencias_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InventariosExistencias_Adapter.ViewHolder holder, int position) {
        InventariosExistencias_Constructor inventarioExistencia = listaInventarios.get(position);
        holder.producto.setText(inventarioExistencia.getIeProducto());
        holder.envase.setText(inventarioExistencia.getIeEnvase());
        holder.cantidad.setText(String.valueOf(inventarioExistencia.getIeCantidad()));
    }

    @Override
    public int getItemCount() {
        return listaInventarios.size();
    }
}
