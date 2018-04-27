package papb.com.presensicafe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class PegawaiHomeActivity extends AppCompatActivity {

    private FirebaseAuth fIrebaseAuth;
    private DatabaseReference databaseReference;

    private Button btnJaga;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pegawai_home);

        fIrebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        btnJaga=findViewById(R.id.buttonjaga);
        btnJaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnJaga.getText().toString().equals("JAGA")){
                    btnJaga.setText("SELESAI JAGA");
                    catatWaktuMulai();
                }else{
                    btnJaga.setText("JAGA");
                    catatWaktuSelesai();
                    final long durasiJagaDalamMillisecond = TimeKeeper.timeStop-TimeKeeper.timeStart;
                    databaseReference.child("users").child(fIrebaseAuth.getCurrentUser().getUid()).child("jamJaga").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long jamJaga = dataSnapshot.getValue(Long.class);
                            long jamJagaBaru = jamJaga+durasiJagaDalamMillisecond;
                            databaseReference.child("users").child(fIrebaseAuth.getCurrentUser().getUid()).child("jamJaga").setValue(jamJagaBaru);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    private void catatWaktuMulai(){
        TimeKeeper.timeStart=new Date().getTime();

    }
    private void catatWaktuSelesai(){
        TimeKeeper.timeStop=new Date().getTime();
    }
}
