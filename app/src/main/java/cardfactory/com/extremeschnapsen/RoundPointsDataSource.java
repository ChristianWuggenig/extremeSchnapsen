package cardfactory.com.extremeschnapsen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RoundPointsDataSource {

    private static final String LOG_TAG = cardfactory.com.extremeschnapsen.RoundPointsDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private long insertId;

    private String[] columns = {
            DbHelper.COLUMN_ROUNDPOINTS_ID,
            DbHelper.COLUMN_CURRENTROUNDPOINTS,
            DbHelper.COLUMN_POINTSPLAYER1,
            DbHelper.COLUMN_POINTSPLAYER2
    };

    public RoundPointsDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere PointsRoundDataSource erzeugt jetzt den dbHelper.");
        dbHelper = DbHelper.getInstance(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    public RoundPoints createRoundPoints(int RoundPointsID, int CurrentRoundPointsID, int PointsPlayer1, int PointsPlayer2) {
        ContentValues values = new ContentValues();
        //values.put(DbHelper.COLUMN_ROUNDPOINTS_ID, RoundPointsID);
        values.put(DbHelper.COLUMN_CURRENTROUNDPOINTS, CurrentRoundPointsID);
        values.put(DbHelper.COLUMN_POINTSPLAYER1, PointsPlayer1);
        values.put(DbHelper.COLUMN_POINTSPLAYER2, PointsPlayer2);

        insertId = database.insert(DbHelper.TABLE_ROUNDPOINTS_LIST, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_ROUNDPOINTS_LIST,
                columns, DbHelper.COLUMN_ROUNDPOINTS_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        RoundPoints roundPoints = cursorToRoundpoints(cursor);
        cursor.close();

        return roundPoints;
    }

    private RoundPoints cursorToRoundpoints(Cursor cursor) {
        int idRoundPointsID = cursor.getColumnIndex(DbHelper.COLUMN_ROUNDPOINTS_ID);
        int idCurrentRoundPoints = cursor.getColumnIndex(DbHelper.COLUMN_CURRENTROUNDPOINTS);
        int idPointsPlayer1 = cursor.getColumnIndex(DbHelper.COLUMN_POINTSPLAYER1);
        int idPointsPlayer2 = cursor.getColumnIndex(DbHelper.COLUMN_POINTSPLAYER2);


        int RoundPointsID = cursor.getInt(idRoundPointsID);
        int CurrentRoundPoints = cursor.getInt(idCurrentRoundPoints);
        int PointsPlayer1 = cursor.getInt(idPointsPlayer1);
        int PointsPlayer2 = cursor.getInt(idPointsPlayer2);


        long id = cursor.getLong(idRoundPointsID);

        RoundPoints roundPoints;
        roundPoints = new RoundPoints(RoundPointsID, CurrentRoundPoints, PointsPlayer1, PointsPlayer2);

        return roundPoints;
    }

    public List<RoundPoints> getAllRoundPoints() {
        List<RoundPoints> roundPointsList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_ROUNDPOINTS_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        RoundPoints roundPoints;

        while(!cursor.isAfterLast()) {
            roundPoints = cursorToRoundpoints(cursor);
            roundPointsList.add(roundPoints);
            Log.d(LOG_TAG, "ID: " + roundPoints.getRoundpointsID() + ", Inhalt: " + roundPoints.toString());
            cursor.moveToNext();
        }

        cursor.close();
        return roundPointsList;
    }

    public RoundPoints getCurrentRoundPointsObject(){
        List<RoundPoints> roundPointsList = getAllRoundPoints();
        RoundPoints rp =null;

        for (RoundPoints rpl : roundPointsList){
            if (rpl.getCurrentroundpoints() == 1){
                rp = rpl;
                break;
            }
        }

        return rp;

    }

    public void saveRoundPoints(RoundPoints pointsToSave){

        List<RoundPoints> roundPointsList = getAllRoundPoints();
        RoundPoints rp =null;

        for (RoundPoints rpl : roundPointsList){
            if (rpl.getCurrentroundpoints() == 1){
                rp = rpl;
                break;
            }
        }

        rp.setPointsplayer1(rp.getPointsplayer1() + pointsToSave.getPointsplayer1());
        rp.setPointsplayer2(rp.getPointsplayer2() + pointsToSave.getPointsplayer2());


        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_CURRENTROUNDPOINTS, rp.getCurrentroundpoints());
        values.put(DbHelper.COLUMN_ROUNDPOINTS_ID, insertId);
        values.put(DbHelper.COLUMN_POINTSPLAYER1, rp.getPointsplayer1());
        values.put(DbHelper.COLUMN_POINTSPLAYER2, rp.getPointsplayer2());

        database.update(DbHelper.TABLE_ROUNDPOINTS_LIST,
                values,
                DbHelper.COLUMN_CURRENTROUNDPOINTS + "=" + 1,
                null);

        getAllRoundPoints();
    }

}


