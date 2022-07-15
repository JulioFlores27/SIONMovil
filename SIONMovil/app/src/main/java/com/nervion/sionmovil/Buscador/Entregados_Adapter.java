package com.nervion.sionmovil.Buscador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class Entregados_Adapter extends RecyclerView.Adapter<Entregados_Adapter.ViewHolder> {

    protected List<Entregados_Constructor> listaBuscador;
    private final Context contexto;

    public Entregados_Adapter(Context contexto, List<Entregados_Constructor> listaBuscador){
        this.listaBuscador = listaBuscador;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pedido, usuario, fecha, ubicacion, hora;

        public ViewHolder(View itemView) {
            super(itemView);
            pedido = itemView.findViewById(R.id.beObservaciones2);
            usuario = itemView.findViewById(R.id.beUsuario);
            fecha = itemView.findViewById(R.id.beFecha);
            ubicacion = itemView.findViewById(R.id.beUbicacion);
            hora = itemView.findViewById(R.id.beHora);
        }
    }

    @Override
    public Entregados_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.buscador_entregados_adapter, null);
        return new Entregados_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Entregados_Adapter.ViewHolder holder, int position) {
        Entregados_Constructor entregados = listaBuscador.get(position);
        holder.pedido.setText(entregados.getBePedido());
        holder.usuario.setText(entregados.getBeUsuario());
        holder.fecha.setText(String.valueOf(entregados.getBeFecha()));
        holder.ubicacion.setText(entregados.getBeLatitud()+", "+entregados.getBeLongitud());
        holder.hora.setText(entregados.getBeHora());
    }

    @Override
    public int getItemCount() {
        return listaBuscador.size();
    }
}
