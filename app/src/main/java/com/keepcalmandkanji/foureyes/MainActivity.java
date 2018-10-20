package com.keepcalmandkanji.foureyes;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner spFlashcards;
    Spinner spFront;
    Spinner spBack;
    Spinner spTop;
    Spinner spBottom;
    CheckBox ckRandomize;
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapterFront;
    ArrayAdapter<String> arrayAdapterBack;
    ArrayAdapter<String> arrayAdapterTop;
    ArrayAdapter<String> arrayAdapterBottom;
    DatabaseAccess databaseAccess;
    int[] positionNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //animation of cog wheel
        ImageView cog = (ImageView) findViewById(R.id.cog);
        RotateAnimation rotate = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(8000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        cog.startAnimation(rotate);

        //database initializer
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List tables = new ArrayList();
        tables = databaseAccess.getTables();

        spFlashcards = (Spinner) findViewById(R.id.spinner);
        spFront = (Spinner) findViewById(R.id.front_spinner);
        spBack = (Spinner) findViewById(R.id.back_spinner);
        spTop = (Spinner) findViewById(R.id.top_spinner);
        spBottom = (Spinner) findViewById(R.id.bottom_spinner);
        ckRandomize = (CheckBox) findViewById(R.id.randomize);

        //set initial values of tables spinner
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner, tables);
        spFlashcards.setAdapter(arrayAdapter);

        spFlashcards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTable = spFlashcards.getSelectedItem().toString();

                //Log.i("CHECK", spFlashcards.getSelectedItem().toString());

                String[] columns = databaseAccess.getColumns(selectedTable);

                for (int i = 0; i < columns.length; i++) {
                    //Log.i("CHECK",columns[i]);
                }

                //Log.i("CHECK","WORKED!");

                arrayAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.spinner,columns);
                spFront.setAdapter(arrayAdapter);
                spBack.setAdapter(arrayAdapter);
                spTop.setAdapter(arrayAdapter);
                spBottom.setAdapter(arrayAdapter);

                int numColumns = spFront.getAdapter().getCount();
                Log.i("BLAH","COUNT IS " + Integer.toString(numColumns));

                if (numColumns >= 4) {
                    spFront.setSelection(0);
                    spBack.setSelection(1);
                    spTop.setSelection(2);
                    spBottom.setSelection(3);
                } else if (numColumns == 3){
                    spFront.setSelection(0);
                    spBack.setSelection(1);
                    spTop.setSelection(2);
                    spBottom.setSelection(0);
                } else if (numColumns == 2) {
                    spFront.setSelection(0);
                    spBack.setSelection(1);
                    spTop.setSelection(0);
                    spBottom.setSelection(1);
                } else  {
                    spFront.setSelection(0);
                    spBack.setSelection(0);
                    spTop.setSelection(0);
                    spBottom.setSelection(0);
                }

                Boolean random = ckRandomize.isChecked();
                positionNumbers = databaseAccess.getPositionNumbers(selectedTable,random);
                //Log.i("BLAH",Integer.toString(positionNumbers[0]));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ckRandomize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String selectedTable = spFlashcards.getSelectedItem().toString();
                String selectedFront = spFront.getSelectedItem().toString();
                Boolean random = ckRandomize.isChecked();
                positionNumbers = databaseAccess.getPositionNumbers(selectedTable,random);
                //Log.i("BLAH",Integer.toString(positionNumbers[0]));
                //Log.i("BLAH3",databaseAccess.getItemAtPosition(selectedTable,selectedFront,positionNumbers[0]));
            }
        });

    }

    public void flashCardClick(View view) {

        String selectedTable = spFlashcards.getSelectedItem().toString();
        String selectedFront = spFront.getSelectedItem().toString();
        String selectedBack = spBack.getSelectedItem().toString();
        String selectedTop = spTop.getSelectedItem().toString();
        String selectedBottom = spBottom.getSelectedItem().toString();

        Intent intent = new Intent(getApplicationContext(), flashCards.class);
        intent.putExtra("selectedTable",selectedTable);
        intent.putExtra("selectedFront",selectedFront);
        intent.putExtra("selectedBack",selectedBack);
        intent.putExtra("selectedTop",selectedTop);
        intent.putExtra("selectedBottom",selectedBottom);
        intent.putExtra("positionNumbers",positionNumbers);
        startActivity(intent);
    }

    public void fileExplorerClick(View view) {
        Intent intent = new Intent(getApplicationContext(), fileExplorer.class);
        Explode explode = new Explode();
        explode.setDuration(300);
        Slide slide = new Slide();
        slide.setDuration(300);
        Fade fade = new Fade();
        fade.setDuration(400);
        slide.setSlideEdge(Gravity.END);
        getWindow().setEnterTransition(fade);
        getWindow().setReenterTransition(fade);
        getWindow().setExitTransition(slide);
        startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

    }

    public void manageClick(View view) {
        Intent intent = new Intent(getApplicationContext(),manageCards.class);
        startActivity(intent);

    }

    public void selectClick(View view) {

        String selectedTable = spFlashcards.getSelectedItem().toString();
        String selectedFront = spFront.getSelectedItem().toString();
        String selectedBack = spBack.getSelectedItem().toString();
        String selectedTop = spTop.getSelectedItem().toString();
        String selectedBottom = spBottom.getSelectedItem().toString();

        Intent intent = new Intent(getApplicationContext(), selectCards.class);
        intent.putExtra("selectedTable",selectedTable);
        intent.putExtra("selectedFront",selectedFront);
        intent.putExtra("selectedBack",selectedBack);
        intent.putExtra("selectedTop",selectedTop);
        intent.putExtra("selectedBottom",selectedBottom);
        intent.putExtra("positionNumbers",positionNumbers);
        startActivity(intent);

    }

}
