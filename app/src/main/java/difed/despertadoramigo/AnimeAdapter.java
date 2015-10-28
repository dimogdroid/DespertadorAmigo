package difed.despertadoramigo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.rey.material.widget.Switch;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import difed.bd.ProveedorBd;
import difed.bd.ProveedorBdImpl;
import difed.entidades.Alarma;
import difed.util.ManagerAlarma;

/**
 * Creado por Hermosa Programación
 */
public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder> {
    private List<Alarma> items;
    static Context context;
    static ProveedorBd proveedor;
    static ManagerAlarma mngAlarma;

    public static class AnimeViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
       /*
        public ImageView imagen;
        public TextView nombre;
        public TextView visitas;*/

        public int id;
        public TextView nombre;
        public TextView hora;
        public Switch swtActiva;
        ImageButton btnDelete;
        ImageButton btnEditar;

        public TextView txtLunes;
        public TextView txtMartes;
        public TextView txtMiercoles;
        public TextView txtJueves;
        public TextView txtViernes;
        public TextView txtSabado;
        public TextView txtDomingo;

        Typeface tf_regular;




        public AnimeViewHolder(View v) {
            super(v);

            this.nombre = (TextView) v.findViewById(R.id.nombre);
            this.hora = (TextView) v.findViewById(R.id.hora);

            tf_regular = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/reloj.ttf");
            hora.setTypeface(tf_regular);

            this.btnDelete = (ImageButton) v.findViewById(R.id.btnBorrar);
            this.btnEditar = (ImageButton) v.findViewById(R.id.btnEditar);
            this.swtActiva = (Switch) v.findViewById(R.id.onoff);



            this.txtLunes = (TextView) v.findViewById(R.id.txtLun);
            this.txtMartes = (TextView) v.findViewById(R.id.txtMar);
            this.txtMiercoles = (TextView) v.findViewById(R.id.txtMie);
            this.txtJueves = (TextView) v.findViewById(R.id.txtJue);
            this.txtViernes = (TextView) v.findViewById(R.id.txtVie);
            this.txtSabado = (TextView) v.findViewById(R.id.txtSab);
            this.txtDomingo = (TextView) v.findViewById(R.id.txtDom);

            proveedor = ProveedorBdImpl.getProveedor(v.getContext());
            mngAlarma = new ManagerAlarma(proveedor, context);

          /*  imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
            visitas = (TextView) v.findViewById(R.id.visitas);*/
        }
    }

    public AnimeAdapter(Context context, List<Alarma> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public AnimeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.alarma, viewGroup, false);
        return new AnimeViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final AnimeViewHolder viewHolder, final int i) {

        String repeticiones = "";
        String[] diasRepetir;
        String hora;
        final int  idAlarma;

        viewHolder.id = items.get(i).getId();
        idAlarma = viewHolder.id;

        viewHolder.nombre.setText(items.get(i).getNombre());

        hora = items.get(i).getHora() + ":" + items.get(i).getMinutos();
        viewHolder.hora.setText(hora);

        viewHolder.swtActiva.setChecked(items.get(i).isActiva());
        if (items.get(i).isActiva()) {
            viewHolder.hora.setTextColor(Color.parseColor("#31d831"));
        }else{
            viewHolder.hora.setTextColor(Color.parseColor("#db3939"));
        }

        repeticiones = items.get(i).getRepetir();

        viewHolder.txtLunes.setTextColor(Color.parseColor("#003366"));
        viewHolder.txtMartes.setTextColor(Color.parseColor("#003366"));
        viewHolder.txtMiercoles.setTextColor(Color.parseColor("#003366"));
        viewHolder.txtJueves.setTextColor(Color.parseColor("#003366"));
        viewHolder.txtViernes.setTextColor(Color.parseColor("#003366"));
        viewHolder.txtSabado.setTextColor(Color.parseColor("#003366"));
        viewHolder.txtDomingo.setTextColor(Color.parseColor("#003366"));

        if (!repeticiones.equalsIgnoreCase("")) {
            diasRepetir = repeticiones.split(",");
            for (int j = 0; j < diasRepetir.length; j++) {
                if(diasRepetir[j].trim().equalsIgnoreCase("Lun")){
                    viewHolder.txtLunes.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(diasRepetir[j].trim().equalsIgnoreCase("Mar")){
                    viewHolder.txtMartes.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(diasRepetir[j].trim().equalsIgnoreCase("Mie")){
                    viewHolder.txtMiercoles.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(diasRepetir[j].trim().equalsIgnoreCase("Jue")){
                    viewHolder.txtJueves.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(diasRepetir[j].trim().equalsIgnoreCase("Vie")){
                    viewHolder.txtViernes.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(diasRepetir[j].trim().equalsIgnoreCase("Sab")){
                    viewHolder.txtSabado.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(diasRepetir[j].trim().equalsIgnoreCase("Dom")){
                    viewHolder.txtDomingo.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }

        }



        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("¿Está seguro?")
                        .setContentText("Se borrará la Alarma seleccionada.")
                        .setConfirmText("Sí, hazlo!")
                        .setCancelText("Cancelar")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                //TODO Eliminar alarma
                                EliminarAlarma(idAlarma);

                               // int position = items.indexOf(i);
                                items.remove(i);
                                notifyItemRemoved(i);

                                sDialog.cancel();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();

            }
        });

        viewHolder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //TODO Llamar para editar
               // startActivity(new Intent(context, NuevaAlarma.class),
               //         1);
            }
        });

        viewHolder.swtActiva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewHolder.swtActiva.isChecked()){

                    viewHolder.hora.setTextColor(Color.parseColor("#31d831"));

                    Alarma alarma = proveedor.getAlarma(idAlarma);
                    mngAlarma.activarAlarma(alarma);
                }else{

                    viewHolder.hora.setTextColor(Color.parseColor("#db3939"));

                    mngAlarma.CancelarAlarma(idAlarma);
                }

            }
        });



      /*  viewHolder.imagen.setImageResource(items.get(i).getImagen());
        viewHolder.nombre.setText(items.get(i).getNombre());
        viewHolder.visitas.setText("Visitas:"+String.valueOf(items.get(i).getVisitas()));*/
    }

    private void EliminarAlarma(int idAlarma) {
        //Elimina la alarma de la BD
        proveedor.eliminarAlarma(Integer
                .valueOf(idAlarma));
        //Desactivar la alarma
        mngAlarma.CancelarAlarma(idAlarma);

        //Recargar listado
       // notifyDataSetChanged();

    }


}
