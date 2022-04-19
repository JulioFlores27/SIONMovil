
package com.nervion.sionmovil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity_Adaptor extends RecyclerView.Adapter<MainActivity_Adaptor.ViewHolder> {
 protected List<MainActivity_Constructor> listaModulos;
 private final Context contexto;

 private OnItemClickListener onClickListener;

 public MainActivity_Adaptor(Context contexto, List<MainActivity_Constructor> listaModulos){
  this.listaModulos = listaModulos;
  this.contexto = contexto;
 }

 public static class ViewHolder extends RecyclerView.ViewHolder{
  public ImageView portada;
  public TextView titulo;

  public ViewHolder(View itemView) {
   super(itemView);
   portada = itemView.findViewById(R.id.btnModulos);
   titulo = itemView.findViewById(R.id.tvModulos);
  }
 }

 @Override
 public MainActivity_Adaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
  LayoutInflater inflater = LayoutInflater.from(contexto);
  View v = inflater.inflate(R.layout.activity_main_adaptor, null);
  return new MainActivity_Adaptor.ViewHolder(v);
 }

 @Override
 public void onBindViewHolder(MainActivity_Adaptor.ViewHolder holder, int position) {
  MainActivity_Constructor modulos = listaModulos.get(position);
  holder.portada.setImageResource(modulos.getImagenModulo());
  holder.titulo.setText(modulos.getTituloModulo());

  holder.itemView.setOnClickListener(v -> onClickListener.setOnItemClickListener(v, position));
 }

 @Override
 public int getItemCount() {
  return listaModulos.size();
 }

 public interface OnItemClickListener {
  void setOnItemClickListener(View view, int position);
 }

 public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
  this.onClickListener = onItemClickListener;
 }
}