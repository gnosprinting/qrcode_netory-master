package com.example.qrcodescanner.Interface;

import com.example.qrcodescanner.Model.Barang;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    //String URL_BARANG = "http://10.20.18.226/netory/api/";

    @GET("get_barang_by_id?id=14")
    Call<List<Barang>> jsonBarang();
}
