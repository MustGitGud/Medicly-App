package com.example.medicly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class Allergies extends AppCompatActivity {

    // reference to allergies collection in Firestore
    public static final String ALLERGIES_COLLECTION = "allergies";

    private AllergiesListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allergies);

        ImageButton bckBtn = findViewById(R.id.bckBtnAllergy);
        bckBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i1 = new Intent(getApplicationContext(), HealthRecords.class);
                startActivity(i1);
            }
        });

        ImageButton addBtn = findViewById(R.id.addBtnAllergy);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Allergies.this, AllergiesEditor.class));
            }
        });

        adapter = new AllergiesListAdapter();
        RecyclerView allergiesList = findViewById(R.id.allergiesList);

        // add a line to separate each row
        allergiesList.addItemDecoration(new DividerItemDecoration(Allergies.this, DividerItemDecoration.VERTICAL));

        allergiesList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get real-time updates in RecyclerView
        // reference: https://firebase.google.com/docs/firestore/query-data/listen
        //
        // *This is added in onStart() because the event listener for real-time
        // updates is automatically removed in onStop() to avoid NullPointerExceptions
        FirebaseFirestore.getInstance()
                .collection(ALLERGIES_COLLECTION)
                .whereEqualTo("owner", "auth") // TODO get current user id (patient id) from Firebase Auth (FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        // ensure that there are no problems with displaying allergy data
                        if (error != null || value == null || adapter == null) return;

                        ArrayList<Allergy> output = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : value) {
                            // convert raw data to allergy (Allergy.java)
                            Allergy data = snapshot.toObject(Allergy.class);

                            // Manually add the id in the newly created allergy (Allergy.java) instance
                            // since the id is not stored as a field (e.g. title, description, owner)
                            //
                            // Instead, the id is taken from the document name
                            data.setId(snapshot.getId());

                            output.add(data);
                        }
                        adapter.setData(output);
                    }
                });
    }
}