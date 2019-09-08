package com.ayusma.textrecognition.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RecognizedTextDB";
    private static final String TABLE_NAME = "SavedText";
    private static final String KEY_ID = "id";
    private static final String KEY_TEXT = "text";
    private static final String[] COLUMNS = {KEY_ID, KEY_TEXT};

    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ID +" INTEGER primary key , "
                + KEY_TEXT +" TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);

    }

    public void delete(Saver saver) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(saver.getId())});
        db.close();
    }

    public Saver getSavedText(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Saver saver = new Saver();
        saver.setId(Integer.parseInt(Objects.requireNonNull(cursor).getString(0)));
        saver.setText(cursor.getString(1));


        return saver;
    }

    public List<Saver> getAllSavedText() {

        List<Saver> saver = new LinkedList<Saver>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Saver saved = null;

        if (cursor.moveToFirst()) {
            do {
                saved = new Saver();
                saved.setId(Integer.parseInt(cursor.getString(0)));
                saved.setText(cursor.getString(1));
                saver.add(saved);
            } while (cursor.moveToNext());
        }

        return saver;
    }

    public void addText(String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TEXT, text);
        // insert
        db.insert(TABLE_NAME, null, values);
        db.close();

    }

}
