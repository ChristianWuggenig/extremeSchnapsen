package cardfactory.com.extremeschnapsen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by NapeStar on 03.04.18.
 * helper class for creating SQLLite-Database
 * contains important constants like tablename, database version, name of columns
 * methods: constructor and onCreate(SQLiteDatabase db)
 */

public class DbHelper extends SQLiteOpenHelper {

    //LOG_TAG for filtering in logcat
    private static final String LOG_TAG = DbHelper.class.getSimpleName();
    public static final String DB_NAME = "extreme_schnapsen.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_PLAYER_LIST = "player_list";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";

    //string for sql query -> CREATE TABLE
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_PLAYER_LIST +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL);";

    //constructor creates database
    public DbHelper(Context context) {
        //super(context, "PLATZHALTER_DATENBANKNAME", null, 1);
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    // onCreate method creates table player_list, if not already created
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}






