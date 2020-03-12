package cl.desafiolatam.pruebafinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Main2Activity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextAddress;
    private Button btnSave, btnGetUsers,btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initializeView();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUser();
            }
        });

        btnGetUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsers();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                irAStorage();
            }
        });
    }


    private void initializeView(){
        db = FirebaseFirestore.getInstance();
        editTextName = findViewById(R.id.et_name);
        editTextLastName = findViewById(R.id.et_last_name);
        editTextAddress = findViewById(R.id.et_address);
        btnSave = findViewById(R.id.btn_save);
        btnGetUsers = findViewById(R.id.btn_get_users);
        btn = findViewById(R.id.button_file_storage);



    }

    private boolean validateEmptyFields(String name, String lastName, String address) {
        if (name.isEmpty()) {
            editTextName.setError("Nombre requerido");
            editTextName.requestFocus();
            return true;
        }
        if (lastName.isEmpty()) {
            editTextLastName.setError("Apellido requerido");
            editTextLastName.requestFocus();
            return true;
        }
        if (address.isEmpty()) {
            editTextAddress.setError("Direccion requerida");
            editTextAddress.requestFocus();
            return true;
        }
        return false;
    }

    private void saveUser() {
        String name = editTextName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        if (!validateEmptyFields(name, lastName, address)) {
            CollectionReference dbUsers = db.collection("usuarios");
            User user = new User(name, lastName, address);
            dbUsers.add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(Main2Activity.this, "User Agregado", Toast.LENGTH_LONG).show();
                            editTextName.setText("");
                            editTextLastName.setText("");
                            editTextAddress.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Main2Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void getUsers() {
        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d("Main2Activity", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("Main2Activity", "Error obteniendo documentos: ", task.getException());
                        }
                    }
                });
    }

    private void irAStorage() {
        Intent intent = new Intent(this, StorageActivity.class);
        startActivity(intent);
    }

}
