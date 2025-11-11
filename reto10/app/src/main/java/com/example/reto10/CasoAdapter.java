package com.example.reto10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CasoAdapter extends RecyclerView.Adapter<CasoAdapter.ViewHolder> {

    private List<CasoCovid> lista;

    public CasoAdapter(List<CasoCovid> lista) {
        this.lista = lista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_punto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CasoCovid c = lista.get(position);
        holder.tvNombreTipo.setText(c.getCiudadMunicipioNom() + " - " + c.getDepartamentoNom());
        holder.tvUbicacion.setText("Sexo: " + c.getSexo() + " | Edad: " + c.getEdad());
        holder.tvDireccion.setText("Estado: " + c.getEstado() + " | Contagio: " + c.getFuenteContagio());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreTipo, tvUbicacion, tvDireccion;
        public ViewHolder(View itemView) {
            super(itemView);
            tvNombreTipo = itemView.findViewById(R.id.tvNombreTipo);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
        }
    }
}
