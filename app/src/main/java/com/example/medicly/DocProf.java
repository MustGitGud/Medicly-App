package com.example.medicly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DocProf extends AppCompatActivity {

    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;
    Button edit;
    TextView name_tv, pNumber_tv, yrs_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile);

        imageView = findViewById(R.id.imgDocProf);
        name_tv = findViewById(R.id.inpName);
        pNumber_tv = findViewById(R.id.inpPNumber);
        yrs_tv = findViewById(R.id.inpYrs);

        documentReference = db.collection("user").document("profile");
        storageReference = firebaseStorage.getInstance().getReference("profile images");

        ImageButton bckBtn = findViewById(R.id.bckBtnDoc);
        bckBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), HealthRecords.class);
                startActivity(i1);
            }
        });


        edit = findViewById(R.id.edtBtn);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(getApplicationContext(), DocProfEditor.class);
                startActivity(i2);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String name_result = task.getResult().getString("name");
                            String pNumber_result = task.getResult().getString("phoneNumber");
                            String years_result = task.getResult().getString("years");
                            String Url = task.getResult().getString("url");

                            Picasso.get().load(Url).into(imageView);
                            name_tv.setText(name_result);
                            pNumber_tv.setText(pNumber_result);
                            yrs_tv.setText(years_result);
                        }else{
                            Toast.makeText(DocProf.this, "No Profile Exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}