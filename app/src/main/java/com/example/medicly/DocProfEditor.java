package com.example.medicly;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DocProfEditor extends AppCompatActivity {
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile_editor);

        img = findViewById(R.id.imgDocProf);
        name_et = findViewById(R.id.edtDocNm);
        phoneNumber_et = findViewById(R.id.edtDocPn);
        years_et = findViewById(R.id.edtDocYrs);
        save = findViewById(R.id.saveBtn);

        documentReference = db.collection("user").document("profile");
        storageReference = firebaseStorage.getInstance().getReference("profile images");

        ImageButton bckBtn = findViewById(R.id.bckBtnDoc);
        bckBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), DocProf.class);
                startActivity(i1);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateProfile();
                Toast.makeText(DocProfEditor.this, "Saved", Toast.LENGTH_SHORT).show();
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

    private void UpdateProfile(){
        String name = name_et.getText().toString();
        String phoneNumber = phoneNumber_et.getText().toString();
        String years = years_et.getText().toString();

        if(imageUri != null){
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

                                final DocumentReference sfDocRef = db.collection("user").document("profile");
                                db.runTransaction(new Transaction.Function<Void>() {
                                            @Override
                                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                                                transaction.update(sfDocRef,"name",name);
                                                transaction.update(sfDocRef,"phoneNumber",phoneNumber);
                                                transaction.update(sfDocRef,"years",years);
                                                transaction.update(sfDocRef,"url",downloadUri.toString());

                                                return null;
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

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
            final DocumentReference sfDocRef = db.collection("user").document("profile");
            db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(sfDocRef);

                            transaction.update(sfDocRef, "name", name);
                            transaction.update(sfDocRef, "phoneNumber", phoneNumber);
                            transaction.update(sfDocRef, "years", years);

                            return null;
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
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
                            Toast.makeText(DocProfEditor.this, "No Profile Exists", Toast.LENGTH_SHORT).show();
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
