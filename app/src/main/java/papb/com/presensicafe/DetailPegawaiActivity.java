package papb.com.presensicafe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class DetailPegawaiActivity extends AppCompatActivity {

    private Button btnNonaktifkanPegawai;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pegawai);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnNonaktifkanPegawai = findViewById(R.id.btnNonaktifkanPegawai);
        btnNonaktifkanPegawai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnNonaktifkanPegawai.getText().toString().equals("AKTIF")){
                } else {
                    btnNonaktifkanPegawai.setText("NONAKTIF");
                }
            }
        });

    }
}
