package com.nervion.sionmovil.Buscador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class StatusPedido_Adapter extends RecyclerView.Adapter<StatusPedido_Adapter.ViewHolder> {

    protected List<StatusPedido_Constructor> listaBuscador;
    private final Context contexto;

    public StatusPedido_Adapter(Context contexto, List<StatusPedido_Constructor> listaBuscador){
        this.listaBuscador = listaBuscador;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView producto, envase, cantidad, observaciones2, fechaYhora;

        public ViewHolder(View itemView) {
            super(itemView);
            producto = itemView.findViewById(R.id.spProducto);
            envase = itemView.findViewById(R.id.spEnvase);
            cantidad = itemView.findViewById(R.id.spCantidad);
            observaciones2 = itemView.findViewById(R.id.spObservaciones2);
            fechaYhora = itemView.findViewById(R.id.spFechaHora);
        }
    }

    @Override
    public StatusPedido_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.buscador_status_pedido_adapter, null);
        return new StatusPedido_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StatusPedido_Adapter.ViewHolder holder, int position) {
        StatusPedido_Constructor existenciasGlobal = listaBuscador.get(position);
        holder.producto.setText(existenciasGlobal.getSpProducto());
        holder.envase.setText(existenciasGlobal.getSpEnvase());
        holder.cantidad.setText(String.valueOf(existenciasGlobal.getSpCantidad()));
        holder.observaciones2.setText(existenciasGlobal.getSpObservaciones2());
        holder.fechaYhora.setText(existenciasGlobal.getSpFecha()+" "+existenciasGlobal.getSpHora());
    }

    @Override
    public int getItemCount() {
        return listaBuscador.size();
    }
}
