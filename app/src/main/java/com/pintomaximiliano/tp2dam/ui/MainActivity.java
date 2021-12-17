package com.pintomaximiliano.tp2dam.ui;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pintomaximiliano.tp2dam.R;
import com.pintomaximiliano.tp2dam.adapter.RecordatorioAdapter;
import com.pintomaximiliano.tp2dam.dao.RecordatorioDataSource;
import com.pintomaximiliano.tp2dam.dao.RecordatorioPreferencesDataSource;
import com.pintomaximiliano.tp2dam.dao.RecordatorioRepository;
import com.pintomaximiliano.tp2dam.dao.RecordatorioRetrofitDataSource;
import com.pintomaximiliano.tp2dam.dao.RecordatorioRoomDataSource;
import com.pintomaximiliano.tp2dam.model.RecordatorioModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigationView;
    private RecyclerView recordatoriosRecyclerView;
    private RecyclerView.LayoutManager recordatoriosLayoutManager;
    private RecordatorioRepository repository;
    private RecordatorioDataSource dataSource;
    private RecordatorioRoomDataSource roomDataSource;
    private RecordatorioDataSource apiDataSource;
    private RecordatorioPreferencesDataSource prefDataSource;
    private RecordatorioAdapter recordatoriosAdapter;
    private ProgressBar barraCarga;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.what) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "Recordatorios borrados.", Toast.LENGTH_SHORT).show();
                        barraCarga.setVisibility(View.GONE);
                        break;
                    case 1:
                        recordatoriosRecyclerView.setVisibility(View.VISIBLE);
                        barraCarga.setVisibility(View.GONE);
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Recordatorio #" + inputMessage.arg1 + " borrado", Toast.LENGTH_SHORT).show();
                        repository.recuperarRecordatorios((exito1, recos) -> {
                            recordatoriosAdapter = new RecordatorioAdapter(recos);
                            recordatoriosRecyclerView.setAdapter(recordatoriosAdapter);
                            handler.sendEmptyMessage(1);
                        });
                        break;
                    case 3:
                        recargarDatosAdapter((List<RecordatorioModel>) inputMessage.obj);
                        cargarListenerAdapter();
                        break;
                    case 4:
                        recargarDatosAdapter((List<RecordatorioModel>) inputMessage.obj);
                        drawerLayout.closeDrawer(drawerNavigationView);
                        break;
                    case 5:
                        recargarDatosAdapter((List<RecordatorioModel>) inputMessage.obj);
                        break;
                }
            }
        };

        recordatoriosRecyclerView = findViewById(R.id.recordatoriosRecycler);
        recordatoriosRecyclerView.setHasFixedSize(true);
        recordatoriosLayoutManager = new LinearLayoutManager(this);
        recordatoriosRecyclerView.setLayoutManager(recordatoriosLayoutManager);

        initDataSources();
        actualizarDataSource();

        Thread thread = new Thread(() -> {
            try  {
                Message mensaje = new Message();
                mensaje.what = 3;
                repository.recuperarRecordatorios((exito, recordatorios) -> {
                    mensaje.obj = recordatorios;
                    handler.sendMessage(mensaje);
                    handler.sendEmptyMessage(1);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_titulo_listarecordatorios);
        setSupportActionBar(toolbar);
        barraCarga = findViewById(R.id.MR_indicador_carga);
        drawerLayout = findViewById(R.id.MR_drawer_layout);
        drawerNavigationView = findViewById(R.id.MR_navigation_view);
        drawerNavigationView.setNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId())
            {
                case R.id.menu_crear_recordatorio:
                    drawerLayout.closeDrawer(drawerNavigationView);
                    Intent i1 = new Intent(MainActivity.this, CrearRecordatorioActivity.class);
                    startActivityForResult(i1, 20);
                    break;
                case R.id.menu_configuracion:
                    drawerLayout.closeDrawer(drawerNavigationView);
                    Intent i2 = new Intent(MainActivity.this, ConfiguracionActivity.class);
                    startActivityForResult(i2, 20);
                    break;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_configuration_opt) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 10:
                if (resultCode == Activity.RESULT_OK){
                    recordatoriosRecyclerView.setVisibility(View.GONE);
                    barraCarga.setVisibility(View.VISIBLE);
                    Message mensaje = new Message();
                    mensaje.what = 5;
                    Thread t = new Thread(() -> {
                        try  {
                            Thread.sleep(1000);
                            repository.recuperarRecordatorios((exito, recordatorios) -> {
                                mensaje.obj = recordatorios;
                                handler.sendMessage(mensaje);
                                handler.sendEmptyMessage(1);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    t.start();

                }
                break;
            case 20:
                if (resultCode == Activity.RESULT_OK){
                    actualizarDataSource();
                    repository.recuperarRecordatorios((exito, recordatorios) -> {
                        recargarDatosAdapter(recordatorios);
                    });
                }
                break;
        }
    }

    private void initDataSources(){
        roomDataSource = new RecordatorioRoomDataSource(this);
        prefDataSource = new RecordatorioPreferencesDataSource(this);
        apiDataSource = new RecordatorioRetrofitDataSource(this);
    }

    private void actualizarDataSource(){
        String tipoDataSource = PreferenceManager.getDefaultSharedPreferences(this).getString("datasource", "0");
        switch (tipoDataSource){
            case "0": dataSource = prefDataSource; break;
            case "1": dataSource = roomDataSource; break;
            case "2": dataSource = apiDataSource; break;
        }
        repository = new RecordatorioRepository(dataSource);
    }

    private void recargarDatosAdapter(List<RecordatorioModel> recordatorios){
        recordatoriosAdapter = new RecordatorioAdapter(recordatorios);
        recordatoriosRecyclerView.setAdapter(recordatoriosAdapter);
    }

    public void cargarListenerAdapter(){
        recordatoriosAdapter.setOnItemClickListener((itemView, position) -> {
            recordatoriosRecyclerView.setVisibility(View.GONE);
            barraCarga.setVisibility(View.VISIBLE);
            int idRecordatorioTemp = recordatoriosAdapter.getRecordatorioId(position);
            Thread t = new Thread(() -> {
                try  {
                    Message mensaje = new Message();
                    mensaje.what = 2;
                    repository.borrarRecordatorio(idRecordatorioTemp, exito -> {
                        mensaje.arg1 = idRecordatorioTemp;
                        handler.sendMessage(mensaje);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t.start();
        });
    }

}