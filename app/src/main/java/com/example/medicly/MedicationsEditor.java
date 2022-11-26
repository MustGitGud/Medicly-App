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

public class MedicationsEditor extends AppCompatActivity {
    public static final String MEDICATIONS_ID_KEY = "medications_id";

    private static final String LOADING_MESSAGE = "Loading...";
    private static final String SAVING_MESSAGE = "Saving...";
    private static final String ERROR_MESSAGE = "An error has occurred.";

    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medications_editor);

        ImageButton bckBtn = findViewById(R.id.bckBtnMed);
        bckBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        EditText medicine = findViewById(R.id.medicine);
        EditText intake = findViewById(R.id.intake);

        final String id;
        if (!getIntent().hasExtra(MEDICATIONS_ID_KEY)) {
            id = UUID.randomUUID().toString();
        } else {
            id = getIntent().getStringExtra(MEDICATIONS_ID_KEY);

            showLoadingDialog();

            FirebaseFirestore.getInstance().collection(Medications.MEDICATIONS_COLLECTION)
                    .document(id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if (documentSnapshot.exists()) {
                                MedsData medsData = documentSnapshot.toObject(MedsData.class);
                                if (medsData != null) {
                                    medsData.setId(documentSnapshot.getId());

                                    medicine.setText(medsData.getMedicine());
                                    intake.setText(medsData.getIntake());
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
                MedsData medsData = new MedsData(
                        id,
                        medicine.getText().toString(),
                        intake.getText().toString(),
                        "auth" // TODO get current user id (patient id) from Firebase Auth (FirebaseAuth.getInstance().getCurrentUser().getUid())
                );
                save(medsData);
            }
        });
    }

    private void save(MedsData medsData) {
        FirebaseFirestore.getInstance()
                .collection(Medications.MEDICATIONS_COLLECTION)
                .document(medsData.getId())
                .set(medsData)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        finish();
                    }
                }).addOnFailureListener(MedicationsEditor.this, new OnFailureListener() {
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
        dialog = new AlertDialog.Builder(MedicationsEditor.this)
                .setMessage(SAVING_MESSAGE)
                .setCancelable(false)
                .show();
    }

    private void showLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(MedicationsEditor.this)
                .setMessage(LOADING_MESSAGE)
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String message) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(MedicationsEditor.this)
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
