package com.keepcalmandkanji.foureyes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public List getTables() {
        Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        c.moveToFirst();
        List tables = new ArrayList();
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tables.add(c.getString(0));
                c.moveToNext();
            }
        }
        tables.remove("android_metadata");
        tables.remove("sample");
        Collections.sort(tables, String.CASE_INSENSITIVE_ORDER);
        return tables;
    }

    public String[] getColumns(String TABLE_NAME) {
        Cursor c = database.rawQuery("SELECT * FROM ["+TABLE_NAME+"]", null);
        String[] columns = c.getColumnNames();
        return columns;
    }

    public List getItems(String TABLE_NAME, String COLUMN_NAME) {
        Cursor c = database.rawQuery("SELECT * FROM ["+TABLE_NAME+"]",null);
        int columnIndex = c.getColumnIndex(COLUMN_NAME);
        c.moveToFirst();
        List items = new ArrayList();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                items.add(c.getString(columnIndex));
                c.moveToNext();
            }
        }
        return items;
    }

    public Integer getSize(String TABLE_NAME) {
        int counter = 0;
        Cursor c = database.rawQuery("SELECT * FROM ["+TABLE_NAME+"]", null);;
        c.moveToFirst();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                counter = counter + 1;
                Log.i("BLAH",Integer.toString(c.getPosition()));

                c.moveToNext();
            }
        }
        return counter;
    }

    public int[] getPositionNumbers(String TABLE_NAME, Boolean random) {
        Cursor c = database.rawQuery("SELECT * FROM ["+TABLE_NAME+"]", null);;
        List positionNumbers = new ArrayList();
        c.moveToFirst();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                positionNumbers.add(c.getPosition());
                c.moveToNext();
            }
        }
        if (random) {
            Collections.shuffle(positionNumbers);
        }
        int[] returnArray = new int[positionNumbers.size()];
        Iterator<Integer> iterator = positionNumbers.iterator();
        for (int i=0; i < returnArray.length; i++){
            returnArray[i] = iterator.next().intValue();
        }
        return returnArray;
    }

    public String getItemAtPosition(String TABLE_NAME, String COLUMN_NAME, Integer POS){
        Cursor c = database.rawQuery("SELECT * FROM ["+TABLE_NAME+"]", null);
        int columnIndex = c.getColumnIndex(COLUMN_NAME);
        c.moveToPosition(POS);
        String item = c.getString(columnIndex);
        c.close();
        return item;
    }

    public void createNewTable(String TABLE_NAME, String NEW_COLUMNS){
        Log.i("TEST","TEST 1");
        database.execSQL("CREATE TABLE IF NOT EXISTS ["+TABLE_NAME+"]("+NEW_COLUMNS+")");
    }

    public void addEntries(String TABLE_NAME, String ENTRIES){
        Log.i("TEST","TEST 2");
        database.execSQL("INSERT INTO ["+ TABLE_NAME + "] VALUES("+ENTRIES+");");
    }

    public void deleteTable(String TABLE_NAME) {
        database.execSQL("DROP TABLE IF EXISTS ["+TABLE_NAME+"]");
    }

}
