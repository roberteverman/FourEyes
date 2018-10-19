package com.keepcalmandkanji.foureyes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class selectCards extends AppCompatActivity {

    public Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String selectedTable = getIntent().getStringExtra("selectedTable");
        String selectedFront = getIntent().getStringExtra("selectedFront");
        String selectedBack = getIntent().getStringExtra("selectedBack");
        String selectedTop = getIntent().getStringExtra("selectedTop");
        String selectedBottom = getIntent().getStringExtra("selectedBottom");
        int[] positionNumbers = getIntent().getIntArrayExtra("positionNumbers");
        adapter = new Adapter(this, selectedTable, selectedFront, selectedBack, selectedTop, selectedBottom, positionNumbers);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cards);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button startButton = (Button) findViewById(R.id.button6);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] newPositionNumbers = adapter.clickStart();

                //debugging
                for (int i = 0; i < newPositionNumbers.length; i ++){
                    //Log.i("BLAH","NEW POSITIONS: " + Integer.toString(newPositionNumbers[i]));
                }

                if(newPositionNumbers.length == 0) {
                    Toast.makeText(selectCards.this, "SELECT AT LEAST ONE", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), flashCards.class);
                    intent.putExtra("selectedTable", selectedTable);
                    intent.putExtra("selectedFront", selectedFront);
                    intent.putExtra("selectedBack", selectedBack);
                    intent.putExtra("selectedTop", selectedTop);
                    intent.putExtra("selectedBottom", selectedBottom);
                    intent.putExtra("positionNumbers", newPositionNumbers);
                    startActivity(intent);
                }
            }
        });

        Button allButton = (Button) findViewById(R.id.button4);
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clickAll();
                adapter.notifyDataSetChanged();
            }
        });

        Button noneButton = (Button) findViewById(R.id.button5);
        noneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clickNone();
                adapter.notifyDataSetChanged();
            }
        });

    }

}
