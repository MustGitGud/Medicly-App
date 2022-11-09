package com.example.medicly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.firebase.database.Query;

public class Allergies extends AppCompatActivity {
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allergies);
        container = findViewById(R.id.linLayAllergy);

        ImageButton bckBtn = findViewById(R.id.bckBtnAllergy);
        bckBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i1 = new Intent(getApplicationContext(), HealthRecords.class);
                startActivity(i1);
            }
        });

        ImageButton addBtn = findViewById(R.id.addBtnAllergy);
        addBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                addContainer();
            }
        });
    }

    public void addContainer() {
        EditText editText = new EditText(this);
        container.addView(editText);
        editText.setGravity(Gravity.LEFT);
        editText.setMinHeight(300);
        editText.setBackgroundResource(R.drawable.edit_txt_bg);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editText.getLayoutParams();
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.bottomMargin = 20;
        editText.setLayoutParams(layoutParams);
    }

}