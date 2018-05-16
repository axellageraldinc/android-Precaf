package papb.com.presensicafe;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PegawaiHomeActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_LOCATION = 0;
    private static final String TAG = PegawaiHomeActivity.class.getSimpleName();
    private static final double TOLERANSI_JARAK_DALAM_KILOMETER = 0.1;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private GoogleApiClient googleApiClient;

    private TextView txtTotalDurasiJaga, txtTerhitungMulaiDari;
    private Button btnJaga;
    private Toolbar toolbar;

    private DateTime today = new DateTime();

    private double latitudeCafeTeti=0;
    private double longitudeCafeTeti=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pegawai_home);

        connectGoogleApiClient();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getLatitudeCafeTeti();
        getLongitudeCafeTeti();

        btnJaga = findViewById(R.id.buttonjaga);
        btnJaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnJaga.getText().toString().equals("JAGA")) {
                    catatWaktuMulai();
                } else {
                    btnJaga.setText("JAGA");
                    catatWaktuSelesai();
                }
            }
        });

        txtTotalDurasiJaga = findViewById(R.id.txtTotalDurasiJaga);
        getTotalDurasiJaga();
        txtTerhitungMulaiDari = findViewById(R.id.txtTerhitungMulaiDari);
        getTerhitungMulaiDari();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth.getCurrentUser()==null){
            Intent loginActivity = new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginActivity);
            finish();
        }
    }

    private void getTerhitungMulaiDari(){
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("tanggalRegistrasi").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String tanggalRegistrasi = dataSnapshot.getValue(String.class);
                txtTerhitungMulaiDari.setText("Terhitung mulai dari " + tanggalRegistrasi);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLatitudeCafeTeti(){
        databaseReference.child("latitudeCafeTeti").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latitudeCafeTeti = dataSnapshot.getValue(double.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getLongitudeCafeTeti(){
        databaseReference.child("longitudeCafeTeti").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                longitudeCafeTeti = dataSnapshot.getValue(double.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTotalDurasiJaga(){
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("jamJaga").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long jamJaga = dataSnapshot.getValue(Long.class);
                txtTotalDurasiJaga.setText(String.format("%02d jam %02d menit %02d detik",
                        TimeUnit.MILLISECONDS.toHours(jamJaga),
                        TimeUnit.MILLISECONDS.toMinutes(jamJaga),
                        TimeUnit.MILLISECONDS.toSeconds(jamJaga) -
                                TimeUnit.MINUTES.toHours(TimeUnit.MILLISECONDS.toMinutes(TimeUnit.MILLISECONDS.toSeconds(jamJaga)))
                ));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void connectGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        googleApiClient.connect();
    }

    private void catatWaktuMulai(){if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
                PegawaiHomeActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSION_LOCATION
        );
    }
        Awareness.SnapshotApi.getLocation(googleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get location.");
                            return;
                        }
                        Location location = locationResult.getLocation();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.i(TAG, latitude + "," + longitude);
                        double distanceToTetiCafe = CalculateDistance.calculate(latitude, longitude, latitudeCafeTeti, longitudeCafeTeti);
                        Log.i(TAG, "Jarak ke cafe teti : " + distanceToTetiCafe*1000 + " meter");
                        if(distanceToTetiCafe < TOLERANSI_JARAK_DALAM_KILOMETER){
                            TimeKeeper.timeStart=new Date().getTime();
                            btnJaga.setText("SELESAI JAGA");
                        } else{
                            Toast.makeText(PegawaiHomeActivity.this, "Anda sedang tidak di TETI Cafe!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void catatWaktuSelesai(){
        TimeKeeper.timeStop=new Date().getTime();
        final long durasiJagaDalamMillisecond = TimeKeeper.timeStop - TimeKeeper.timeStart;
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                long jamJaga = user.getJamJaga();
                long jamJagaBaru = jamJaga + durasiJagaDalamMillisecond;
                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("jamJaga").setValue(jamJagaBaru);
                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("jamJagaBulanIni").setValue(jamJagaBaru);
                if(today.getMonthOfYear()>user.getBulanJagaTerakhir()){
                    databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("bulanJagaTerakhir").setValue(today.getMonthOfYear());
                    databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("jamJagaBulanIni").setValue(durasiJagaDalamMillisecond);
                }
                getTotalDurasiJaga();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                firebaseAuth.signOut();
                Intent loginActivity = new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginActivity);
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
