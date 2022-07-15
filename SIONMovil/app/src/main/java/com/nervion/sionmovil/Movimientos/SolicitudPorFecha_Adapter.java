package com.nervion.sionmovil.Movimientos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class SolicitudPorFecha_Adapter extends RecyclerView.Adapter<SolicitudPorFecha_Adapter.ViewHolder> {

    protected List<SolicitudPorFecha_Constructor> listaMovimientos;
    private final Context contexto;

    public SolicitudPorFecha_Adapter(Context contexto, List<SolicitudPorFecha_Constructor> listaMovimientos){
        this.listaMovimientos = listaMovimientos;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView solicitud, usuario, fecha;

        public ViewHolder(View itemView) {
            super(itemView);
            solicitud = itemView.findViewById(R.id.mfSolicitud);
            usuario = itemView.findViewById(R.id.mfUsuario);
            fecha = itemView.findViewById(R.id.mfFecha);
        }
    }

    @Override
    public SolicitudPorFecha_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.solicitud_por_fecha_adapter, null);
        return new SolicitudPorFecha_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SolicitudPorFecha_Adapter.ViewHolder holder, int position) {
        SolicitudPorFecha_Constructor porFecha = listaMovimientos.get(position);
        holder.solicitud.setText(porFecha.getMfObservaciones());
        holder.usuario.setText(porFecha.getMfUsuario());
        holder.fecha.setText(String.valueOf(porFecha.getMfFecha()));
    }

    @Override
    public int getItemCount() {
        return listaMovimientos.size();
    }
}
