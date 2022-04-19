package com.nervion.sionmovil.Inventarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class InventariosSalidas_Adaptor extends RecyclerView.Adapter<InventariosSalidas_Adaptor.ViewHolder> {

    protected List<InventariosSalidas_Constructor> listaInventarios;
    private final Context contexto;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener monItemLongClickListener;

    public InventariosSalidas_Adaptor(Context contexto, List<InventariosSalidas_Constructor> listaInventarios){
        this.listaInventarios = listaInventarios;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView producto, envase, cantidad, lote, rfc;
        public int id;

        public ViewHolder(View itemView) {
            super(itemView);
            producto = itemView.findViewById(R.id.isClave);
            envase = itemView.findViewById(R.id.isEnvase);
            cantidad = itemView.findViewById(R.id.isCantidad);
            lote = itemView.findViewById(R.id.isLote);
            rfc = itemView.findViewById(R.id.isRFC);
        }
    }

    @Override
    public InventariosSalidas_Adaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.inventarios_salidas_adaptor, null);
        return new InventariosSalidas_Adaptor.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InventariosSalidas_Adaptor.ViewHolder holder, int position) {
        InventariosSalidas_Constructor inventarioSalida = listaInventarios.get(position);
        holder.producto.setText(inventarioSalida.getIsProducto());
        holder.envase.setText(inventarioSalida.getIsEnvase());
        holder.cantidad.setText(String.valueOf(inventarioSalida.getIsCantidad()));
        holder.lote.setText(String.valueOf(inventarioSalida.getIsLote()));
        holder.rfc.setText(inventarioSalida.getIsRack() + "-" + inventarioSalida.getIsFila() + "-" + inventarioSalida.getIsColumna());

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.setOnItemClickListener(v, position));

        holder.itemView.setOnLongClickListener(v -> {
            monItemLongClickListener.setOnItemLongClickListener(v, position);
            return true;
        });
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

    public interface OnItemLongClickListener {
        boolean setOnItemLongClickListener(View view, int position);
    }

    public void OnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.monItemLongClickListener = onItemLongClickListener;
    }
}