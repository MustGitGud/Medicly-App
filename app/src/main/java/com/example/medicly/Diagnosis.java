package com.example.medicly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Diagnosis extends AppCompatActivity {

    public static final String DIAGNOSIS_COLLECTION = "diagnosis";

    private DiagnosisListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnosis);

        ImageButton bckBtn = findViewById(R.id.bckBtnCond);
        bckBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), HealthRecords.class);
                startActivity(i1);
            }
        });

        Button addBtn = findViewById(R.id.addBtnCond);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DiagnosisEditor.class));
            }
        });

        adapter = new DiagnosisListAdapter();
        RecyclerView condList = findViewById(R.id.condList);

        condList.addItemDecoration(new DividerItemDecoration(Diagnosis.this, DividerItemDecoration.VERTICAL));

        condList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance()
                .collection(DIAGNOSIS_COLLECTION)
                .whereEqualTo("owner", "auth") // TODO get current user id (patient id) from Firebase Auth (FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null || value == null || adapter == null) return;

                        ArrayList<DiagnosisData> output = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : value) {
                            DiagnosisData data = snapshot.toObject(DiagnosisData.class);

                            data.setId(snapshot.getId());

                            output.add(data);
                        }
                        adapter.setData(output);
                    }
                });
    }
}