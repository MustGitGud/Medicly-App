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

public class SchedEditor extends AppCompatActivity{
    public static final String SCHEDULE_ID_KEY = "schedule_id";

    private static final String LOADING_MESSAGE = "Loading...";
    private static final String SAVING_MESSAGE = "Saving...";
    private static final String ERROR_MESSAGE = "An error has occurred.";

    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_editor);

        ImageButton bckBtn = findViewById(R.id.bckBtnSched);
        bckBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        EditText place = findViewById(R.id.place);
        EditText date = findViewById(R.id.date);

        final String id;
        if (!getIntent().hasExtra(SCHEDULE_ID_KEY)) {
            id = UUID.randomUUID().toString();
        } else {
            id = getIntent().getStringExtra(SCHEDULE_ID_KEY);

            showLoadingDialog();

            FirebaseFirestore.getInstance().collection(Schedule.SCHEDULE_COLLECTION)
                    .document(id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if (documentSnapshot.exists()) {
                                SchedData schedData = documentSnapshot.toObject(SchedData.class);
                                if (schedData != null) {
                                    schedData.setId(documentSnapshot.getId());

                                    place.setText(schedData.getPlace());
                                    date.setText(schedData.getDate());
                                } else {
                                    showErrorDialog(null);
                                }
                            } else {
                                showErrorDialog(null);
                            }
                        }
                    });
        }
        findViewById(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavingDialog();
                SchedData schedData = new SchedData(
                        id,
                        place.getText().toString(),
                        date.getText().toString(),
                        "auth" // TODO get current user id (patient id) from Firebase Auth (FirebaseAuth.getInstance().getCurrentUser().getUid())
                );
                save(schedData);
            }
        });
    }

    private void save(SchedData schedData) {
        FirebaseFirestore.getInstance()
                .collection(Schedule.SCHEDULE_COLLECTION)
                .document(schedData.getId())
                .set(schedData)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        finish();
                    }
                }).addOnFailureListener(SchedEditor.this, new OnFailureListener() {
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
        dialog = new AlertDialog.Builder(SchedEditor.this)
                .setMessage(SAVING_MESSAGE)
                .setCancelable(false)
                .show();
    }

    private void showLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(SchedEditor.this)
                .setMessage(LOADING_MESSAGE)
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String message) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(SchedEditor.this)
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
