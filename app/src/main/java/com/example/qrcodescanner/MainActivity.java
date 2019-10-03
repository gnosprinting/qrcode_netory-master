package com.example.qrcodescanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private Button btnScanner, btnUpdate;
    private EditText etId, etTahun, etKontrak, etSerial, etNama, etStatus, etLokasi;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        btnScanner = findViewById(R.id.button_scanner);
        pb = findViewById(R.id.progress_circular);
        btnUpdate = findViewById(R.id.button_update);
        etId = findViewById(R.id.et_id);
        etTahun = findViewById(R.id.et_tahun);
        etKontrak = findViewById(R.id.et_kontrak);
        etNama = findViewById(R.id.et_nama);
        etSerial = findViewById(R.id.et_serial);
        etStatus = findViewById(R.id.et_status);
        etLokasi = findViewById(R.id.et_lokasi);

        action();
    }

    private void action() {
        btnScanner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.initiateScan();
                failed();
                cleanIt();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etTahun.length() == 0 || etKontrak.length() == 0 ||
                        etSerial.length() == 0 || etNama.length() == 0 ||
                        etStatus.length() == 0 || etLokasi.length() == 0) {
                    Toast.makeText(MainActivity.this, "Silahkan lalukan Scan Terlebih dahulu..", Toast.LENGTH_SHORT).show();
                } else {
                    //::Todo Proses Update
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if (result.getContents()==null){
                Toast.makeText(this, "hasil tidak diketahui", Toast.LENGTH_SHORT).show();
                failed();
            }else{
                dataById(result.getContents());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void dataById(String id){
        String[] value= id.split(",");
        id = value[0];
        String url = "http://0f28a20b.ngrok.io/netory/api/get_barang_by_id?id=" + id;
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

    @SuppressLint("ResourceAsColor")
    void sukses() {
        etLokasi.setEnabled(true);
        etStatus.setEnabled(true);
        pb.setVisibility(View.GONE);
    }

    @SuppressLint("ResourceAsColor")
    void failed() {
        etLokasi.setEnabled(false);
        etStatus.setEnabled(false);
        pb.setVisibility(View.VISIBLE);
    }

    void setData(String id, String tahun, String no_kontrak, String serial, String nama_barang, String status_barang, String lokasi_barang){
        sukses();
        etId.setText(id);
        etTahun.setText(tahun);
        etKontrak.setText(no_kontrak);
        etSerial.setText(serial);
        etNama.setText(nama_barang);
        etStatus.setText(status_barang);
        etLokasi.setText(lokasi_barang);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reload) {
            cleanIt();
        }
        return super.onOptionsItemSelected(item);
    }

    private void cleanIt() {
        etTahun.setText("");
        etStatus.setText("");
        etLokasi.setText("");
        etKontrak.setText("");
        etNama.setText("");
        etSerial.setText("");
        etId.setText("");
        pb.setVisibility(View.GONE);
    }
}