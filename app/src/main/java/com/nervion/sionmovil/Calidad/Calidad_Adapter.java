package com.nervion.sionmovil.Calidad;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class Calidad_Adapter extends RecyclerView.Adapter<Calidad_Adapter.ViewHolder> {

    protected List<Calidad_Constructor> listaCalidad;
    private Context contexto;

    private Calidad_Adapter.OnItemClickListener mOnItemClickListener;

    public Calidad_Adapter(Context contexto, List<Calidad_Constructor> listaCalidad){
        this.listaCalidad = listaCalidad;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lote, usuario, comentarioCalidad, fecha, hora;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            lote = itemView.findViewById(R.id.ccLote);
            usuario = itemView.findViewById(R.id.ccUsuario);
            comentarioCalidad = itemView.findViewById(R.id.ccComentarioCalidad);
            fecha = itemView.findViewById(R.id.ccFecha);
            hora = itemView.findViewById(R.id.ccHora);

            linearLayout = itemView.findViewById(R.id.ccArea);
        }
    }

    @Override
    public Calidad_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View v = inflater.inflate(R.layout.calidad_adapter, null);
        return new Calidad_Adapter.ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(Calidad_Adapter.ViewHolder holder, int position) {
        Calidad_Constructor calidad = listaCalidad.get(position);
        String cantidadValor="";

        holder.lote.setText(String.valueOf(calidad.getCcLote()));
        holder.usuario.setText(calidad.getCcUsuario());
        if (calidad.getCcCantidad() > 0.0) { cantidadValor = String.valueOf(calidad.getCcCantidad()); }
        holder.comentarioCalidad.setText(cantidadValor+" "+calidad.getCcUnidad()+" "+calidad.getCcMP()+" "+calidad.getCcObservacion());
        holder.fecha.setText(calidad.getCcFecha());
        holder.hora.setText(calidad.getCcHora());

        String resultado = calidad.getCcObservacion();

        if (resultado.equals("PNC")){
            holder.linearLayout.setBackgroundColor(Color.argb(75,255,32,32));
        }else if (resultado.equals("Preaprobado")){
            holder.linearLayout.setBackgroundColor(Color.argb(75,151,179,162));
        }else {  holder.linearLayout.setBackgroundColor(Color.TRANSPARENT); }

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.setOnItemClickListener(v, position));
    }

    @Override
    public int getItemCount() {
        return listaCalidad.size();
    }

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(Calidad_Adapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
