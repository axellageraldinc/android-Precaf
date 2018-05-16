package papb.com.presensicafe;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private TextView txtNamePalingBanyak, txtNamePalingSedikit;
    private CardView btnManajemenPegawai;
    private Button btnJaga;
    private TextView txtStatusJaga;
    private boolean statusJaga = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Log.i(AdminHomeActivity.class.getSimpleName(), "In On Create");

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtNamePalingBanyak = findViewById(R.id.txtNamePalingBanyak);
        getPegawaiPalingBanyakDurasiJamJaga();
        txtNamePalingSedikit = findViewById(R.id.txtNamePalingSedikit);
        getPegawaiPalingSedikitDurasiJamJaga();

        btnManajemenPegawai=(CardView) findViewById(R.id.cardViewManajemenPegawai);
        btnManajemenPegawai.setOnClickListener(this);

        txtStatusJaga=(TextView)findViewById(R.id.textView4);
        btnJaga=(Button)findViewById(R.id.btnAdminJaga);
        btnJaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statusJaga){
                    btnJaga.setText("BERHENTI\nJAGA");
                    txtStatusJaga.setText("Klik tombol di atas\njika berhenti jaga");
                    statusJaga=true;
                } else{
                    btnJaga.setText("JAGA");
                    txtStatusJaga.setText("Klik tombol di atas\n jika mulai jaga");
                    statusJaga=false;
                }

            }
        });
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

    public void onClick(View v){
        switch (v.getId()){
            case R.id.cardViewManajemenPegawai:
                Intent manajemenPegawaiIntent=new Intent(AdminHomeActivity.this, ListPegawaiActivity.class);
                startActivity(manajemenPegawaiIntent);
                break;
        }
    }
    private void getPegawaiPalingBanyakDurasiJamJaga(){
        DatabaseReference databasePegawai = databaseReference.child("users");
        Query findDurasiJamJagaPalingBanyak = databasePegawai.orderByChild("jamJagaBulanIni").limitToLast(1);
        findDurasiJamJagaPalingBanyak.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    txtNamePalingBanyak.setText(user.getFullName());
                }
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
                break;
            default:
                break;
        }
        return true;
    }

    private void getPegawaiPalingSedikitDurasiJamJaga(){
        DatabaseReference databasePegawai = databaseReference.child("users");
        Query findDurasiJamJagaPalingSedikit = databasePegawai.orderByChild("jamJagaBulanIni").limitToFirst(1);
        findDurasiJamJagaPalingSedikit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    txtNamePalingSedikit.setText(user.getFullName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
