package com.nervion.sionmovil.Calidad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.nervion.sionmovil.Inventarios.InventariosSalidas_Adaptor;
import com.nervion.sionmovil.R;
import java.util.List;

public class CalidadPlanta_Adapter extends RecyclerView.Adapter<CalidadPlanta_Adapter.ViewHolder> {

    protected List<CalidadPlanta_Constructor> listaCalidad;
    private Context contexto;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public CalidadPlanta_Adapter(Context contexto, List<CalidadPlanta_Constructor> listaCalidad){
        this.listaCalidad = listaCalidad;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lote, usuario, materiaPrima, fecha, hora;

        public ViewHolder(View itemView) {
            super(itemView);
            lote = itemView.findViewById(R.id.cpLote);
            materiaPrima = itemView.findViewById(R.id.cpMateriaPrima);
            fecha = itemView.findViewById(R.id.cpFecha);
            usuario = itemView.findViewById(R.id.cpUsuario);
            hora = itemView.findViewById(R.id.cpHora);
        }
    }

    @Override
    public CalidadPlanta_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.calidad_planta_adapter, null);
        return new CalidadPlanta_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CalidadPlanta_Adapter.ViewHolder holder, int position) {
        CalidadPlanta_Constructor calidad = listaCalidad.get(position);

        holder.lote.setText(String.valueOf(calidad.getCpLote()));
        holder.usuario.setText(calidad.getCpUsuario());
        if (calidad.getCpObservacion().equals("En proceso")){
            holder.materiaPrima.setText(calidad.getCpCantidad()+" "+calidad.getCpUnidad()+" "+calidad.getCpMP()+" En proceso");
        }else{ holder.materiaPrima.setText(calidad.getCpCantidad()+" "+calidad.getCpUnidad()+" "+calidad.getCpMP()); }
        holder.fecha.setText(calidad.getCpFecha());
        holder.hora.setText(calidad.getCpHora());

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.setOnItemClickListener(v, position));

        holder.itemView.setOnLongClickListener(v -> {
            mOnItemLongClickListener.setOnItemLongClickListener(v, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaCalidad.size();
    }

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemLongClickListener {
        boolean setOnItemLongClickListener(View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }
}
