package com.nervion.sionmovil.Surtir;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.nervion.sionmovil.R;
import java.util.List;

public class SurtirLocal_Adapter extends RecyclerView.Adapter<SurtirLocal_Adapter.ViewHolder> {

    private LayoutInflater inflador;
    protected List<SurtirLocal_Constructor> listaSurtir;
    private Context contexto;
    private SurtirLocal_Adapter.OnItemClickListener mOnItemClickListener;

    public SurtirLocal_Adapter(Context contexto, List<SurtirLocal_Constructor> listaSurtir){
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listaSurtir = listaSurtir;
        this.contexto = contexto;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;
        public TextView partida, rfc, producto, envase, cantidadSolicitada, cantidadPendiente, lote, cantidad;
        private AdapterView.OnItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            partida = itemView.findViewById(R.id.slPartida);
            rfc = itemView.findViewById(R.id.slRFC);
            producto = itemView.findViewById(R.id.slClave);
            envase = itemView.findViewById(R.id.slEnvase);
            cantidadSolicitada = itemView.findViewById(R.id.slCantidadSolicitada);
            cantidadPendiente = itemView.findViewById(R.id.slCantidadPendiente);
            lote = itemView.findViewById(R.id.slLote);
            cantidad = itemView.findViewById(R.id.slCantidad);

            linearLayout = itemView.findViewById(R.id.slArea);
        }
    }

    @Override
    public SurtirLocal_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.surtir_local_adapter, null);
        return new SurtirLocal_Adapter.ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(SurtirLocal_Adapter.ViewHolder holder, int position) {
        SurtirLocal_Constructor surtirLocal = listaSurtir.get(position);
        holder.partida.setText(String.valueOf(surtirLocal.getSlPartida()));
        holder.rfc.setText(surtirLocal.getSlRack()+"-"+surtirLocal.getSlFila()+"-"+surtirLocal.getSlColumna());
        holder.producto.setText(surtirLocal.getSlClave());
        holder.envase.setText(surtirLocal.getSlEnvase());
        holder.cantidadSolicitada.setText(String.valueOf(surtirLocal.getSlCantidadSolicitada()));

        int resultado = surtirLocal.getSlCantidadSolicitada()+surtirLocal.getSlCantidadPendiente();
        holder.cantidadPendiente.setText(String.valueOf(resultado));
        holder.lote.setText(String.valueOf(surtirLocal.getSlLote()));
        holder.cantidad.setText(String.valueOf(surtirLocal.getSlCantidad()));


        if (surtirLocal.getSlCantidad() == 0 && surtirLocal.getSlCantidadSolicitada() > resultado && resultado != 0){
            holder.linearLayout.setBackgroundColor(Color.argb(100,191,245,220));
        }else if (resultado == 0){
            holder.linearLayout.setBackgroundColor(Color.argb(100,127,255,0));
        }else { holder.linearLayout.setBackgroundColor(Color.argb(0,0,0,0)); }

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.setOnItemClickListener(v, position));
    }

    @Override
    public int getItemCount() {
        return listaSurtir.size();
    }

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(SurtirLocal_Adapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
