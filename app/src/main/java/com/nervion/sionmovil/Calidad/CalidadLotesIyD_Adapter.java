package com.nervion.sionmovil.Calidad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class CalidadLotesIyD_Adapter extends RecyclerView.Adapter<CalidadLotesIyD_Adapter.ViewHolder> {

    protected List<CalidadLotesIyD_Constructor> listaCalidad;
    private Context contexto;

    public CalidadLotesIyD_Adapter(Context contexto, List<CalidadLotesIyD_Constructor> listaCalidad){
        this.listaCalidad = listaCalidad;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lote, producto, fecha;

        public ViewHolder(View itemView) {
            super(itemView);
            lote = itemView.findViewById(R.id.cidLote);
            producto = itemView.findViewById(R.id.cidProducto);
            fecha = itemView.findViewById(R.id.cidFecha);
        }
    }

    @Override
    public CalidadLotesIyD_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.calidad_lotesid_adapter, null);
        return new CalidadLotesIyD_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CalidadLotesIyD_Adapter.ViewHolder holder, int position) {
        CalidadLotesIyD_Constructor calidad = listaCalidad.get(position);

        holder.lote.setText(String.valueOf(calidad.getCidInvLote()));
        holder.producto.setText(calidad.getCidProducto());
        holder.fecha.setText(calidad.getCidFecha());
    }

    @Override
    public int getItemCount() {
        return listaCalidad.size();
    }
}
