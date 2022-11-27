package com.example.medicly;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DocProfCreator extends AppCompatActivity {

    public static final String TAG = "TAG";
    private Uri imageUri;
    private static final int PICK_IMAGE = 1;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    EditText name_et, phoneNumber_et, years_et;
    ImageView img;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile_creator);

        img = findViewById(R.id.imgDocProf);
        name_et = findViewById(R.id.inputDocNm);
        phoneNumber_et = findViewById(R.id.inputDocPn);
        years_et = findViewById(R.id.inputDocYrs);
        save = findViewById(R.id.saveBtn);

        ImageButton bckBtn = findViewById(R.id.bckBtnDoc);
        bckBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), DocProf.class);
                startActivity(i1);
            }
        });

        documentReference = db.collection("user").document("profile");
        storageReference = firebaseStorage.getInstance().getReference("profile images");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadData();
            }
        });

    }

    public void ChooseImage(View view){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null){
            imageUri = data.getData();

            Picasso.get().load(imageUri).into(img);
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadData(){
        String name = name_et.getText().toString();
        String phoneNumber = phoneNumber_et.getText().toString();
        String years = years_et.getText().toString();

        if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(phoneNumber) || !TextUtils.isEmpty(years) || imageUri != null){
             final StorageReference reference = storageReference.child(System.currentTimeMillis()+" "+getFileExt(imageUri));

             uploadTask = reference.putFile(imageUri);

             Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                 @Override
                 public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                     if(!task.isSuccessful()){
                         throw task.getException();
                     }
                     return reference.getDownloadUrl();
                 }
             })
                     .addOnCompleteListener(new OnCompleteListener<Uri>() {
                         @Override
                         public void onComplete(@NonNull Task<Uri> task) {
                             if(task.isSuccessful()){
                                 Uri downloadUri = task.getResult();
                                 HashMap<String,String> profile = new HashMap<>();
                                 profile.put("name", name);
                                 profile.put("phoneNumber", phoneNumber);
                                 profile.put("years", years);
                                 profile.put("url", downloadUri.toString());

                                 documentReference.set(profile)
                                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void unused) {
                                                Toast.makeText(DocProfCreator.this, "Profile Created", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(DocProfCreator.this, DocProf.class);
                                                startActivity(intent);
                                             }
                                         })
                                         .addOnFailureListener(new OnFailureListener() {
                                             @Override
                                             public void onFailure(@NonNull Exception e) {
                                                 Toast.makeText(DocProfCreator.this, "Failed", Toast.LENGTH_SHORT).show();
                                             }
                                         });

                                 }
                             }
                         })
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {

                         }
                     });
        }else{
            Toast.makeText(this, "All Fields Required", Toast.LENGTH_SHORT).show();
        }
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

                            Picasso.get().load(Url).into(img);
                            name_et.setText(name_result);
                            phoneNumber_et.setText(pNumber_result);
                            years_et.setText(years_result);
                        }else{
                            Toast.makeText(DocProfCreator.this, "No Profile Exists", Toast.LENGTH_SHORT).show();
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
