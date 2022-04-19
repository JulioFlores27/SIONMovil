package com.nervion.sionmovil.Movimientos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class MovimientosPorFecha_Adapter extends RecyclerView.Adapter<MovimientosPorFecha_Adapter.ViewHolder> {

    protected List<MovimientosPorFecha_Constructor> listaMovimientos;
    private final Context contexto;

    public MovimientosPorFecha_Adapter(Context contexto, List<MovimientosPorFecha_Constructor> listaMovimientos){
        this.listaMovimientos = listaMovimientos;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView producto, envase, cantidad, lote;

        public ViewHolder(View itemView) {
            super(itemView);
            producto = itemView.findViewById(R.id.mfProducto);
            envase = itemView.findViewById(R.id.mfEnvase);
            cantidad = itemView.findViewById(R.id.mfCantidad);
            lote = itemView.findViewById(R.id.mfLote);
        }
    }

    @Override
    public MovimientosPorFecha_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.movimientos_fecha_adapter, null);
        return new MovimientosPorFecha_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovimientosPorFecha_Adapter.ViewHolder holder, int position) {
        MovimientosPorFecha_Constructor movimientoFecha = listaMovimientos.get(position);
        holder.producto.setText(movimientoFecha.getMfClave());
        holder.envase.setText(movimientoFecha.getMfEnvase());
        holder.cantidad.setText(String.valueOf(movimientoFecha.getMfCantidad()));
        holder.lote.setText(String.valueOf(movimientoFecha.getMfLote()));
    }

    @Override
    public int getItemCount() {
        return listaMovimientos.size();
    }
}
