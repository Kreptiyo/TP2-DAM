package com.pintomaximiliano.tp2dam.dao;

import android.content.Context;

import com.pintomaximiliano.tp2dam.db.BuilderDB;
import com.pintomaximiliano.tp2dam.model.RecordatorioModel;

import java.util.ArrayList;
import java.util.Collections;

public class RecordatorioRoomDataSource implements RecordatorioDataSource{

    RecordatorioRoomDao recordatorioDao;

    public RecordatorioRoomDataSource(Context context){
        recordatorioDao = BuilderDB.getInstancia().getRecordatorioRoomDao(context);
    }

    @Override
    public void guardarRecordatorio(RecordatorioModel recordatorio, GuardarRecordatorioCallback callback) {
        recordatorioDao.insertarRecordatorio(recordatorio);
        callback.resultado(true);
    }

    @Override
    public void recuperarRecordatorios(RecuperarRecordatorioCallback callback) {
        RecordatorioModel[] recordatorios = recordatorioDao.cargarRecordatorios();
        ArrayList<RecordatorioModel> recordatoriosList = new ArrayList<>();
        Collections.addAll(recordatoriosList, recordatorios);
        callback.resultado(true, recordatoriosList);
    }

    @Override
    public void borrarRecordatorios(BorrarRecordatoriosCallback callback) {
        recordatorioDao.borrarRecordatorios();
        callback.resultado(true);
    }

    @Override
    public void borrarRecordatorio(int idRecordatorio, BorrarRecordatorioCallback callback) {
        RecordatorioModel reco = recordatorioDao.obtenerRecordatorio(idRecordatorio);
        recordatorioDao.borrarRecordatorio(reco);
        callback.resultado(true);
    }
}
