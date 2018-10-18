package com.keepcalmandkanji.foureyes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class manageCards extends AppCompatActivity {

    DatabaseAccess databaseAccess;
    ArrayAdapter arrayAdapter;
    Spinner spFlashcards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cards);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        List tables = new ArrayList();
        tables = databaseAccess.getTables();


        spFlashcards = (Spinner) findViewById(R.id.spinner2);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner, tables);
        spFlashcards.setAdapter(arrayAdapter);



    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void deleteClick(View view) {
        String selectedTable = spFlashcards.getSelectedItem().toString();
        //Toast.makeText(this, selectedTable, Toast.LENGTH_SHORT).show();
        databaseAccess.deleteTable(selectedTable);
    }

}
