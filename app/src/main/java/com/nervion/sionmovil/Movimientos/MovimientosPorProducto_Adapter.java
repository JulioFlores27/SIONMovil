package com.nervion.sionmovil.Movimientos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class MovimientosPorProducto_Adapter extends RecyclerView.Adapter<MovimientosPorProducto_Adapter.ViewHolder> {

    protected List<MovimientosPorProducto_Constructor> listaMovimientos;
    private final Context contexto;

    public MovimientosPorProducto_Adapter(Context contexto, List<MovimientosPorProducto_Constructor> listaMovimientos){
        this.listaMovimientos = listaMovimientos;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView producto, envase, cantidad, lote, solicitud, fecha, hora, usuario;

        public ViewHolder(View itemView) {
            super(itemView);
            producto = itemView.findViewById(R.id.mpProducto);
            envase = itemView.findViewById(R.id.mpEnvase);
            cantidad = itemView.findViewById(R.id.mpCantidad);
            lote = itemView.findViewById(R.id.mpLote);
            solicitud = itemView.findViewById(R.id.mpObservaciones);
            fecha = itemView.findViewById(R.id.mpFecha);
            hora = itemView.findViewById(R.id.mpHora);
            usuario = itemView.findViewById(R.id.mpUsuario);
        }
    }

    @Override
    public MovimientosPorProducto_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.movimientos_producto_adapter, null);
        return new MovimientosPorProducto_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovimientosPorProducto_Adapter.ViewHolder holder, int position) {
        MovimientosPorProducto_Constructor movimientoFecha = listaMovimientos.get(position);
        holder.producto.setText(movimientoFecha.getMpClave());
        holder.envase.setText(movimientoFecha.getMpEnvase());
        holder.cantidad.setText(String.valueOf(movimientoFecha.getMpCantidad()));
        holder.lote.setText(String.valueOf(movimientoFecha.getMpLote()));
        holder.solicitud.setText(movimientoFecha.getMpObservaciones());
        holder.fecha.setText(movimientoFecha.getMpFecha());
        holder.hora.setText(movimientoFecha.getMpHora());
        holder.usuario.setText(movimientoFecha.getMpUsuario());
    }

    @Override
    public int getItemCount() {
        return listaMovimientos.size();
    }
}
