package com.nervion.sionmovil.Vigilancia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class SalidasVigilancia_Adapter extends RecyclerView.Adapter<SalidasVigilancia_Adapter.ViewHolder> {

    protected List<SalidasVigilancia_Constructor> listaVigilancia;
    private final Context contexto;

    public SalidasVigilancia_Adapter(Context contexto, List<SalidasVigilancia_Constructor> listaVigilancia){
        this.listaVigilancia = listaVigilancia;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView folio, producto, fecha, pedido, envase, cantidad, lote, hora;

        public ViewHolder(View itemView) {
            super(itemView);
            folio = itemView.findViewById(R.id.svFolio);
            producto = itemView.findViewById(R.id.svProducto);
            fecha = itemView.findViewById(R.id.svFecha);
            pedido = itemView.findViewById(R.id.svPedido);
            envase = itemView.findViewById(R.id.svEnvase);
            cantidad = itemView.findViewById(R.id.svCantidad);
            lote = itemView.findViewById(R.id.svLote);
            hora = itemView.findViewById(R.id.svHora);
        }
    }

    @Override
    public SalidasVigilancia_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.salidas_vigilancia_adapter, null);
        return new SalidasVigilancia_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SalidasVigilancia_Adapter.ViewHolder holder, int position) {
        SalidasVigilancia_Constructor salidaVigilancia = listaVigilancia.get(position);
        holder.folio.setText(String.valueOf(salidaVigilancia.getSvFolio()));
        holder.producto.setText(salidaVigilancia.getSvProducto());
        holder.fecha.setText(salidaVigilancia.getSvFecha());
        holder.pedido.setText(String.valueOf(salidaVigilancia.getSvPedido()));
        holder.envase.setText(salidaVigilancia.getSvEnvase());
        holder.cantidad.setText(String.valueOf(salidaVigilancia.getSvCantidad()));
        holder.lote.setText(String.valueOf(salidaVigilancia.getSvLote()));
        holder.hora.setText(salidaVigilancia.getSvHora());
    }

    @Override
    public int getItemCount() {
        return listaVigilancia.size();
    }
}
