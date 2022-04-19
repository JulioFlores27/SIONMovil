package com.nervion.sionmovil.Entinte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class EntinteHistorial_Adapter extends RecyclerView.Adapter<EntinteHistorial_Adapter.ViewHolder> {

    protected List<EntinteHistorial_Constructor> listaEntinte;
    private final Context contexto;

    public EntinteHistorial_Adapter(Context contexto, List<EntinteHistorial_Constructor> listaEntinte){
        this.listaEntinte = listaEntinte;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pedido, usuario, fecha, tonos, datetime, hora, producto, envase, lote, fechaSalida, diferencia, horaSalida;

        public ViewHolder(View itemView) {
            super(itemView);
            pedido = itemView.findViewById(R.id.ehPedido);
            usuario = itemView.findViewById(R.id.ehUsuario);
            fecha = itemView.findViewById(R.id.ehFecha);
            tonos = itemView.findViewById(R.id.ehTonos);
            datetime = itemView.findViewById(R.id.ehComentarioEninte);
            hora = itemView.findViewById(R.id.ehHora);
            producto = itemView.findViewById(R.id.ehProducto);
            envase = itemView.findViewById(R.id.ehEnvase);
            lote = itemView.findViewById(R.id.ehLote);
            fechaSalida = itemView.findViewById(R.id.ehFechaSalida);
            diferencia = itemView.findViewById(R.id.ehDiferencia);
            horaSalida = itemView.findViewById(R.id.ehHoraSalida);
        }
    }

    @Override
    public EntinteHistorial_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.entinte_historial_adapter, null);
        return new EntinteHistorial_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EntinteHistorial_Adapter.ViewHolder holder, int position) {
        EntinteHistorial_Constructor entinteHistorial = listaEntinte.get(position);
        holder.pedido.setText(String.valueOf(entinteHistorial.getEhPedido()));
        holder.usuario.setText(entinteHistorial.getEhUsuario());
        holder.fecha.setText(entinteHistorial.getEhFecha());
        holder.tonos.setText("Tonos: "+ entinteHistorial.getEhTono());
        holder.datetime.setText("Individual: "+entinteHistorial.getEhDateTime());
        holder.hora.setText(entinteHistorial.getEhHora());
        holder.producto.setText(entinteHistorial.getEhClave());
        holder.envase.setText(entinteHistorial.getEhEnvase());
        holder.lote.setText(String.valueOf(entinteHistorial.getEhLote()));
        holder.fechaSalida.setText(entinteHistorial.getEhFechaSalida());
        holder.diferencia.setText("Horas: "+entinteHistorial.getEhDiferencia());
        holder.horaSalida.setText(entinteHistorial.getEhHoraSalida());
    }

    @Override
    public int getItemCount() {
        return listaEntinte.size();
    }
}
