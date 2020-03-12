package cl.desafiolatam.pruebafinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class StorageActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 420;
    private ImageView image;
    private Button buttonChose, buttonUpload;
    private Uri filePath;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        initializeView();
        storageReference = FirebaseStorage.getInstance().getReference();

        buttonChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            showFileChooser();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirArchivo();
            }
        });
    }
    private void initializeView() {
        image = findViewById(R.id.imageView);
        buttonChose = findViewById(R.id.buttonChoose);
        buttonUpload = findViewById(R.id.buttonUpload);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Este método sube la imagen al servidor
    private void subirArchivo() {
//Verificamos si hay algun archivo para subir
        if (filePath != null) {
//Muestra un dialogo que indica un proceso ejecutandose
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Subiendo");
            progressDialog.show();
//Seleccionamos la carpeta images en Firebase Cloud Storage y asignamos el nombre del archivo que queremos almacenar
            StorageReference riversRef = storageReference.child("images/pic.jpg");
//Utilizamos nuestro path al archivo y lo pasamos por parámetros, luego implementamos
//los listener correspondientes que nos indica nuestra tarea
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//Si la subida es satisfactoria ocultamos el dialogo
                            progressDialog.dismiss();
//y mostramos un mensaje satisfactorio
                            Toast.makeText(getApplicationContext(), "Archivo subido Satisfactoriamente",
                                    Toast.LENGTH_LONG).show();
                            Log.d("EXAMPLE", taskSnapshot.getUploadSessionUri().toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
//Si la subida NO es satisfactoria ocultamos el dialogo
                            progressDialog.dismiss();
//y mostramos un mensaje de error
                            Toast.makeText(getApplicationContext(), exception.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//De esta forma estamos calculando el porcentaje aproximado
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) /
                                    taskSnapshot.getTotalByteCount();
//aca mostramos el resultado de nuestro calculo
                            progressDialog.setMessage("Subido: " + ((int) progress) + "%...");
                        }
                    });
        } else {
//No hay archivo seleccionado
            Toast.makeText(getApplicationContext(), "Archivo NO seleccionado ",
                    Toast.LENGTH_LONG).show();
        }
    }
}
