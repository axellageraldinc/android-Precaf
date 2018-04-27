package papb.com.presensicafe;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListPegawaiActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private FloatingActionButton btnAddPegawai;
    private Dialog dialog;
    private EditText txtFullName, txtEmail, txtPassword;
    private Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pegawai);

        firebaseAuth = firebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnAddPegawai = findViewById(R.id.btnAddPegawai);

        dialog = new Dialog(ListPegawaiActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_new_pegawai);
        txtFullName = dialog.findViewById(R.id.tambahNama);
        txtEmail = dialog.findViewById(R.id.tambahEmail);
        txtPassword = dialog.findViewById(R.id.tambahPassword);
        btnDaftar = dialog.findViewById(R.id.buttonTambah);

        btnAddPegawai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerPegawaiBaru(txtEmail.getText().toString(), txtPassword.getText().toString(), txtFullName.getText().toString());
                dialog.dismiss();
            }
        });
    }

    private void registerPegawaiBaru(final String email, String password, final String fullName){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User();
                    user.setId(task.getResult().getUser().getUid());
                    user.setFullName(fullName);
                    user.setEmail(email);
                    user.setRole("pegawai");
                    user.setJamJaga(0);
                    user.setStatus("aktif");
                    databaseReference.child("users").child(task.getResult().getUser().getUid()).setValue(user);
                    txtEmail.setText("");
                    txtPassword.setText("");
                } else{
                    Toast.makeText(ListPegawaiActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
