package com.keepcalmandkanji.foureyes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keepcalmandkanji.foureyes.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = Adapter.class.getSimpleName();

    Integer ITEM_COUNT;
    private List<Item> items;
    DatabaseAccess databaseAccess;

    public Adapter(Context context, String selectedTable, String selectedFront, String selectedBack, String selectedTop, String selectedBottom, int[] positionNumbers) {
        super();
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        ITEM_COUNT = positionNumbers.length;

        // Create some items
        items = new ArrayList<>();
        for (int i = 0; i < ITEM_COUNT; ++i) {
            items.add(new Item(Integer.toString(positionNumbers[i]),
                    "Front: " + databaseAccess.getItemAtPosition(selectedTable,selectedFront,i)
                    + "\nBack: " + databaseAccess.getItemAtPosition(selectedTable,selectedBack,i)
                    + "\nTop: " + databaseAccess.getItemAtPosition(selectedTable,selectedTop,i)
                    + "\nBottom: " + databaseAccess.getItemAtPosition(selectedTable,selectedBottom,i)
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }
}