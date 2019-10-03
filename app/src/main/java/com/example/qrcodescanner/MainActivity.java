package com.example.qrcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodescanner.Model.Barang;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
//    private ZXingScannerView mScannerView;

    private Button btnScanner;
//    private TextView tvQRcode;
    private EditText etId, etTahun, etKontrak, etSerial, etNama, etStatus, etLokasi;
//    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnScanner = findViewById(R.id.button_scanner);
//        tvQRcode = findViewById(R.id.tv_qrcode_value);
        etId = findViewById(R.id.et_id);
        etTahun = findViewById(R.id.et_tahun);
        etKontrak = findViewById(R.id.et_kontrak);
        etNama = findViewById(R.id.et_nama);
        etSerial = findViewById(R.id.et_serial);
        etStatus = findViewById(R.id.et_status);
        etLokasi = findViewById(R.id.et_lokasi);
        btnScanner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.initiateScan();
            }
        });
//        textViewResult = findViewById(R.id.text_view_result);

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://10.0.2.2/netory/api/")
//                .addConverterFactory(GsonConverterFactory.create())
//
//                .build();
//        ApiInterface apiInterface = retrofit.create(com.example.qrcodescanner.Interface.ApiInterface.class);
//        Call<List<Barang>> call = apiInterface.jsonBarang();
//        call.enqueue(new Callback<List<Barang>>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onResponse(Call<List<Barang>> call, Response<List<Barang>> response) {
//                if (!response.isSuccessful()){
//                    textViewResult.setText("Code" + response.code());
//                    return;
//                }
//                List<Barang> get_barang = response.body();
//
//                for (Barang post : get_barang){
//                    String content = "";
//                    content += "ID: " + post.getId() + "\n";
//                    content += "Tahun Pengadaan: " + post.getTahun() + "\n";
//                    content += "No Kontrak: " + post.getNo_kontrak() + "\n";
//                    content += "Nama Barang: " + post.getNama_barang() + "\n";
//                    content += "Kategori: " + post.getKategori() + "\n";
//                    content += "Serial Number: " + post.getSerial_number() + "\n";
//                    content += "Status Barang: " + post.getStatus_barang() + "\n";
//                    content += "Lokasi Barang: " + post.getLokasi_barang() + "\n\n";
//
////                    content += "ID : " + post.getId() + "\n";
////                    content += "User ID: " + post.getUserId() + "\n";
////                    content += "Tittle: " + post.getTittle() + "\n";
////                    content += "Text: " + post.getText() + "\n\n";
//
//                    textViewResult.append(content);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Barang>> call, Throwable throwable) {
//                textViewResult.setText(throwable.getMessage());
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if (result.getContents()==null){
                Toast.makeText(this, "hasil tidak diketahui", Toast.LENGTH_SHORT).show();
            }else{
//                tvQRcode.setText(result.getContents());
                dataById(result.getContents());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void dataById(String id){
        String[] value= id.split(",");
        id = value[0];
        String url = "https://96221d84.ngrok.io/netory/api/get_barang_by_id?id="+id;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray getResult = jsonObject.getJSONArray("barang");

                    Random ran = new Random();
                    JSONObject mv = getResult.getJSONObject(ran.nextInt(getResult.length()));

                    Barang brg =new Barang();
//                    Toast.makeText(MainActivity.this, mv.getString("nama_barang"), Toast.LENGTH_SHORT).show();

                    setData(mv.getString("id"),
                            mv.getString("tahun"),
                            mv.getString("no_kontrak"),
                            mv.getString("serial_number"),
                            mv.getString("nama_barang"),
                            mv.getString("status_barang"),
                            mv.getString("lokasi_barang")
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
    void setData(String id, String tahun, String no_kontrak, String serial, String nama_barang, String status_barang, String lokasi_barang){

//        tvQRcode.setText(id);
        etId.setText(id);
        etTahun.setText(tahun);
        etKontrak.setText(no_kontrak);
        etSerial.setText(serial);
        etNama.setText(nama_barang);
        etStatus.setText(status_barang);
        etLokasi.setText(lokasi_barang);

    }

}