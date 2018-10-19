package com.keepcalmandkanji.foureyes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = Adapter.class.getSimpleName();

    Integer ITEM_COUNT;
    private List<Item> items;
    DatabaseAccess databaseAccess;
    SparseBooleanArray itemStateArray = new SparseBooleanArray();
    int[] selectedPositions;
    String pSelectedTable, pSelectedFront, pSelectedBack, pSelectedTop, pSelectedBottom;


    public Adapter(Context context, String selectedTable, String selectedFront, String selectedBack, String selectedTop, String selectedBottom, int[] positionNumbers) {
        super();
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        ITEM_COUNT = positionNumbers.length;
        selectedPositions = positionNumbers;
        pSelectedTable = selectedTable;
        pSelectedFront = selectedFront;
        pSelectedBack = selectedBack;
        pSelectedTop = selectedTop;
        pSelectedBottom = selectedBottom;

        // Create some items
        items = new ArrayList<>();
        for (int i = 0; i < ITEM_COUNT; ++i) {
            itemStateArray.put(i,true);
            items.add(new Item(Integer.toString(positionNumbers[i]),
                    "Front: " + databaseAccess.getItemAtPosition(selectedTable,selectedFront,i)
                    + "\nBack: " + databaseAccess.getItemAtPosition(selectedTable,selectedBack,i)
                    + "\nTop: " + databaseAccess.getItemAtPosition(selectedTable,selectedTop,i)
                    + "\nBottom: " + databaseAccess.getItemAtPosition(selectedTable,selectedBottom,i),getChecked(i)
            ));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());
        holder.aSwitch.setChecked(getChecked(position));

    }

    public boolean getChecked(Integer i) {
        return itemStateArray.get(i);
    }

    public int[] clickStart() {
        List newPositions = new ArrayList();
        Log.i("TEST","Start Clicked!!");
        for (int i = 0; i < ITEM_COUNT; ++i) {
            if(itemStateArray.get(i)) {
                newPositions.add(selectedPositions[i]);
            }
        }

        if (newPositions.size() == 0){
            int[] newPositionNumbers = new int[0];
            return newPositionNumbers;
        } else {

            int[] newPositionNumbers = new int[newPositions.size()];
            for (int i = 0; i < newPositions.size(); ++i) {
                newPositionNumbers[i] = Integer.parseInt(newPositions.get(i).toString());

            }
            return newPositionNumbers;
        }
    }

    public void clickAll() {
        Log.i("TEST","All Clicked!");
        for (int i = 0; i < ITEM_COUNT; ++i) {
            if(!itemStateArray.get(i)) {
                itemStateArray.put(i,true);
                //Log.i("TEST","ITEM NUMBER" + Integer.toString(i) +"IS CLICKED");
            }
        }
    }

    public void clickNone() {
        Log.i("TEST","None Clicked!");
        for (int i = 0; i < ITEM_COUNT; ++i) {
            if(itemStateArray.get(i)) {
                itemStateArray.put(i,false);
                //Log.i("TEST","ITEM NUMBER" + Integer.toString(i) +"IS CLICKED");
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        Switch aSwitch;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            aSwitch = (Switch) itemView.findViewById(R.id.switch1);

            aSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Boolean checked = aSwitch.isChecked();
                    if (checked) {
                        itemStateArray.put(position,true);

                    } else {
                        itemStateArray.put(position,false);
                    }
                }
            });

        }
    }
}