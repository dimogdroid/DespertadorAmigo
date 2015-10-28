package difed.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.sql.Time;
import java.util.Calendar;

import difed.bd.ProveedorBd;
import difed.bd.ProveedorBdImpl;
import difed.despertadoramigo.AlarmaActiva;
import difed.entidades.Alarma;

/**
 * Created by dgdavila on 16/09/2015.
 */
public class ManagerAlarma {

    ProveedorBd proveedor;
    Context context;

    public ManagerAlarma() {
    }

    public ManagerAlarma(ProveedorBd proveedor, Context context) {
        this.proveedor = proveedor;
        this.context = context;
    }

    //TODO CANCELAR ALARMA
    public void CancelarAlarma (Alarma alarma){
        Intent intent = new Intent(context, AlarmaActiva.class);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                alarma.getId(), intent, 0);
        alarmManager.cancel(pendingIntent);


    }
    //TODO CANCELAR ALARMA
    public void CancelarAlarma (int idAlarma){

        Intent intent = new Intent(context, AlarmaActiva.class);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                idAlarma, intent, 0);
        alarmManager.cancel(pendingIntent);


    }

    public void activarAlarma(Alarma alarma) {


        Calendar cal = Calendar.getInstance();

        cal = AlarmaOn(alarma);

        if (cal != null) {  //Activar


            cal.set(Calendar.SECOND, 0);

            AlarmManager am = (AlarmManager) context
                    .getSystemService(Activity.ALARM_SERVICE);

            Intent intent = new Intent(context, AlarmaActiva.class);
            intent.putExtra("alarma", alarma.getId());

            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    alarma.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * Devuelve el valor en milisegundos de la alarma
     * @param alarma
     * @return
     */
    private Calendar AlarmaOn(Alarma alarma) {

            boolean Encontrada = false;

            int sigDia;

            //Instacnciado con ala hora actual
            Calendar calADevolver = Calendar.getInstance();

            //calADevolver.set(Calendar.HOUR_OF_DAY, now.hour);
            //calADevolver.set(Calendar.MINUTE, now.minute);
            String[] arrayDias =null;
            String diasRepetir = alarma.getRepetir();

            if (!diasRepetir.isEmpty()){
                arrayDias = diasRepetir.split(",");
            }

            if (arrayDias==null) {
                if (HoraDeAlarmaMayorHoraActual(alarma)) {
                    calADevolver.set(Calendar.HOUR_OF_DAY,
                            Integer.valueOf(alarma.getHora()));
                    calADevolver.set(Calendar.MINUTE,
                            Integer.valueOf(alarma.getMinutos()));
                    calADevolver.set(Calendar.SECOND, 0);
                    calADevolver.set(Calendar.DAY_OF_WEEK, diaDeHoy());
                } else {
                      calADevolver = null;
//                    calADevolver.set(Calendar.HOUR_OF_DAY,
//                            Integer.valueOf(alarma.getHora()));
//                    calADevolver.set(Calendar.MINUTE,
//                            Integer.valueOf(alarma.getMinutos()));
//                    calADevolver.set(Calendar.SECOND, 0);
//                    calADevolver.add(Calendar.DAY_OF_WEEK, 1);
                }
            } else {
                if (arrayDias.length == 1) {
                    sigDia = siguienteDia(alarma);
                    calADevolver.set(Calendar.HOUR_OF_DAY,
                            Integer.valueOf(alarma.getHora()));
                    calADevolver.set(Calendar.MINUTE,
                            Integer.valueOf(alarma.getMinutos()));
                    calADevolver.set(Calendar.SECOND, 0);
                    calADevolver.add(Calendar.DATE, sigDia);
                } else {
                    if (ExisteDia(alarma, diaDeHoy())) {
                        if (HoraDeAlarmaMayorHoraActual(alarma)) {
                            calADevolver.set(Calendar.HOUR_OF_DAY,
                                    Integer.valueOf(alarma.getHora()));
                            calADevolver.set(Calendar.MINUTE,
                                    Integer.valueOf(alarma.getMinutos()));
                            calADevolver.set(Calendar.SECOND, 0);
                            calADevolver.set(Calendar.DAY_OF_WEEK, diaDeHoy());
                            Encontrada = true;
                        }
                    }
                    if (!ExisteDia(alarma, diaDeHoy()) || (!Encontrada)) {

                        sigDia = siguienteDia(alarma);
                        calADevolver.set(Calendar.HOUR_OF_DAY,
                                Integer.valueOf(alarma.getHora()));
                        calADevolver.set(Calendar.MINUTE,
                                Integer.valueOf(alarma.getMinutos()));
                        calADevolver.set(Calendar.SECOND, 0);
                        // calADevolver.set(Calendar.DAY_OF_WEEK, sigDia);
                        calADevolver.add(Calendar.DATE, sigDia);

                    }

                }
            }

            return calADevolver;



    }

    private boolean HoraDeAlarmaMayorHoraActual(Alarma alarma) {

        boolean superior = false;

        Calendar calAlarma = Calendar.getInstance();

        calAlarma.set(Calendar.HOUR_OF_DAY, Integer.valueOf(alarma.getHora()));
        calAlarma.set(Calendar.MINUTE, Integer.valueOf(alarma.getMinutos()));

        Calendar calActual = Calendar.getInstance();

        long diferencia = calActual.getTimeInMillis()
                - calAlarma.getTimeInMillis();

        if (diferencia < 0) {
            superior = true;
        }

        return superior;
    }
    private int diaDeHoy() {
        Calendar calFechaActual = Calendar.getInstance();
        int diaDeHoy = calFechaActual.get(Calendar.DAY_OF_WEEK);
        return diaDeHoy;
    }
    public int siguienteDia(Alarma alarma) {

        int sigDia = -1;
        boolean Encontrada = false;
        boolean EncontradoDia = false;

        int pasos = 0;

        int control = 0;

        if (ExisteDia(alarma, diaDeHoy())) {
            if (HoraDeAlarmaMayorHoraActual(alarma)) {
                sigDia = diaDeHoy();
                Encontrada = true;
            } else {
                Encontrada = false;
            }
        }
        if (!ExisteDia(alarma, diaDeHoy()) || (!Encontrada)) {

            sigDia = diaDeHoy();
            pasos = 0;

            while (!EncontradoDia) {

                sigDia++;
                pasos++;
                if (sigDia > 7) {
                    sigDia = 1;
                }

                if (ExisteDia(alarma, sigDia)) {
                    EncontradoDia = true;
                    // return sigDia;
                }
                control++;
                if (control > 10) {
                    break;
                }

            }

        }
        return pasos;
    }

    /**
     * Mira si existe el dia actual en la cadena repetir.
     *
     * @param alarma
     * @return
     */
    public boolean ExisteDia(Alarma alarma, int dia) {

        boolean existe = false;
        int diaDeAlarma = -1;

        String diasRepetir = alarma.getRepetir();
        String[] arrayDias = diasRepetir.split(",");

        for (int i = 0; i < arrayDias.length; i++) {

            proveedor = ProveedorBdImpl.getProveedor(context);
            diaDeAlarma = proveedor.diaEnInt(arrayDias[i].trim());

            if (dia == diaDeAlarma) {
                existe = true;
            }

        }

        return existe;
    }

    public boolean fechaActual(String fecha) {

        Calendar c = Calendar.getInstance();

        int dia = (c.get(Calendar.DATE));
        int mes = (c.get(Calendar.MONTH)) + 1;
        int annio = (c.get(Calendar.YEAR));

        if ((fecha == null) || (fecha.equalsIgnoreCase(""))) {
            return false;
        }

        try {

            int diaFecha = Integer.parseInt(fecha.substring(0, 2));
            int mesFecha = Integer.parseInt(fecha.substring(3, 5));
            int anioFecha = Integer.parseInt(fecha.substring(6, 10));

            if ((dia == diaFecha) && (mes == mesFecha) && (annio == anioFecha)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }


}
