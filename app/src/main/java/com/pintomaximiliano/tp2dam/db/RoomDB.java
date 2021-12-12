package com.pintomaximiliano.tp2dam.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.pintomaximiliano.tp2dam.dao.RecordatorioRoomDao;
import com.pintomaximiliano.tp2dam.model.RecordatorioModel;


@Database(entities = {RecordatorioModel.class}, version = 1)
@TypeConverters(ConvertidoresDB.class)
public abstract class RoomDB extends RoomDatabase {
    public abstract RecordatorioRoomDao recordatorioRoomDao();
}
