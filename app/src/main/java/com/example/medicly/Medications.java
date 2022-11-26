package com.example.medicly;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Medications extends AppCompatActivity {

    public static final String MEDICATIONS_COLLECTION = "medications";

    private MedsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medications);

        ImageButton bckBtn = findViewById(R.id.bckBtnMed);
        bckBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), HealthRecords.class);
                startActivity(i1);
            }
        });

        Button addBtn = findViewById(R.id.addBtnMed);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Medications.this, MedicationsEditor.class));
            }
        });

        adapter = new MedsListAdapter();
        RecyclerView medsList = findViewById(R.id.medsList);

        medsList.addItemDecoration(new DividerItemDecoration(Medications.this, DividerItemDecoration.VERTICAL));
        medsList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance()
                .collection(MEDICATIONS_COLLECTION)
                .whereEqualTo("owner", "auth") // TODO get current user id (patient id) from Firebase Auth (FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null || value == null || adapter == null) return;

                        ArrayList<MedsData> output = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : value) {
                            MedsData data = snapshot.toObject(MedsData.class);
                            data.setId(snapshot.getId());
                            output.add(data);
                        }
                        adapter.setData(output);
                    }
                });
    }
}