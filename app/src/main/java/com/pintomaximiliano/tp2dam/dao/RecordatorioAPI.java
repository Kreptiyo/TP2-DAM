package com.pintomaximiliano.tp2dam.dao;

import com.pintomaximiliano.tp2dam.model.RecordatorioModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;

public interface RecordatorioAPI {

    @GET("recordatorio")
    Call<List<RecordatorioModel>> obtenerRecordatorios();

    @POST("recordatorio")
    Call<RecordatorioModel> guardarRecordatorio(@Body RecordatorioModel recordatorio);

    @HTTP(method = "DELETE", path = "recordatorio", hasBody = true)
    Call<RecordatorioModel> borrarRecordatorio(@Body RecordatorioModel reco);

}
