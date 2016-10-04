package org.iotope.iotopeprint;

/**
 * Created by hadarayoub on 28/09/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBAdapter {

    DatabaseHelper	DBHelper;
    Context context;
    SQLiteDatabase db;


    public DBAdapter(Context context){
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    //Setter DatabaseHelper Object
    public DatabaseHelper setDataBaseHelper(Context context){
        DBHelper = new DatabaseHelper(context);
        return DBHelper;
    }

    // Getter sqliteDatabase instanciated in DBAdapter.open method
    public SQLiteDatabase getDb() {
        return db;
    }


    // Methods DatabaseHelper Class :
    // --> Constuctor
    // --> Create sqlite database
    // --> Upgrade sqlite database
    public class DatabaseHelper extends SQLiteOpenHelper {

        Context context;

        public DatabaseHelper(Context context) {
            super(context, "inscrits", null, 1);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table inscrits (full_name text not null, "
                    + "email text not null, pass text not null, "
                    + "registration_code text primary key, company text not null, "
                    + "conf_day text not null "
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Toast.makeText(context, "Mise à jour de la Base de données", Toast.LENGTH_SHORT).show();
            db.execSQL("DROP TABLE IF EXISTS inscrits");
            onCreate(db);
        }

    }

    public DBAdapter open(){
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        db.close();
    }

    public void Truncate(){
        db.execSQL("DELETE FROM inscrits");
    }

    public long insererUnInscrits(String name, String regCode, String company, String email, String typePass, String confDay){
        ContentValues values = new ContentValues();
        values.put("full_name", name);
        values.put("email", email);
        values.put("pass", typePass);
        values.put("registration_code", regCode);
        values.put("company", company);
        values.put("conf_day", confDay);
        return db.insert("inscrits", null, values);
    }

    public boolean supprimerInscrit(String regCode){
        return db.delete("inscrits", "id="+regCode, null)>0;
    }

    public Cursor getGuestsList(){
        return db.query("inscrits",new String[]{"full_name",
                "email",
                "pass",
                "registration_code",
                "company",
                "conf_day"},null,null,null,null,null);
    }

    public Cursor getGuest(String regCode){

        return db.query("inscrits",new String[]{"full_name",
                "email",
                "pass",
                "registration_code",
                "company",
                "conf_day"},"registration_code=?",new String[]{ regCode },null,null,null);
    }
}
