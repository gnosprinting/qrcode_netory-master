package com.kominfo.netory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
    private EditText etId, etTahun, etKontrak, etSerial, etNama, etLokasi;
    private Spinner spinner;
    private ProgressBar pb;
    private String BASE_URL = "http://95cd86c3.ngrok.io/";
    private String statusValue = "-";

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
        etLokasi = findViewById(R.id.et_lokasi);
        spinner = findViewById(R.id.et_status);

        btnUpdate.setVisibility(View.INVISIBLE);

        action();
    }

    private void action() {
        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.initiateScan();
                failed();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etTahun.length() == 0 || etKontrak.length() == 0 ||
                        etSerial.length() == 0 || etNama.length() == 0 ||
                        etLokasi.length() == 0) {
                    Toast.makeText(MainActivity.this, "Silahkan lalukan Scan Terlebih dahulu..", Toast.LENGTH_SHORT).show();
                } else {
                    spinner.setEnabled(false);
                    etLokasi.setEnabled(false);
                    String res = String.format("%snetory/api/update_barang?id=%s&tahun=%s&no_kontrak=%s&nama_barang=%s&serial_number=%s&status_barang=%s&lokasi_barang=%s",
                            BASE_URL,
                            etId.getText().toString().trim(),
                            etTahun.getText().toString().trim(),
                            etKontrak.getText().toString().trim(),
                            etNama.getText().toString().trim(),
                            etSerial.getText().toString().trim(),
                            statusValue,
                            etLokasi.getText().toString().trim());

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get(res, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Toast.makeText(MainActivity.this, "Berhasil Mengubah data", Toast.LENGTH_SHORT).show();
                            cleanIt();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(MainActivity.this, "gagal mengubah data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        statusValue = "Digunakan";
                        break;
                    case 1:
                        statusValue = "Habis";
                        break;
                    case 2:
                        statusValue = "Rusak";
                        break;
                    case 3:
                        statusValue = "Hilang";
                        break;
                    default:
                        statusValue = "-";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                statusValue = "-";
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "hasil tidak diketahui", Toast.LENGTH_SHORT).show();
                failed();
            } else {
                dataById(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void dataById(String id) {
        String[] value = id.split(",");
        id = value[0];
        Toast.makeText(this, "Memuat data..", Toast.LENGTH_SHORT).show();
        String url = String.format("%snetory/api/get_barang_by_id?id=%s",
                BASE_URL,
                id);
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
        spinner.setEnabled(true);
        btnUpdate.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
    }

    @SuppressLint("ResourceAsColor")
    void failed() {
        etLokasi.setEnabled(false);
        spinner.setEnabled(false);
        btnUpdate.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);
    }

    void setData(String id, String tahun, String no_kontrak, String serial, String nama_barang, String status_barang, String lokasi_barang) {
        sukses();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        etId.setText(id);
        etTahun.setText(tahun);
        etKontrak.setText(no_kontrak);
        etSerial.setText(serial);
        etNama.setText(nama_barang);
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
        etLokasi.setText("");
        etKontrak.setText("");
        etNama.setText("");
        etSerial.setText("");
        etId.setText("");
        pb.setVisibility(View.GONE);
    }
}