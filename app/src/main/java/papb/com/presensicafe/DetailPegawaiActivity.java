package papb.com.presensicafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class DetailPegawaiActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private Button btnNonaktifkanPegawai;
    private Toolbar toolbar;
    private TextView txtKeterangan;

    private TextView txtNamaPegawaiDetail, txtTerdaftarSejak, txtDurasiJagaBulanIni, txtTotalDurasiJaga;


    private String pegawaiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pegawai);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pegawaiId = getPegawaiIdFromIntent();

        txtKeterangan = findViewById(R.id.txtKeterangan);
        btnNonaktifkanPegawai = findViewById(R.id.btnNonaktifkanPegawai);
        btnNonaktifkanPegawai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnNonaktifkanPegawai.getText().toString().equals("NONAKTIF")){
                    btnNonaktifkanPegawai.setText("AKTIF");
                    txtKeterangan.setText("Klik tombol di atas\nuntuk mengaktifkan pegawai");
                } else {
                    btnNonaktifkanPegawai.setText("NONAKTIF");
                    txtKeterangan.setText("Klik tombol di atas\nuntuk menonaktifkan pegawai");
                }
            }
        });

        txtNamaPegawaiDetail = findViewById(R.id.txtNamaPegawaiDetail);
        txtTerdaftarSejak = findViewById(R.id.txtTerdaftarSejak);
        txtDurasiJagaBulanIni = findViewById(R.id.txtDurasiJagaBulanIni);
        txtTotalDurasiJaga = findViewById(R.id.txtTotalDurasiJaga);

        setDetailPegawaiContent();
    }

    private String getPegawaiIdFromIntent(){
        Intent intent = getIntent();
        return intent.getStringExtra("pegawaiId");
    }

    private void setDetailPegawaiContent(){
        databaseReference.child("users").child(pegawaiId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                txtNamaPegawaiDetail.setText(user.getFullName());
                txtTerdaftarSejak.setText(user.getTanggalRegistrasi());
                if(user.getStatus().equals("aktif")){
                    btnNonaktifkanPegawai.setText("NONAKTIF");
                } else{
                    btnNonaktifkanPegawai.setText("AKTIF");
                }
                txtTotalDurasiJaga.setText(String.format("%02d jam %02d menit %02d detik",
                        TimeUnit.MILLISECONDS.toHours(user.getJamJaga()),
                        TimeUnit.MILLISECONDS.toMinutes(user.getJamJaga()),
                        TimeUnit.MILLISECONDS.toSeconds(user.getJamJaga()) -
                                TimeUnit.MINUTES.toHours(TimeUnit.MILLISECONDS.toMinutes(TimeUnit.MILLISECONDS.toSeconds(user.getJamJaga())))));
                txtDurasiJagaBulanIni.setText(String.format("%02d jam %02d menit %02d detik",
                        TimeUnit.MILLISECONDS.toHours(user.getJamJagaBulanIni()),
                        TimeUnit.MILLISECONDS.toMinutes(user.getJamJagaBulanIni()),
                        TimeUnit.MILLISECONDS.toSeconds(user.getJamJagaBulanIni()) -
                                TimeUnit.MINUTES.toHours(TimeUnit.MILLISECONDS.toMinutes(TimeUnit.MILLISECONDS.toSeconds(user.getJamJagaBulanIni())))));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
