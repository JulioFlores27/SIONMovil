package com.nervion.sionmovil.Penalizacion;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.nervion.sionmovil.Inventarios.InventariosSalidas_Adaptor;
import com.nervion.sionmovil.Inventarios.InventariosSalidas_Constructor;
import com.nervion.sionmovil.R;

import java.util.List;

public class PenalizacionAlmacen_Adaptor extends RecyclerView.Adapter<PenalizacionAlmacen_Adaptor.ViewHolder> {

    private LayoutInflater inflador;
    protected List<PenalizacionAlmacen_Constructor> listaPenalizaciones;
    private Context contexto;

    private PenalizacionAlmacen_Adaptor.OnItemClickListener mOnItemClickListener;

    public PenalizacionAlmacen_Adaptor(Context contexto, List<PenalizacionAlmacen_Constructor> listaPenalizaciones){
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listaPenalizaciones = listaPenalizaciones;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;
        public TextView fecha, hora, cantidad, correoPenalizado, cantidadEscaneada, observaciones, restantes;
        public int id;
        private AdapterView.OnItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.paFecha);
            hora = itemView.findViewById(R.id.paHora);
            cantidad = itemView.findViewById(R.id.paCantidad);
            correoPenalizado = itemView.findViewById(R.id.paCorreoPenalizado);
            cantidadEscaneada = itemView.findViewById(R.id.paCantidadEscaneada);
            observaciones = itemView.findViewById(R.id.paObservaciones);
            restantes = itemView.findViewById(R.id.paRestantes);

            linearLayout = itemView.findViewById(R.id.paArea);
        }
    }

    @Override
    public PenalizacionAlmacen_Adaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.penalizacionalmacen_adapter, null);
        return new PenalizacionAlmacen_Adaptor.ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(PenalizacionAlmacen_Adaptor.ViewHolder holder, int position) {
        PenalizacionAlmacen_Constructor penalizacionesAlmacen = listaPenalizaciones.get(position);
        holder.fecha.setText(penalizacionesAlmacen.getPaFecha());
        holder.hora.setText(penalizacionesAlmacen.getPaHora());
        holder.cantidad.setText(String.valueOf(penalizacionesAlmacen.getPaCantidad()));
        holder.correoPenalizado.setText(penalizacionesAlmacen.getPaUsuarioPenalizado());
        holder.cantidadEscaneada.setText(String.valueOf(penalizacionesAlmacen.getPaCantidadEscaneada()));
        holder.observaciones.setText(penalizacionesAlmacen.getPaObservaciones());
        holder.restantes.setText(String.valueOf(penalizacionesAlmacen.getPaRestantes()));

        int resultado = penalizacionesAlmacen.getPaRestantes();

        if (resultado == 0){ holder.linearLayout.setBackgroundColor(Color.argb(64,127,255,0)); }

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.setOnItemClickListener(v, position));
    }

    @Override
    public int getItemCount() {
        return listaPenalizaciones.size();
    }

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(PenalizacionAlmacen_Adaptor.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
