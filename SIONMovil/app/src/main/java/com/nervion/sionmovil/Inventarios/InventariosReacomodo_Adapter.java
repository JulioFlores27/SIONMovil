package com.nervion.sionmovil.Inventarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nervion.sionmovil.R;

import java.util.List;

public class InventariosReacomodo_Adapter extends RecyclerView.Adapter<InventariosReacomodo_Adapter.ViewHolder> {

    protected List<InventariosReacomodo_Constructor> listaInventarios;
    private final Context contexto;

    private OnItemClickListener mOnItemClickListener;

    public InventariosReacomodo_Adapter(Context contexto, List<InventariosReacomodo_Constructor> listaInventarios){
        this.listaInventarios = listaInventarios;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView producto, envase, cantidad, lote, rfc;
        public int id;

        public ViewHolder(View itemView) {
            super(itemView);
            producto = itemView.findViewById(R.id.irClave);
            envase = itemView.findViewById(R.id.irEnvase);
            cantidad = itemView.findViewById(R.id.irCantidad);
            lote = itemView.findViewById(R.id.irLote);
            rfc = itemView.findViewById(R.id.irRFC);
        }
    }

    @Override
    public InventariosReacomodo_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.inventarios_reacomodo_adapter, null);
        return new InventariosReacomodo_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InventariosReacomodo_Adapter.ViewHolder holder, int position) {
        InventariosReacomodo_Constructor inventarioReacomodo = listaInventarios.get(position);
        holder.producto.setText(inventarioReacomodo.getIrProducto());
        holder.envase.setText(inventarioReacomodo.getIrEnvase());
        holder.cantidad.setText(String.valueOf(inventarioReacomodo.getIrCantidad()));
        holder.lote.setText(String.valueOf(inventarioReacomodo.getIrLote()));
        holder.rfc.setText(inventarioReacomodo.getIrRack() + "-" + inventarioReacomodo.getIrFila() + "-" + inventarioReacomodo.getIrColumna());

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.setOnItemClickListener(v, position));
    }

    @Override
    public int getItemCount() {
        return listaInventarios.size();
    }

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
