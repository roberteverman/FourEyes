package com.keepcalmandkanji.foureyes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

public class fileExplorer extends AppCompatActivity {

    Button button;
    TextView textView;
    TextView csvViewer;
    EditText editText;
    List<String[]> allData;
    Button saveButton;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        csvViewer = (TextView) findViewById(R.id.csvViewer);
        editText = (EditText) findViewById(R.id.TextInput);
        saveButton = (Button) findViewById(R.id.saveButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withActivity(fileExplorer.this)
                        .withRequestCode(1000)
                        .withFilter(Pattern.compile(".*\\.csv$")) // Filtering files and directories by file name using regexp
                        .withFilterDirectories(false) // Set directories filterable (false by default)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file
            textView.setText(filePath);

            allData = importCSV(filePath);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1001:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission granted!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"Permission denied!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public List<String[]> importCSV(String file) {

        try {
            FileReader fileReader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(fileReader).build();
            List<String[]> allData = csvReader.readAll();

            return allData;
        }
        catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();

        }


    }

    public void setCsvViewer(int size, Dictionary d) {

        String csvText = new String();

        csvText = csvText + "LOADED \n \n";

        for(int i = 1; i < size + 1; i++){
            csvText = csvText + "Column " + Integer.toString(i) + ":";
            csvText = csvText + "\n \n";
        }

        csvViewer.setText(csvText);

    }

    public void saveClick(View view) {

        String tableName = editText.getText().toString();
        if (tableName.matches("")) {
            Toast.makeText(this, "Enter a name for your flashcards.", Toast.LENGTH_SHORT).show();
        } else {

            String[] firstRow = allData.get(0);
            String newColumns = new String();

            for (int i = 0; i < firstRow.length; i++) {
                if (i == 0) {
                    newColumns = "[" + firstRow[i] + "]" + " VARCHAR";
                } else {
                    newColumns = newColumns + "," + "[" + firstRow[i] + "]" + " VARCHAR";
                }
            }
            Log.i("BLAH", newColumns);
            databaseAccess.createNewTable(tableName,newColumns);

            for (int i = 1; i < allData.size(); i++) {
                String newRow = new String();
                for (int j = 0; j < allData.get(i).length; j++) {
                    if (j == allData.get(i).length - 1) {
                        newRow = newRow + "'" + allData.get(i)[j].replace("'", "`") + "'";
                    } else {
                        newRow = newRow + "'" + allData.get(i)[j].replace("'", "`") + "',";
                    }
                }
                Log.i("BLAH", newRow);
                databaseAccess.addEntries(tableName,newRow);
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

}
