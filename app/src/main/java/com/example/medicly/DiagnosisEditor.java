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

public class DiagnosisEditor extends AppCompatActivity {

    public static final String DIAGNOSIS_ID_KEY = "diagnosis_id";

    private static final String LOADING_MESSAGE = "Loading...";
    private static final String SAVING_MESSAGE = "Saving...";
    private static final String ERROR_MESSAGE = "An error has occurred.";

    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnosis_editor);

        ImageButton bckBtn = findViewById(R.id.bckBtnCond);
        bckBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        EditText title = findViewById(R.id.diagTitle);
        EditText description = findViewById(R.id.diagDescription);
        EditText date = findViewById(R.id.diagDate);

        final String id;
        if (!getIntent().hasExtra(DIAGNOSIS_ID_KEY)) {
            id = UUID.randomUUID().toString();
        } else {
            id = getIntent().getStringExtra(DIAGNOSIS_ID_KEY);

            showLoadingDialog();

            FirebaseFirestore.getInstance().collection(Diagnosis.DIAGNOSIS_COLLECTION)
                    .document(id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if (documentSnapshot.exists()) {
                                DiagnosisData diagnosisData = documentSnapshot.toObject(DiagnosisData.class);
                                if (diagnosisData != null) {
                                    diagnosisData.setId(documentSnapshot.getId());

                                    title.setText(diagnosisData.getTitle());
                                    description.setText(diagnosisData.getDescription());
                                    date.setText(diagnosisData.getDate());
                                } else {
                                    showErrorDialog(null);
                                }
                            } else {
                                showErrorDialog(null);
                            }
                        }
                    });
        }

        findViewById(R.id.diagSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavingDialog();
                DiagnosisData diagnosisData = new DiagnosisData(
                        id,
                        title.getText().toString(),
                        description.getText().toString(),
                        date.getText().toString(),
                        "auth"
                );
                save(diagnosisData);
            }
        });
    }

    private void save(DiagnosisData diagnosisData) {
        FirebaseFirestore.getInstance()
                .collection(Diagnosis.DIAGNOSIS_COLLECTION)
                .document(diagnosisData.getId())
                .set(diagnosisData)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        finish();
                    }
                }).addOnFailureListener(DiagnosisEditor.this, new OnFailureListener() {
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
        dialog = new AlertDialog.Builder(DiagnosisEditor.this)
                .setMessage(SAVING_MESSAGE)
                .setCancelable(false)
                .show();
    }

    private void showLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(DiagnosisEditor.this)
                .setMessage(LOADING_MESSAGE)
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String message) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(DiagnosisEditor.this)
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
