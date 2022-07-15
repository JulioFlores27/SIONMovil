package com.nervion.sionmovil.Calidad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class CalidadHistorial_Adapter extends RecyclerView.Adapter<CalidadHistorial_Adapter.ViewHolder> {

    private LayoutInflater inflador;
    protected List<CalidadHistorial_Constructor> listaCalidad;
    private Context contexto;

    public CalidadHistorial_Adapter(Context contexto, List<CalidadHistorial_Constructor> listaCalidad){
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listaCalidad = listaCalidad;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lote, usuario, comentarioCalidad, fecha, hora, viscosidad, densidad, solidos, brillo, observaciones2;

        public ViewHolder(View itemView) {
            super(itemView);
            lote = itemView.findViewById(R.id.chLote);
            comentarioCalidad = itemView.findViewById(R.id.chComentarioCalidad);
            fecha = itemView.findViewById(R.id.chFecha);
            usuario = itemView.findViewById(R.id.chUsuario);
            hora = itemView.findViewById(R.id.chHora);
            viscosidad = itemView.findViewById(R.id.chViscosidad);
            densidad = itemView.findViewById(R.id.chDensidad);
            solidos = itemView.findViewById(R.id.chSolidos);
            brillo = itemView.findViewById(R.id.chBrillo);
            observaciones2 = itemView.findViewById(R.id.chObservaciones2);
        }
    }

    @Override
    public CalidadHistorial_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.calidad_historial_adapter, null);
        return new CalidadHistorial_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CalidadHistorial_Adapter.ViewHolder holder, int position) {
        CalidadHistorial_Constructor calidad = listaCalidad.get(position);

        holder.lote.setText(String.valueOf(calidad.getChLote()));
        String comentarioCalidad = calidad.chObservacion+" "+calidad.getChCantidad()+" "+calidad.getChUnidad()+" "+calidad.getChMP();
        holder.comentarioCalidad.setText(comentarioCalidad.trim());
        holder.fecha.setText(calidad.getChFecha());

        holder.usuario.setText(calidad.getChUsuario());
        holder.hora.setText(calidad.getChHora());

        String viscosidad = "V: "+calidad.getChViscosidad()+" "+calidad.getChViscUnidad();
        holder.viscosidad.setText(viscosidad.trim());
        holder.densidad.setText("D: "+calidad.getChDensidad());
        holder.solidos.setText("S: "+calidad.getChSolidos());
        holder.brillo.setText("B: "+calidad.getChBrillo());

        holder.observaciones2.setText(calidad.getChObservaciones2());

        if ( holder.observaciones2.getText().equals("")) {
            holder.observaciones2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaCalidad.size();
    }
}
