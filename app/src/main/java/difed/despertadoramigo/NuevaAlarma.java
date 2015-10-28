package difed.despertadoramigo;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Slider;
import com.rey.material.widget.TextView;
import com.rey.material.widget.TimePicker;

import java.util.Calendar;

import difed.bd.ProveedorBd;
import difed.bd.ProveedorBdImpl;
import difed.entidades.Alarma;
import difed.util.ManagerAlarma;

/**
 * Created by dgdavila on 10/09/2015.
 */
public class NuevaAlarma extends AppCompatActivity {

    ImageButton btnHora;
    ImageButton btnRepeticiones;
    ImageButton btnPostponer;
    ImageButton btnTono;
    ImageButton btnTiempo;
    ImageButton btnTrafico;
    Slider sldPosponer;
    EditText edtNombreAlarma;
    TextView txtHora;
    TextView txtRepetir;
    TextView txtPosponer;
    TextView txtTono;
    TextView txtTiempo;
    TextView txtTrafico;
    CheckBox chkSantoral;
    CheckBox chkVibracion;

    TextView txtMinPost;
    String minutosPostponer;

    private int mHour;
    private int mMinute;

    Typeface tf_regular;

    RingtoneManager mRingtoneManager;
    Cursor mcursor;
    Intent Mringtone;

    ProveedorBd proveedor;
    ManagerAlarma mng;

    long idAlarma;
    Alarma alarma;
    Alarma alarmaModificada;
    String sRepeticiones = "";
    String sPosponer = "";
    String sTiempo = "";
    String sTrafico = "";
    boolean bSantoral = false;
    String sRecordatorio = "";
    String sNombre = "";
    boolean bVibracion = false;
    String sTono = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_alarma);

        proveedor = ProveedorBdImpl.getProveedor(this);
        mng = new ManagerAlarma(proveedor,this);

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        tf_regular = Typeface.createFromAsset(getAssets(), "fonts/reloj.ttf");

        edtNombreAlarma = (EditText) findViewById(R.id.nombreAlarma);


        btnHora = (ImageButton) findViewById(R.id.btnHora);
        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    MostrarRelog();

            }

        });
        txtHora = (TextView) findViewById(R.id.txtHora);
        txtHora.setTypeface(tf_regular);
        txtHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarRelog();
            }
        });

        btnRepeticiones = (ImageButton) findViewById(R.id.btnRepetir);
        btnRepeticiones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MostrarRepeticiones();

            }

        });
        txtRepetir = (TextView) findViewById(R.id.txtRepetir);
        txtRepetir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarRepeticiones();
            }
        });

        btnPostponer = (ImageButton) findViewById(R.id.btnPosponer);
        btnPostponer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MostrarPostponer();

            }

        });
        txtPosponer = (TextView) findViewById(R.id.txtPosponer);
        txtPosponer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarPostponer();
            }
        });

        btnTono = (ImageButton) findViewById(R.id.btnTono);
        btnTono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MostrarTono();

            }

        });
        txtTono = (TextView) findViewById(R.id.txtTono);
        txtTono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarTono();
            }
        });

        btnTiempo = (ImageButton) findViewById(R.id.btnTiempo);
        btnTiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MostrarElTiempo();

            }

        });
        txtTiempo = (TextView) findViewById(R.id.txtTiempo);

        btnTrafico = (ImageButton) findViewById(R.id.btnTrafico);
        btnTrafico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MostrarElTrafico();

            }

        });
        txtTrafico = (TextView) findViewById(R.id.txtTrafico);

        chkSantoral = (CheckBox) findViewById(R.id.chkSantoral);

        chkVibracion= (CheckBox) findViewById(R.id.chkVibracion);






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nueva, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getTitle()) {
            case 0:
                // do whatever
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/

        //Solo hay una opción en el menú

        sNombre = edtNombreAlarma.getText().toString();
        bSantoral = chkSantoral.isChecked();
        bVibracion = chkVibracion.isChecked();

        // Guardar los datos
        // 1. Creamos el objeto Alarma
        CrearAlarma();

        //TODO
        // if (modificando) {
        //    proveedor.modificarAlarma(alarma.getId(), alarma);
        //    idAlarma = alarma.getId();
        //} else {
        idAlarma = proveedor.guardarAlarma(alarma);
        //}

        // Activar Alarma
        //La activamos en el Main Principal.
        //mng.activarAlarma(alarma);

        finish();

        return true;
    }
    private void CrearAlarma() {

        alarma = new Alarma();

       /*TODO
        if (modificando) {
            alarma.setId(alarmaModificada.getId());
        }
        */
        alarma.setNombre(sNombre);
        alarma.setHora(pad(mHour));
        alarma.setMinutos(pad(mMinute));
        alarma.setRepetir(sRepeticiones);
        alarma.setPosponer(sPosponer);
        alarma.setTono(sTono);
        alarma.setTiempo(sTiempo);
        alarma.setTrafico(sTrafico);
        alarma.setSantoral(bSantoral);
        alarma.setRecordatorio(sRecordatorio);
        alarma.setVibracion(bVibracion);
        alarma.setActiva(true);

    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


    // TONO
    // -------

    private void MostrarTono() {

        // Starts the intent or Activity of the ringtone manager, opens popup
        // box
        Mringtone = new Intent(mRingtoneManager.ACTION_RINGTONE_PICKER);

        // specifies what type of tone we want, in this case "ringtone", can be
        // notification if you want
        Mringtone.putExtra(mRingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_RINGTONE);

        // gives the title of the RingtoneManager picker title
        Mringtone.putExtra(mRingtoneManager.EXTRA_RINGTONE_TITLE,
                "Elige el tono que desee");

        // returns true shows the rest of the song on the device in the default
        // location
        Mringtone.getBooleanExtra(mRingtoneManager.EXTRA_RINGTONE_INCLUDE_DRM,
                true);

        String uri = null;
        // chooses and keeps the selected item as a uri
        if (uri != null) {
            Mringtone.putExtra(mRingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    Uri.parse(uri));
        } else {
            Mringtone.putExtra(mRingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    (Uri) null);
        }

        startActivityForResult(Mringtone, 999);

    }

    // To handle when an image is selected from the browser, add the following
    // to your Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String test = "";

        if (resultCode == RESULT_OK) {

            if (requestCode == 999) {

                Uri uri = data
                        .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (uri != null) {
                    test = uri.toString();
                }
                txtTono.setText(test);
                sTono = test;

            }
        }
    }

    private void MostrarElTrafico() {

        new MaterialDialog.Builder(this)
                .title("Elige una Provincia")
                .items(R.array.listProvincias)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/

                        txtTrafico.setText(text);
                        sTrafico = text.toString();

                        return true;
                    }
                })
                .show();
    }

    private void MostrarElTiempo() {

        new MaterialDialog.Builder(this)
                .title("Elige una Provincia")
                .items(R.array.listProvincias)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/

                        txtTiempo.setText(text);
                        sTiempo = text.toString();

                        return true;
                    }
                })
                .show();
    }

    private void MostrarPostponer() {

        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Tiempo a Postponer")
                .customView(R.layout.postponer, wrapInScrollView)
                .positiveText("OK")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        //Actualizamos etiqueta del botón

                        txtPosponer.setText(minutosPostponer);


                    }
                })
                .show();

        View v = dialog.getCustomView();
        txtMinPost = (TextView) v.findViewById(R.id.txtminutosPostponer);
        sldPosponer = (Slider) v.findViewById(R.id.sldPosponer);
        sldPosponer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actualizar minutos en label
                minutosPostponer = ((int) sldPosponer.getExactValue()) + " minutos";
                txtMinPost.setText(minutosPostponer);
                sPosponer = String.valueOf(sldPosponer.getExactValue());


            }
        });

    }

    private void MostrarRepeticiones() {

        new MaterialDialog.Builder(this)
                .title("Repetir")
                .items(R.array.repeticiones)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        /**
                         * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected check box to actually be selected.
                         * See the limited multi choice dialog example in the sample project for details.
                         **/
                        String repeticiones = "Repeticiones";

                        if (text.length > 0) {
                            repeticiones = "";
                            for (int i = 0; i < text.length; i++) {

                                repeticiones = repeticiones + text[i].subSequence(0, 3) + ", ";

                            }
                            repeticiones = repeticiones.substring(0, repeticiones.length() - 2);
                        }

                        txtRepetir.setText(repeticiones);
                        sRepeticiones = repeticiones;

                        return true;
                    }
                })
                .positiveText("OK")
                .show();
    }

    private void MostrarRelog(){
        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay,
                                          int minute) {

                        txtHora.setText(pad(hourOfDay) + ":" + pad(minute));

                        mHour = hourOfDay;
                        mMinute = minute;



                    }

                }, mHour, mMinute, true);
        tpd.show();

    }

    //TODO Key Atras
  /*  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // Guardar los datos
            // 1. Creamos el objeto Alarma
            CrearAlarma();

            if (modificando) {
                proveedor.modificarAlarma(alarma.getId(), alarma);
                idAlarma = alarma.getId();
            } else {
                idAlarma = proveedor.guardarAlarma(alarma);
            }

            alarma.setId((int) idAlarma);

            // Activar alarma

            // activarAlarma(alarma);

            //
            Intent data = new Intent();

            data.putExtra("alarmanueva", idAlarma);

            setResult(RESULT_OK, data);

            finish();
        }
        return true;
    }*/
}
