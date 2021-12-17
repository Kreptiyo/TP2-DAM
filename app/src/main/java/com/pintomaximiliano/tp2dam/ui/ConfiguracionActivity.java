package com.pintomaximiliano.tp2dam.ui;

import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.pintomaximiliano.tp2dam.R;

public class ConfiguracionActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialButton borrarDatosBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Configuración");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            Intent i1 = new Intent(ConfiguracionActivity.this, MainActivity.class);
            setResult(RESULT_OK, i1);
            finish();
        });

        getFragmentManager().beginTransaction().replace(R.id.fragment_configuracion_container, new ConfiguracionPreferencesFragment()).commit();
    }
}
