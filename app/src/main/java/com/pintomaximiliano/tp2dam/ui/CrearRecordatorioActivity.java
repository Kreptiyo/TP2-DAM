package com.pintomaximiliano.tp2dam.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.pintomaximiliano.tp2dam.R;
import com.pintomaximiliano.tp2dam.dao.RecordatorioDataSource;
import com.pintomaximiliano.tp2dam.dao.RecordatorioPreferencesDataSource;
import com.pintomaximiliano.tp2dam.dao.RecordatorioRepository;
import com.pintomaximiliano.tp2dam.dao.RecordatorioRetrofitDataSource;
import com.pintomaximiliano.tp2dam.dao.RecordatorioRoomDataSource;
import com.pintomaximiliano.tp2dam.dialog.DatePickerFragment;
import com.pintomaximiliano.tp2dam.dialog.TimePickerFragment;
import com.pintomaximiliano.tp2dam.model.RecordatorioModel;
import com.pintomaximiliano.tp2dam.receiver.RecordatorioReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CrearRecordatorioActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private EditText fecha;
    private EditText hora;
    private EditText descripcion;
    private Button btnGuardar;
    private RecordatorioRepository repository;
    private RecordatorioDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_recordatorio);

        this.canalNotificaciones();

        fecha = findViewById(R.id.editTextDate);
        hora = findViewById(R.id.editTextTime);
        descripcion = findViewById(R.id.editTextDescripcion);
        btnGuardar = findViewById(R.id.buttonGuardar);

        dataSource = null;
        String prefDataSource = PreferenceManager.getDefaultSharedPreferences(this).getString("datasource", "0");
        switch (prefDataSource){
            case "0": dataSource = new RecordatorioPreferencesDataSource(this); break;
            case "1": dataSource = new RecordatorioRoomDataSource(this); break;
            case "2": dataSource = new RecordatorioRetrofitDataSource(this); break;
        }
        repository = new RecordatorioRepository(dataSource);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_titulo_crear_recordatorio);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null)
        {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        fecha.setOnClickListener(view -> showDatePickerDialog());
        hora.setOnClickListener(view -> showDialogTimePicker());
        btnGuardar.setOnClickListener(view -> guardarRecordatorio());

    }

    private void showDatePickerDialog()
    {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "/" + (month+1) + "/" + year;
                fecha.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showDialogTimePicker()
    {
        TimePickerFragment fragmentoTimePicker = TimePickerFragment.newInstance((timePicker, hours, minutes) -> {
            final String selectedHour = ((hours > 9) ? Integer.toString(hours) : ("0" + hours)) + ":" + ((minutes > 9) ? Integer.toString(minutes) : ("0" + minutes));
            hora.setText(selectedHour);
        });
        fragmentoTimePicker.show(getSupportFragmentManager(), "timePicker");
    }

    private void canalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.nombre_canal);
            String description = getString(R.string.descripcion_canal);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(R.string.id_canal), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void guardarRecordatorio(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy kk:mm");
        TimeZone timeZoneArgentina = TimeZone.getTimeZone("America/Argentina/Buenos_Aires");
        formatter.setTimeZone(timeZoneArgentina);
        Date fechaFinal = null;
        Date fechaActual = null;
        String fechaSeleccionada = fecha.getText().toString() + " " + hora.getText().toString();


        try
        {
            fechaFinal = formatter.parse(fechaSeleccionada);
            fechaActual = formatter.parse(formatter.format(new Date()));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        if (fechaFinal == null || fechaFinal.before(fechaActual))
        {
            Toast.makeText(this, "Fecha no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        String descripcionRecordatorio = descripcion.getText().toString().trim();

        if (descripcionRecordatorio.isEmpty())
        {
            Toast.makeText(this, "Descripción no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("NOTIFICACIONES", true))
        {
            Intent intentAux = new Intent(this, RecordatorioReceiver.class);
            intentAux.setAction("RECORDATORIO");
            intentAux.putExtra("FECHA", formatter.format(fechaFinal));
            intentAux.putExtra("DESCRIPCION", descripcionRecordatorio);

            PendingIntent alarmIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 1, intentAux, PendingIntent.FLAG_UPDATE_CURRENT);

            final AlarmManager alarma = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            alarma.set(AlarmManager.RTC_WAKEUP, fechaFinal.getTime(), alarmIntent);
        }

        RecordatorioModel recordatorioTemp = new RecordatorioModel(descripcionRecordatorio, fechaFinal);
        repository.guardarRepositorio(recordatorioTemp, exito ->
        {
            if (exito)
                Toast.makeText(this, "Recordatorio registrado!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Error al guardar recordatorio", Toast.LENGTH_SHORT).show();
        });

        Intent menuPrincipalIntent = new Intent(CrearRecordatorioActivity.this, MainActivity.class);
        setResult(RESULT_OK, menuPrincipalIntent);
        finish();
    }

}
