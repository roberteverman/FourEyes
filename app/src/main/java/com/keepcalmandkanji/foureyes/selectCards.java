package com.keepcalmandkanji.foureyes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class selectCards extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String selectedTable = getIntent().getStringExtra("selectedTable");
        String selectedFront = getIntent().getStringExtra("selectedFront");
        String selectedBack = getIntent().getStringExtra("selectedBack");
        String selectedTop = getIntent().getStringExtra("selectedTop");
        String selectedBottom = getIntent().getStringExtra("selectedBottom");
        int[] positionNumbers = getIntent().getIntArrayExtra("positionNumbers");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cards);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new Adapter(this, selectedTable, selectedFront, selectedBack, selectedTop, selectedBottom, positionNumbers));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


}
