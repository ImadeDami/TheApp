package com.theapp.zeathon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    public static final String TAG ="DBHelper";
    public static final String TABLE_NAME ="employee";
    public static final String COL1 ="id";
    public static final String COL2 ="FNAMEE";
    public static final String COL3 ="SNAMEE";
    public static final String COL4 ="GENDD";
    public static final String COL5 ="MARSTT";
    public static final String COL6 ="PHONENUM";
    public static final String SYNC_STATUS = "syncstatus";

    public DBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " FNAMEE TEXT, SNAMEE TEXT, GENDD TEXT, MARSTT TEXT, PHONENUM TEXT, syncstatus integer)";
       /** String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 +
                " VARCHAR, " + COL3 + " VARCHAR, " + COL4 + " VARCHAR, " + COL5 + " VARCHAR, " + SYNC_STATUS +
                " integer);";**/
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }


    public boolean addData(String fName, String sName, String marSt, String gndr, String phNo, int sync_status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, fName);
        contentValues.put(COL3, sName);
        contentValues.put(COL4, marSt);
        contentValues.put(COL5, gndr);
        contentValues.put(COL6, phNo);
        contentValues.put(SYNC_STATUS, sync_status);

        Log.d(TAG, "add data: adding " + fName + ", " + sName + ", " + marSt + ", " + gndr + ", " + phNo + ", " + sync_status + "  to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if data is inserted correctly it will return -1
        if(result == -1){
            return false;
        } else {
            return  true;
        }
    }

    /**return all data from database**/

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        /*if(data.getCount()>0){ //new down
            if(data.moveToFirst()){
                do {

                }while (data.moveToNext());
            }
        }*/
        return data;
    }

   /* public Cursor readFromLocalDatabase(SQLiteDatabase db) {

    }*/

   public void updateLocalDatabase(String fName, String sName, String marSt, String gndr, String phNo, int sync_status){
       SQLiteDatabase db = this.getWritableDatabase();
       ContentValues contentValues = new ContentValues();
       contentValues.put(SYNC_STATUS, sync_status);
       String selection = COL2+" LIKE ?";
       String[] selection_args = {fName, sName, marSt, gndr, phNo};
       db.update(TABLE_NAME, contentValues, selection, selection_args);

   }

    public boolean updateNameStatus(int id, int sync_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SYNC_STATUS, sync_status);
        db.update(TABLE_NAME, contentValues, COL1 + "=" + id, null);
        db.close();
        return true;
    }
    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + SYNC_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /**
     46
     * Get list of Users from SQLite DB as Array List
     47
     * @return
    48
     */

    public ArrayList<HashMap<String, String>> getAllUsers() {
        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM users";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("COL1", cursor.getString(0));
                map.put("COL2", cursor.getString(1));
                map.put("COL3", cursor.getString(2));
                map.put("COL4", cursor.getString(3));
                map.put("COL5", cursor.getString(4));
                map.put("COL6", cursor.getString(5));
                usersList.add(map);
            } while (cursor.moveToNext());
        } //fName, String sName, String marSt, String gndr,
        database.close();
        return usersList;
    }

        public List<String> getAllLabels(){
            List<String> labels = new ArrayList<String>();

            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_NAME;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    labels.add(cursor.getString(1));
                } while (cursor.moveToNext());
            }

            // closing connection
            cursor.close();
            db.close();

            // returning lables
            return labels;
  }

}
