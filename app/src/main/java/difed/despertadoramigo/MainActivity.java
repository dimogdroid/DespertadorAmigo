package difed.despertadoramigo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;
import com.rey.material.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import difed.bd.ProveedorBd;
import difed.bd.ProveedorBdImpl;
import difed.entidades.Alarma;
import difed.util.ManagerAlarma;


public class MainActivity extends AppCompatActivity {

    /*
    Declarar instancias globales
     */
    AnimeAdapter animeAdapter;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    List<Alarma> lstAlarmas = new ArrayList<Alarma>();

    ProveedorBd proveedor;
    ManagerAlarma mng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActionBar supportActionBar = getSupportActionBar();

        // Inicializar Animes
        List<Alarma> items = new ArrayList<>();
        items.add(new Alarma(1,"PROBANDO"));
        items.add(new Alarma(2,"PROBANDO"));

        proveedor = ProveedorBdImpl.getProveedor(this);
        mng = new ManagerAlarma(proveedor,this);

        lstAlarmas = proveedor.getAlarmas();



        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recycler);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CrearNuevaAlarma();

            }
        });

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new AnimeAdapter(this, lstAlarmas);
        recycler.setAdapter(adapter);

    }

    private void CrearNuevaAlarma() {
        startActivityForResult(new Intent(this, NuevaAlarma.class),
                1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1) { // OK

            lstAlarmas.clear();
            lstAlarmas = proveedor.getAlarmas();

            for (Alarma alm: lstAlarmas){
                if (alm.isActiva()) {
                    mng.activarAlarma(alm);
                }

            }


            adapter = new AnimeAdapter(this, lstAlarmas);
            recycler.setAdapter(adapter);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                // do whatever
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
