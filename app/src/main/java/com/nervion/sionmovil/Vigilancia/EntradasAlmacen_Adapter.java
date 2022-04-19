package com.nervion.sionmovil.Vigilancia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class EntradasAlmacen_Adapter extends RecyclerView.Adapter<EntradasAlmacen_Adapter.ViewHolder> {

    protected List<EntradasAlmacen_Constructor> listaVigilancia;
    private final Context contexto;
    private OnItemClickListener mOnItemClickListener;

    public EntradasAlmacen_Adapter(Context contexto, List<EntradasAlmacen_Constructor> listaVigilancia){
        this.listaVigilancia = listaVigilancia;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView producto, envase, cantidad, lote, rfc;

        public ViewHolder(View itemView) {
            super(itemView);
            producto = itemView.findViewById(R.id.eaProducto);
            envase = itemView.findViewById(R.id.eaEnvase);
            cantidad = itemView.findViewById(R.id.eaCantidad);
            lote = itemView.findViewById(R.id.eaLote);
            rfc = itemView.findViewById(R.id.eaRFC);
        }
    }

    @Override
    public EntradasAlmacen_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.vigilancia_entradas_almacen_adapter, null);
        return new EntradasAlmacen_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EntradasAlmacen_Adapter.ViewHolder holder, int position) {
        EntradasAlmacen_Constructor vigilancia = listaVigilancia.get(position);
        holder.producto.setText(vigilancia.getEaProducto());
        holder.envase.setText(vigilancia.getEaEnvase());
        holder.cantidad.setText(String.valueOf(vigilancia.getEaCantidad()));
        holder.lote.setText(String.valueOf(vigilancia.getEaLote()));
        holder.rfc.setText(vigilancia.getEaRack()+"-"+vigilancia.getEaFila()+"-"+vigilancia.getEaColumna());

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.setOnItemClickListener(v, position));
    }

    @Override
    public int getItemCount() {
        return listaVigilancia.size();
    }

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
