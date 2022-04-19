package com.nervion.sionmovil.Movimientos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class PorSolicitud_Adapter extends RecyclerView.Adapter<PorSolicitud_Adapter.ViewHolder> {

    protected List<PorSolicitud_Constructor> listaMovimientos;
    private final Context contexto;

    private PorSolicitud_Adapter.OnItemLongClickListener monItemLongClickListener;

    public PorSolicitud_Adapter(Context contexto, List<PorSolicitud_Constructor> listaMovimientos){
        this.listaMovimientos = listaMovimientos;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView producto, envase, cantidad, lote;

        public ViewHolder(View itemView) {
            super(itemView);
            producto = itemView.findViewById(R.id.msClave);
            envase = itemView.findViewById(R.id.msEnvase);
            cantidad = itemView.findViewById(R.id.msCantidad);
            lote = itemView.findViewById(R.id.msLote);
        }
    }

    @Override
    public PorSolicitud_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.por_solicitud_adapter, null);
        return new PorSolicitud_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PorSolicitud_Adapter.ViewHolder holder, int position) {
        PorSolicitud_Constructor porSolicitud = listaMovimientos.get(position);
        holder.producto.setText(porSolicitud.getMsClave());
        holder.envase.setText(porSolicitud.getMsEnvase());
        holder.cantidad.setText(String.valueOf(porSolicitud.getMsCantidad()));
        holder.lote.setText(String.valueOf(porSolicitud.getMsLote()));

        holder.itemView.setOnLongClickListener(v -> {
            monItemLongClickListener.setOnItemLongClickListener(v, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaMovimientos.size();
    }

    public interface OnItemLongClickListener {
        boolean setOnItemLongClickListener(View view, int position);
    }

    public void OnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.monItemLongClickListener = onItemLongClickListener;
    }
}
