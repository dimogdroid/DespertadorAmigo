package difed.despertadoramigo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.skyfishjy.library.RippleBackground;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import difed.api.Conexion;
import difed.api.ConnectionException;
import difed.bd.ProveedorBd;
import difed.bd.ProveedorBdImpl;
import difed.entidades.Alarma;
import difed.util.CustomLog;
import difed.util.ManagerAlarma;

/**
 * Created by dgdavila on 17/09/2015.
 */
public class AlarmaActiva  extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private PowerManager.WakeLock wl;
    private static TextoHablado task;
    private Alarma alarma;
    ProveedorBd proveedor;
    Typeface tf_regular;

    ShimmerTextView txtHora;
    Shimmer shimmer;

    RingtoneManager mRingtoneManager;
    Ringtone rt;
    boolean sinTono = false;

    ManagerAlarma mngAlarma ;
    private TextToSpeech mTts;
    Locale loc;

    String TextoHablado;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PowerManager pm = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "Despertando");
        wl.acquire();

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarma_activa);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);

        rippleBackground.startRippleAnimation();

        ImageView button=(ImageView)findViewById(R.id.centerImage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Hacer DESCARTAR

               //comprobarAlarma();
               mngAlarma = new ManagerAlarma();

               mngAlarma.activarAlarma(alarma);



                finish();
            }
        });

        loc = new Locale("es", "", "");

        // Capturamos el evento que provoc la alarma
        alarma = new Alarma();
        Intent intent = getIntent();
        int idAlarma =intent.getIntExtra("alarma", 0);
        proveedor = ProveedorBdImpl.getProveedor(this);
        alarma = proveedor.getAlarma(idAlarma);

        txtHora = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        tf_regular = Typeface.createFromAsset(getAssets(), "fonts/reloj.ttf");
        txtHora.setTypeface(tf_regular);
        txtHora.setText(alarma.getHora() + ":" + alarma.getMinutos());

        shimmer = new Shimmer();
        shimmer.start(txtHora);

        mTts = new TextToSpeech(this,this);
        mTts.isLanguageAvailable(Locale.getDefault());


        textoAHablar();

        play();




        //txthablar = new TextoHablar(this,alarma);
        //TextoParaRecitar = txthablar.textoAHablar();


    }

    @Override
    public void onInit(int initStatus) {
        // check for successful instantiation

    }


    public void textoAHablar(){
        task = new TextoHablado();
        task.setActivity(this);
        task.execute(alarma);
    }

    class TextoHablado extends AsyncTask<Alarma, Integer, String> {

        private AlarmaActiva activity;

        public void setActivity(AlarmaActiva activity) {
            this.activity = activity;
        }
        Locale loc;
        String tiempo;
        String trafico;

        String prediccionTiempo;
        String[] incidenciasTrafico;
        String[] santoral;


        boolean ExisteTiempo = false;
        boolean ExisteTrafico = false;
        boolean ExisteSantoral = false;



        int contadorIncidencias = 0;
        int contadorsantoral = 0;


        @Override
        protected void onPreExecute() {
            // findViewById(R.id.cargando).setVisibility(View.VISIBLE);
            proveedor = ProveedorBdImpl.getProveedor(activity);
            mngAlarma = new ManagerAlarma();
        }

        @Override
        protected String doInBackground(Alarma... arg0) {

            Alarma alm = arg0[0];

            tiempo = alm.getTiempo();

            if ((tiempo != null) && !(tiempo.equalsIgnoreCase(""))) {
                ExisteTiempo = true;
            }

            trafico = alm.getTrafico();
            if ((trafico != null) && !(trafico.equalsIgnoreCase(""))) {
                ExisteTrafico = true;
            }
            if (alm.isSantoral()) {
                ExisteSantoral = true;
            }

            if (ExisteTiempo) {
                String idProv = proveedor.obtenerIdProvinciaTiempo(tiempo);
                obtenerTiempo(idProv);
            }

            if (ExisteTrafico) {
                String idCom = proveedor.obtenerIdComunidadTrafico(trafico);
                String idProv = proveedor.obtenerIdProvinciaTrafico(trafico);
                obtenerTrafico(idCom, idProv);
                if ((trafico.equalsIgnoreCase("Sevilla"))) {
                    obtenerTraficoEspecial(trafico);
                }

            }

            if (ExisteSantoral) { // Decir el Santoral

                obtenerSantoral();

            }
              /*  if ((alm.getRecordatorio() != null)
                        && (!alm.getRecordatorio().equalsIgnoreCase(""))) {
                    obtenerRecordatorio(alm.getId());
                    ExisteRecordatorio = true;
                }*/

            try {

                if (ExisteTiempo) {
                    if ((prediccionTiempo == null)
                            || (prediccionTiempo.equalsIgnoreCase(""))) {
                        TextoHablado = "No se han podido obtener datos referente al Tiempo.";
                    } else {
                        TextoHablado = "El Tiempo en  " + alm.getTiempo() + ". " + prediccionTiempo;
                    }
                }

                if (ExisteTrafico) {


                    TextoHablado = TextoHablado + " ." + " Incidencias de tráfico.";

                    if (contadorIncidencias > 0) {
                        int i = 0;

                        while (i <= contadorIncidencias) {

                            TextoHablado = TextoHablado + " ." + (incidenciasTrafico[i]);
                        }
                    } else {
                        TextoHablado = TextoHablado + " ." + "No se han encontrado incidencias de tráfico. ";
                    }
                }

                if (ExisteSantoral) {
                    int i = 0;
                    try {

                        TextoHablado = TextoHablado + " ." + "Hoy es el santo de: ";

                        while (i < contadorsantoral) {
                            TextoHablado = TextoHablado + (santoral[i]) + ", ";
                            i++;
                        }
                    } catch (Exception e) {

                    }
                }






                  /*  if (ExisteTiempo || ExisteTrafico || ExisteSantoral
                            || ExisteRecordatorio) {
                       //TODO poner donde se llama al procedimiento

                     /*   tiempoespera = proveedor.tiempoComenzarHablar();

                        if (activadoXAvion) { // ya gastamos los 10seg.
                            tiempoespera = tiempoespera - 10;
                        }

                        if (tiempoespera < 0)
                            tiempoespera = 0;

                        Thread.sleep(tiempoespera * 1000);*/
                //  }
            } catch (Exception e) {
                // Mensaje en caso de que falle
            }
            // }else{
            // repetirTiempo.setEnabled(false);
            // repetirTrafico.setEnabled(false);
            // repetirSantoral.setEnabled(false);
            // }
            return null;
        }

           /* private void obtenerRecordatorio(int id) {
                recordatorio = proveedor.ontenerRecordatorio(id);
            }*/

        private void obtenerSantoral() {

            Conexion conexion = new Conexion("santoral");
            InputStream resultado = null;

            BufferedReader br = null;
            String line = null;

            try {
                resultado = conexion.conectar(activity);

                br = new BufferedReader(new InputStreamReader(resultado,
                        "ISO-8859-1"));

                boolean Encontrado = false;
                try {

                    santoral = new String[30];

                    if (br.read() == -1) {
                        CustomLog.error("AlarmReceiverActivity", "vacio");
                        resultado = null;
                    }

                    while (((line = br.readLine()) != null) && !Encontrado) {
                        if (line.indexOf("lista_nombres") > 0) {
                            Encontrado = true;
                        }
                    }
                    if (Encontrado) {

                        Encontrado = false;
                        String nombre = "";
                        while (((line = br.readLine()) != null) && !Encontrado) {
                            if (line.indexOf("nombre") > 0) {
                                nombre = line.substring(
                                        line.indexOf("nombre") + 8,
                                        line.indexOf("</span>"));
                                santoral[contadorsantoral] = nombre;
                                contadorsantoral++;

                            }
                            if (line.indexOf("</ul>") >= 0) {
                                Encontrado = true;
                            }
                        }

                        santoral[contadorsantoral] = QuitarEtiquetas(santoral[contadorsantoral]);
                        santoral[contadorsantoral] = TildesyMinusculas(santoral[contadorsantoral]);
                        santoral[contadorsantoral] = QuitarParentesis(santoral[contadorsantoral]);

                    } else {
                        resultado = null;
                    }

                } catch (IOException e) {
                    CustomLog.error("AlarmReceiverActivity", e.getMessage());
                    resultado = null;
                } catch (Exception e) {
                    resultado = null;
                    CustomLog.error("AlarmReceiverActivity", e.getMessage());
                }

            } catch (ConnectionException e) {
                resultado = null;
                CustomLog.error("AlarmReceiverActivity", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                resultado = null;
                CustomLog.error("AlarmReceiverActivity", e.getMessage());
            } catch (Exception e) {
                resultado = null;
                CustomLog.error("AlarmReceiverActivity", e.getMessage());
            }
            conexion = null;

        }

        protected void onPostExecute(String prediccion) {

            //findViewById(R.id.cargando).setVisibility(View.GONE);

            //textoHablado();


            HablarTexto();



        }

        private void obtenerTiempo(String idProv) {
            Conexion conexion = new Conexion(idProv, "tiempo");
            InputStream resultado = null;

            BufferedReader br = null;
            String line = null;
            String ProvinciaGrados = null;
            String gMaximo = null;
            String gMinimo = null;

            try {
                resultado = conexion.conectar(activity);

                br = new BufferedReader(new InputStreamReader(resultado,
                        "ISO-8859-1"));

                boolean Encontrado = false;

                prediccionTiempo = "";
                try {

                    if (br.read() == -1) {
                        CustomLog.error("AlarmReceiverActivity", "vacio");
                        resultado = null;
                    }

                    while (((line = br.readLine()) != null) && !Encontrado) {
                        if (line.indexOf("contenedor_central") > 0) {
                            Encontrado = true;
                        }
                    }

                    Encontrado = false;
                    // Ahora buscamos el texto_normal
                    while (((line = br.readLine()) != null) && !Encontrado) {
                        if (line.indexOf("texto_normal") > 0) {

                            Encontrado = true;
                        }
                    }

                    if (Encontrado) {
                        // Coger hasta encontrar </p>
//                        Encontrado = false;
                        prediccionTiempo = prediccionTiempo + line;
//                        while (((line = br.readLine()) != null) && !Encontrado) {
//                            prediccionTiempo = prediccionTiempo + line;
//                            if (line.indexOf("</div>") > 0) {
//                                Encontrado = true;
//                            }
//                        }

                        // Buscar maximos y minimos
                        Encontrado = false;
                        while (((line = br.readLine()) != null) && !Encontrado) {
                            if (line.indexOf("tabla_datos") >= 0) {
                                Encontrado = true;
                            }
                        }
                        if (Encontrado) {

                            Encontrado = false;
                            while (((line = br.readLine()) != null)
                                    && !Encontrado) {
                                if (line.indexOf("borde_rlb") >= 0) {
                                    ProvinciaGrados = line.substring(
                                            line.indexOf(">") + 1,
                                            line.indexOf("</th>"));
                                    ProvinciaGrados = QuitarParentesis(ProvinciaGrados);
                                    ProvinciaGrados = ProvinciaGrados.trim();

                                    String transforma = Transforma(tiempo);

                                    String sinTilde = QuitarTildes(transforma);

                                    if (ProvinciaGrados
                                            .equalsIgnoreCase(sinTilde)) {
                                        while (((line = br.readLine()) != null)
                                                && !Encontrado) {
                                            if (line.indexOf("texto_rojo") >= 0) {
                                                gMaximo = line
                                                        .substring(
                                                                line.indexOf("texto_rojo") + 12,
                                                                line.indexOf("</span>"));
                                                Encontrado = true;
                                            }
                                        }

                                        if (line.indexOf("texto_azul") >= 0) {
                                            gMinimo = line
                                                    .substring(
                                                            line.indexOf("texto_azul") + 12,
                                                            line.indexOf("</span>"));
                                            Encontrado = true;
                                        }

                                        Encontrado = true;
                                    }

                                }
                            }
                            // Añadir a prediccion los grados
                            prediccionTiempo = prediccionTiempo + " máxima de " + gMaximo
                                    + " grados, mínima de " + gMinimo
                                    + " grados.";

                        }

                    } else {
                        resultado = null;
                    }

                    prediccionTiempo = TildesyMinusculas(prediccionTiempo);
                    prediccionTiempo = QuitarEtiquetas(prediccionTiempo);
                    prediccionTiempo = QuitarParentesis(prediccionTiempo);

                } catch (IOException e) {
                    CustomLog.error("TextoHablado", e.getMessage());
                    resultado = null;
                } catch (Exception e) {
                    resultado = null;
                    CustomLog.error("TextoHablado", e.getMessage());
                }

            } catch (ConnectionException e) {
                resultado = null;
                CustomLog.error("TextoHablado", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                resultado = null;
                CustomLog.error("TextoHablado", e.getMessage());
            } catch (Exception e) {
                resultado = null;
                CustomLog.error("TextoHablado", e.getMessage());
            }

            conexion = null;
        }

        private String Transforma(String provi) {
            String transformada = provi;

            if (provi.equalsIgnoreCase("ALICANTE")) {
                transformada = "ALACANT";
            }
            if (provi.equalsIgnoreCase("CANTABRIA")) {
                transformada = "SANTANDER";
            }
            if (provi.equalsIgnoreCase("A CORUÑA")) {
                transformada = "A CORUNA";
            }
            if (provi.equalsIgnoreCase("ASTURIAS")) {
                transformada = "OVIEDO";
            }
            if (provi.equalsIgnoreCase("ALAVA")) {
                transformada = "VITORIA";
            }
            if (provi.equalsIgnoreCase("Guipuzcoa")) {
                transformada = "SAN SEBASTIAN";
            }
            if (provi.equalsIgnoreCase("NAVARRA")) {
                transformada = "PAMPLONA";
            }
            if (provi.equalsIgnoreCase("León")) {
                transformada = "LEON";
            }
            if (provi.equalsIgnoreCase("Cáceres")) {
                transformada = "CACERES";
            }
            if (provi.equalsIgnoreCase("CASTELLÓN")) {
                transformada = "CASTELLO";
            }

            return transformada;
        }

        private void obtenerTrafico(String idCom, String idProv) {


            Conexion conexion = new Conexion(idCom, idProv, "trafico");
            InputStream resultado = null;

            BufferedReader br = null;
            String line = null;

            String fecha = "";

            try {

                incidenciasTrafico = new String[30];

                resultado = conexion.conectar(activity);

                br = new BufferedReader(new InputStreamReader(resultado,
                        "ISO-8859-1"));

                boolean Encontrado = false;
                try {
                    if (br.read() == -1) {
                        CustomLog.error("TextoHablado", "vacio");
                        resultado = null;
                    }

                    boolean bNoFecha = true;
                    String aBuscar = "bg2";
                    while (bNoFecha) {
                        while (((line = br.readLine()) != null) && !Encontrado) {
                            if (line.indexOf(aBuscar) > 0) {
                                fecha = line.substring(
                                        line.indexOf("<br/>") + 5,
                                        line.length() - 5);
                                Encontrado = true;
                            }
                        }

                        if (mngAlarma.fechaActual(fecha)) {

                            Encontrado = false;
                            // Ahora buscamos el texto_normal
                            while (((line = br.readLine()) != null)
                                    && !Encontrado) {
                                if (line.indexOf("nombreIncidencia2") > 0) {
                                    Encontrado = true;
                                }
                            }

                            if (Encontrado) {
                                // Coger hasta encontrar </p>
                                Encontrado = false;
                                incidenciasTrafico[contadorIncidencias] = line;
                                while (((line = br.readLine()) != null)
                                        && !Encontrado) {
                                    incidenciasTrafico[contadorIncidencias] = incidenciasTrafico[contadorIncidencias]
                                            + line;
                                    if (line.indexOf("</td>") >= 0) {
                                        Encontrado = true;
                                    }
                                }

                                incidenciasTrafico[contadorIncidencias] = TildesyMinusculas(incidenciasTrafico[contadorIncidencias]);
                                incidenciasTrafico[contadorIncidencias] = QuitarEtiquetas(incidenciasTrafico[contadorIncidencias]);
                                incidenciasTrafico[contadorIncidencias] = QuitarParentesis(incidenciasTrafico[contadorIncidencias]);

                                if (aBuscar.equalsIgnoreCase("bg2")) {
                                    Encontrado = false;
                                    aBuscar = "bg1";
                                } else {
                                    Encontrado = false;
                                    aBuscar = "bg2";
                                }
                                contadorIncidencias++;
                            }
                        } else {
                            bNoFecha = false;
                        }
                    }

                } catch (IOException e) {
                    CustomLog.error("AlarmReceiverActivity", e.getMessage());
                    resultado = null;
                } catch (Exception e) {
                    resultado = null;
                    CustomLog.error("AlarmReceiverActivity", e.getMessage());
                }

            } catch (ConnectionException e) {
                resultado = null;
                CustomLog.error("AlarmReceiverActivity", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                resultado = null;
                CustomLog.error("AlarmReceiverActivity", e.getMessage());
            } catch (Exception e) {
                resultado = null;
                CustomLog.error("AlarmReceiverActivity", e.getMessage());
            }
            conexion = null;
        }

        /**
         * Trafico especial
         */
        private void obtenerTraficoEspecial(String provincia) {

            Conexion conexion = new Conexion(provincia);
            conexion.conexionEspecial(provincia);

            InputStream resultado = null;

            BufferedReader br = null;
            String line = null;

            try {

                resultado = conexion.conectar(activity);

                br = new BufferedReader(new InputStreamReader(resultado,
                        "ISO-8859-1"));

                boolean Encontrado = false;
                try {
                    if (br.read() == -1) {
                        CustomLog.error("AlarmReceiverActivity", "vacio");
                        resultado = null;
                    }

                    if (provincia.equalsIgnoreCase("Sevilla")) {

                        String aBuscar = ".-";

                        while (((line = br.readLine()) != null) && !Encontrado) {
                            if (line.indexOf(aBuscar) > 0) {
                                incidenciasTrafico[contadorIncidencias] = incidenciasTrafico[contadorIncidencias]
                                        + line.substring(line.indexOf(".-"));
                                Encontrado = true;
                            }
                        }
                    }
                    // if (provincia.equalsIgnoreCase("Granada")) {
                    //
                    // String aBuscar = "#trafico";
                    //
                    // while (((line = br.readLine()) != null) && !Encontrado) {
                    // if (line.indexOf(aBuscar) > 0) {
                    // incidencias[contadorIncidencias] =
                    // incidencias[contadorIncidencias]
                    // + line.substring(line.indexOf(".-"));
                    // Encontrado = true;
                    // }
                    // }
                    // }
                    // Encontrado = false;
                    // aBuscar = "</script>";
                    // while (((line = br.readLine()) != null) && !Encontrado) {
                    // if (line.indexOf(aBuscar) > 0) {
                    // Encontrado = true;
                    // }
                    // }
                    // Encontrado = false;
                    // while (((line = br.readLine()) != null) && !Encontrado) {
                    // incidencias[contadorIncidencias] =
                    // incidencias[contadorIncidencias]
                    // + line;
                    // if (line.indexOf("</div>") >= 0) {
                    // Encontrado = true;
                    // }
                    // }

                    incidenciasTrafico[contadorIncidencias] = TildesyMinusculas(incidenciasTrafico[contadorIncidencias]);
                    incidenciasTrafico[contadorIncidencias] = QuitarEtiquetas(incidenciasTrafico[contadorIncidencias]);
                    incidenciasTrafico[contadorIncidencias] = QuitarParentesis(incidenciasTrafico[contadorIncidencias]);

                } catch (IOException e) {
                    CustomLog.error("AlarmReceiverActivity", e.getMessage());
                    resultado = null;
                }

            } catch (ConnectionException e) {
                resultado = null;
                CustomLog.error("AlarmReceiverActivity", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                resultado = null;
                CustomLog.error("AlarmReceiverActivity", e.getMessage());
            } catch (Exception e) {
                resultado = null;
                CustomLog.error("AlarmReceiverActivity", e.getMessage());
            }
            conexion = null;
        }


        public String QuitarEtiquetas(String enviada) {
            String limpia = "";

            try {

                if (enviada == null) {
                    return null;
                }

                limpia = enviada;

                // String limpia=sucia;
                if (limpia.indexOf("<p>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("<p>", "");
                }
                if (limpia.indexOf("</p>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("</p>", "");
                }
                if (limpia.indexOf("<br>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("<br>", "");
                }
                if (limpia.indexOf("<br/>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("<br/>", "");
                }
                if (limpia.indexOf("<\\t>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("<\\t>", "");
                }
                if (limpia.indexOf("null'") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("null", "");
                }
                // if (limpia.indexOf(",") >= 0) { // grados del satelite
                // limpia = limpia.replaceAll(",", "");
                // }
                if (limpia.indexOf("</div>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("</div>", "");
                }

                if (limpia.indexOf("<\\n>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("<\\n>", "");
                }
                if (limpia.indexOf("<\\t>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("<\\t>", "");
                }

                if (limpia.indexOf("<strong>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("<strong>", "");
                }
                if (limpia.indexOf("</strong>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("</strong>", "");
                }
                if (limpia.indexOf("<dl") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("<dl", "");
                }
                if (limpia.indexOf("<dd>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("<dd>", "");
                }
                if (limpia.indexOf("-") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("-", " ");
                }

                if (limpia.indexOf("class=\"marginTop0") >= 0) { // grados
                    // del
                    // satelite
                    limpia = limpia.replaceAll("class=\"marginTop0", "");
                }

                if (limpia.indexOf("marginBottom0\">") >= 0) { // grados
                    // del
                    // satelite
                    limpia = limpia.replaceAll("marginBottom0\">", "");
                }

                if (limpia.indexOf("class=\"margintop0") >= 0) {
                    limpia = limpia.replaceAll("class=\"margintop0", "");
                }

                if (limpia.indexOf("marginbottom0\">") >= 0) {
                    limpia = limpia.replaceAll("marginbottom0\">", "");
                }

                if (limpia.indexOf("</dd>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("</dd>", "");
                }
                if (limpia.indexOf("</dl>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("</dl>", ". ");
                }
                if (limpia.indexOf("</td>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("</td>", ".");
                }
                if (limpia.indexOf("<font") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("<font", "");
                }
                if (limpia.indexOf("color=\"#ab3000\">") >= 0) { // grados del
                    // satelite
                    limpia = limpia.replaceAll("color=\"#ab3000\">", "");
                }
                if (limpia.indexOf("</font>") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("</font>", "");
                }

                if (limpia.indexOf("/") >= 0) { // grados del satelite
                    limpia = limpia.replaceAll("/", ",");
                }

            } catch (Exception ex) {
                CustomLog.error("limpiarString", ex.getMessage());
            }

            return limpia;
        }

        public String QuitarParentesis(String cadena) {
            String limpia = null;
            String parte1 = null;
            String parte2 = null;

            try {

                try {

                    if (cadena == null) {
                        return null;
                    }

                    limpia = cadena;

                    while (limpia.indexOf("(") >= 0) {

                        parte1 = limpia.substring(0, limpia.indexOf("("));
                        parte2 = limpia.substring(limpia.indexOf(")") + 1);

                        limpia = parte1 + " " + parte2;

                    }
                } catch (Exception e) {

                }
            } catch (Exception e) {

            }

            return limpia;
        }

        public String TildesyMinusculas(String textoHablado) {

            String cadena = textoHablado;

            loc = new Locale("es", "", "");

            if (textoHablado != null) {
                cadena = textoHablado.toLowerCase(loc);
                cadena = cadena.replaceAll("minimo", "mánimo");
                cadena = cadena.replaceAll("trafico", "tráfico");
                cadena = cadena.replaceAll("minima", "mínima");
                cadena = cadena.replaceAll("maximo", "máximo");
                cadena = cadena.replaceAll("maxima", "máxima");
                cadena = cadena.replaceAll("evolucion", "evolución");
                cadena = cadena.replaceAll(" c ", "grados");
                cadena = cadena.replaceAll("area", "área");
                cadena = cadena.replaceAll("alcala", "alcalá");
                cadena = cadena.replaceAll("debiles", "débiles");
                cadena = cadena.replaceAll("iran", "irán");
                cadena = cadena.replaceAll(" w ", "oeste");
                cadena = cadena.replaceAll(" nw ", "noroeste");
                cadena = cadena.replaceAll(" e ", "este");
                cadena = cadena.replaceAll(" ne ", "noreste");
                cadena = cadena.replaceAll(" n ", "norte");
                cadena = cadena.replaceAll(" s ", "sur");
                cadena = cadena.replaceAll(" sw ", "suroeste");
                // cadena = cadena.replaceAll(" se ", "sureste");
                cadena = cadena.replaceAll("regimen", "régimen");
                cadena = cadena.replaceAll("aviles", "avilés");
                cadena = cadena.replaceAll("manana", "mañana");
                cadena = cadena.replaceAll("depresion", "depresión");

                cadena = cadena.replaceAll("malaga", "málaga");

                cadena = cadena.replaceAll("km", "kilómetro");

                cadena = cadena.replaceAll("direccion", "dirección");
                cadena = cadena.replaceAll("debil", "débil");
                cadena = cadena.replaceAll("olimpica", "olímpica");

                cadena = cadena.replaceAll(" se-", " s e ");
                cadena = cadena.replaceAll(" ap-", " a p ");
                cadena = cadena.replaceAll(" al-", " a l ");
                cadena = cadena.replaceAll(" ca-", " c a ");
                cadena = cadena.replaceAll(" co-", " c o ");
                cadena = cadena.replaceAll(" hu-", " h u ");
                cadena = cadena.replaceAll(" ma-", " m a ");
                cadena = cadena.replaceAll(" ja-", " j a ");

                cadena = cadena.replaceAll("null", " ");

            }
            return cadena;
        }

        public String QuitarTildes(String tiempo) {
            String sinTilde = null;

            sinTilde = tiempo;

            if (sinTilde != null) {

                sinTilde = sinTilde.replace('á', 'a');
                sinTilde = sinTilde.replace('é', 'e');
                sinTilde = sinTilde.replace('í', 'i');
                sinTilde = sinTilde.replace('ó', 'o');
                sinTilde = sinTilde.replace('ú', 'u');
            }
            return sinTilde;
        }
    }



    @SuppressWarnings("deprecation")
    private void HablarTexto() {

        if (!sinTono) {
            try {
                if (rt != null) {
                    if (rt.isPlaying()) {
                        rt.stop();
                    }
                }
            } catch (Exception e) {
            }
        }
        //TODO
//        if (alarma.isVibracion()) {
//            v.cancel();
//        }
       Locale loc = new Locale("es", "", "");
       if (mTts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE) {
           mTts.setLanguage(loc);
       }



        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());

        String aux = formattedDate.substring(0, 2);
        String aux2 = formattedDate.substring(3);

        int auxInt = Integer.valueOf(aux);
        int aux2Int = Integer.valueOf(aux2);

        String cadenaSaludo = "";

        if ((auxInt >= 4) && (auxInt <= 12)) {
            cadenaSaludo = " Buenas Días. ";
        } else {
            if ((auxInt > 12) && (auxInt <= 20)) {
                cadenaSaludo = " Buenas Tardes. ";
            } else {

                cadenaSaludo = " Buenas Noches. ";
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTts.speak(cadenaSaludo + " Son las " + auxInt + " Horas y " + aux2Int
                    + " minutos.", TextToSpeech.QUEUE_FLUSH, null, null);

            mTts.speak(TextoHablado, TextToSpeech.QUEUE_ADD, null, null);

        }else{
            mTts.speak(cadenaSaludo + " Son las " + auxInt + " Horas y " + aux2Int
                    + " minutos.", TextToSpeech.QUEUE_FLUSH, null);
            mTts.speak(TextoHablado, TextToSpeech.QUEUE_ADD, null);
        }




    }





//    private void comprobarAlarma() {
//
//        if (mngAlarma.proximaAlarma(this).equalsIgnoreCase("")) {
//
//            mngAlarma.CancelarAlarma(alarma.getId());
//
////            Intent myIntent = new Intent(this, ComprobarConexiones.class);
////            PendingIntent pendingIntent = PendingIntent.getService(this, 0,
////                    myIntent, 0);
////            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
////            alarmManager.cancel(pendingIntent);
//
//            // siempre, por si viene de posponer.
//          //  mgr.cancel(notifyID);
//
//        } else {
//
//              mngAlarma.activarAlarma(alarma);
//
////            Alarma AlarmaSiguiente = comun.SiguienteAlarmaActivar(this);
////            // Activar esta alarma
////            if (AlarmaSiguiente != null) {
////
////                comun.activarAlarma(this, AlarmaSiguiente);
////
////                // activarAlarma(AlarmaSiguiente);
////
////                String nombre = "";
////
////                if ((AlarmaSiguiente.getNombre() != null)
////                        && !(AlarmaSiguiente.getNombre().equalsIgnoreCase(""))
////                        && !(AlarmaSiguiente.getNombre()
////                        .equalsIgnoreCase("Ninguno"))) {
////                    nombre = MaysPrimera(AlarmaSiguiente.getNombre());
////                } else {
////                    nombre = "--Sin Nombre--";
////                }
////
////                String proxima = comun.proximaAlarma(this);
////
////                if (proxima.equalsIgnoreCase("")) {
////
////                    if (notificacionCustom.equalsIgnoreCase("1")) {
////                        mgr.cancel(notifyID);
////                    }
////                    if (notificacionReloj.equalsIgnoreCase("1")) {
////                        setStatusBarIcon(this, false);
////                    }
////                } else {
////                    if (notificacionReloj.equalsIgnoreCase("1")) {
////                        setStatusBarIcon(this, true);
////                    }
////                    proxima = MaysPrimera(proxima);
////                    if (notificacionCustom.equalsIgnoreCase("1")) {
////                        mgr.notify(notifyID, getSimple(nombre, proxima).build());
////                    }
////                }
////            }
//
//        }
//
//
//    }

    private void play() {

        Uri uri = null;
        try {
            if (!alarma.getTono().equalsIgnoreCase("")) {

                uri = Uri.parse(alarma.getTono());

            } else {
                uri = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if (uri == null) {
                    uri = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_ALARM);
                }
                // sinTono = true;
            }
        } catch (Exception e) {

            CustomLog.error("Play", e.toString());

        }

        if (uri != null) {

            try {
                rt = mRingtoneManager.getRingtone(this, uri);
                rt.play();
            } catch (Exception e) {

                CustomLog.error("Play", e.toString());

            }

        } else {
            sinTono = true;
        }

        // CustomLog.error("Play", uri.getPath());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!sinTono) {
            try {
                if (rt != null) {
                    if (rt.isPlaying()) {
                        rt.stop();
                    }
                }
            } catch (Exception e) {
            }
        }

        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }


    }

    protected void conectar(Alarma alarma) {


    }




}
