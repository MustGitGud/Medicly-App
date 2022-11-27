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

public class Schedule extends AppCompatActivity {
    public static final String SCHEDULE_COLLECTION = "schedule";

    private SchedListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_scheduling);

        ImageButton bckBtn = findViewById(R.id.backBtnSched);
        bckBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), HealthRecords.class);
                startActivity(i1);
            }
        });

        ImageButton addBtn = findViewById(R.id.addSchedBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Schedule.this, SchedEditor.class));
            }
        });

        adapter = new SchedListAdapter();
        RecyclerView schedList = findViewById(R.id.schedList);

        schedList.addItemDecoration(new DividerItemDecoration(Schedule.this, DividerItemDecoration.VERTICAL));
        schedList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance()
                .collection(SCHEDULE_COLLECTION)
                .whereEqualTo("owner", "auth") // TODO get current user id (patient id) from Firebase Auth (FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null || value == null || adapter == null) return;

                        ArrayList<SchedData> output = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : value) {
                            SchedData data = snapshot.toObject(SchedData.class);
                            data.setId(snapshot.getId());
                            output.add(data);
                        }
                        adapter.setData(output);
                    }
                });
    }
}