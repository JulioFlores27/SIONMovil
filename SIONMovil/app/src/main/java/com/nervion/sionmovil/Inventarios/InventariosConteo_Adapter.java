package com.nervion.sionmovil.Inventarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class InventariosConteo_Adapter extends RecyclerView.Adapter<InventariosConteo_Adapter.ViewHolder> {

    protected List<InventariosConteo_Constructor> listaInventarios;
    private Context contexto;

    private OnItemLongClickListener monItemLongClickListener;

    public InventariosConteo_Adapter(Context contexto, List<InventariosConteo_Constructor> listaInventarios){
        this.listaInventarios = listaInventarios;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView producto, envase, cantidad, lote, rfc;

        public ViewHolder(View itemView) {
            super(itemView);
            producto = itemView.findViewById(R.id.icClave);
            envase = itemView.findViewById(R.id.icEnvase);
            cantidad = itemView.findViewById(R.id.icCantidad);
            lote = itemView.findViewById(R.id.icLote);
            rfc = itemView.findViewById(R.id.icRFC);
        }
    }

    @Override
    public InventariosConteo_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.inventarios_conteo_adaptor, null);
        return new InventariosConteo_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InventariosConteo_Adapter.ViewHolder holder, int position) {
        InventariosConteo_Constructor inventarioConteo = listaInventarios.get(position);
        holder.producto.setText(inventarioConteo.getIcProducto());
        holder.envase.setText(inventarioConteo.getIcEnvase());
        holder.cantidad.setText(String.valueOf(inventarioConteo.getIcCantidad()));
        holder.lote.setText(String.valueOf(inventarioConteo.getIcLote()));
        holder.rfc.setText(inventarioConteo.getIcRack() + "-" + inventarioConteo.getIcFila() + "-" + inventarioConteo.getIcColumna());

        holder.itemView.setOnLongClickListener(v -> {
            monItemLongClickListener.setOnItemLongClickListener(v, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaInventarios.size();
    }

    public interface OnItemLongClickListener {
        boolean setOnItemLongClickListener(View view, int position);
    }

    public void OnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.monItemLongClickListener = onItemLongClickListener;
    }
}
