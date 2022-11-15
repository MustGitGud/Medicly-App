package com.example.medicly;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class AllergiesEditor extends AppCompatActivity {

    public static final String ALLERGY_ID_KEY = "allergy_id";

    private static final String LOADING_MESSAGE = "Loading...";
    private static final String SAVING_MESSAGE = "Saving...";
    private static final String ERROR_MESSAGE = "An error has occurred.";

    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allergies_editor);

        ImageButton bckBtn = findViewById(R.id.bckBtnAllergy);
        bckBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        EditText title = findViewById(R.id.title);
        EditText description = findViewById(R.id.description);

        final String id;
        if (!getIntent().hasExtra(ALLERGY_ID_KEY)) {
            // id does not exist, therefore a new id must be generated
            id = UUID.randomUUID().toString();
        } else {
            /*
             * Retrieve data passed from previous activity
             * reference: https://developer.android.com/reference/android/content/Intent#getStringExtra(java.lang.String)
             */
            id = getIntent().getStringExtra(ALLERGY_ID_KEY);

            showLoadingDialog();

            // id exists, therefore associated data must be retrieved from Firestore so that it can be edited
            /*
             * Read data.
             * reference: https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
             */
            FirebaseFirestore.getInstance().collection(Allergies.ALLERGIES_COLLECTION)
                    .document(id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if (documentSnapshot.exists()) {
                                // convert raw data to allergy (Allergy.java)
                                Allergy allergy = documentSnapshot.toObject(Allergy.class);
                                if (allergy != null) {
                                    // Manually add the id in the newly created allergy (Allergy.java) instance
                                    // since the id is not stored as a field (e.g. title, description, owner)
                                    //
                                    // Instead, the id is taken from the document name
                                    allergy.setId(documentSnapshot.getId());

                                    title.setText(allergy.getTitle());
                                    description.setText(allergy.getDescription());
                                } else {
                                    showErrorDialog(null);
                                }
                            } else {
                                showErrorDialog(null);
                            }
                        }
                    });
        }

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavingDialog();
                Allergy allergy = new Allergy(
                        id,
                        title.getText().toString(),
                        description.getText().toString(),
                        "auth" // TODO get current user id (patient id) from Firebase Auth (FirebaseAuth.getInstance().getCurrentUser().getUid())
                );
                save(allergy);
            }
        });
    }

    private void save(Allergy allergy) {
        // Create or overwrite a document.
        // reference: https://firebase.google.com/docs/firestore/manage-data/add-data#set_a_document
        FirebaseFirestore.getInstance()
                .collection(Allergies.ALLERGIES_COLLECTION)
                .document(allergy.getId())
                .set(allergy)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        finish();
                    }
                }).addOnFailureListener(AllergiesEditor.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showErrorDialog(e.getMessage() == null ? ERROR_MESSAGE : e.getMessage());
                    }
                });
    }

    private void showSavingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(AllergiesEditor.this)
                .setMessage(SAVING_MESSAGE)
                .setCancelable(false)
                .show();
    }

    private void showLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(AllergiesEditor.this)
                .setMessage(LOADING_MESSAGE)
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String message) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(AllergiesEditor.this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
