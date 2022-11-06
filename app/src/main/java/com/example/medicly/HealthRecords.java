package com.example.medicly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class HealthRecords extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_records);

        Spinner mySpinner = (Spinner) findViewById(R.id.sexSpinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(HealthRecords.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.sex));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        ImageButton bckBtn = findViewById(R.id.backBtnHR);
        bckBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(i1);
            }
        });

        Button btnA = findViewById(R.id.btnAllergy);
        btnA.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), Allergies.class);
                startActivity(i1);
            }
        });

        Button btnB = findViewById(R.id.btnCon);
        btnB.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), Diagnosis.class);
                startActivity(i1);
            }
        });

        Button btnC = findViewById(R.id.btnMeds);
        btnC.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), Medications.class);
                startActivity(i1);
            }
        });

        Button btnD = findViewById(R.id.btnDoc);
        btnD.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), DocProf.class);
                startActivity(i1);
            }
        });

    }
}