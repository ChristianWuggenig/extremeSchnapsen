package cardfactory.com.extremeschnapsen.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.models.RoundPoints;

public class RoundPointsDataSource {

    private static final String LOG_TAG = RoundPointsDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private long insertId;



    private String[] columns = {
            DbHelper.COLUMN_ROUNDPOINTS_ID,
            DbHelper.COLUMN_CURRENTROUNDPOINTS,
            DbHelper.COLUMN_POINTSPLAYER1,
            DbHelper.COLUMN_POINTSPLAYER2,
            DbHelper.COLUMN_HIDDENPOINTSPLAYER1,
            DbHelper.COLUMN_HIDDENPOINTSPLAYER2,
            DbHelper.COLUMN_ROUND_MOVES,
            DbHelper.COLUMN_ROUND_PHASE,
            DbHelper.COLUMN_ROUND_TRUMPEXCHANGED,
            DbHelper.COLUMN_ROUND_SIGHTJOKERPLAYER1,
            DbHelper.COLUMN_ROUND_SIGHTJOKERPLAYER2,
            DbHelper.COLUMN_ROUND_PARRY_SIGHTJOKERPLAYER1,
            DbHelper.COLUMN_ROUND_PARRY_SIGHTJOKERPLAYER2,
            DbHelper.COLUMN_ROUND_CARD_EXCHANGE_PLAYER1,
            DbHelper.COLUMN_ROUND_CARD_EXCHANGE_PLAYER2
    };

    public RoundPointsDataSource(Context context) {
        //Log.d(LOG_TAG, "Unsere PointsRoundDataSource erzeugt jetzt den dbHelper.");
        dbHelper = DbHelper.getInstance(context);
    }

    public void open() {
        //Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        //Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        //Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    public RoundPoints createRoundPoints(int RoundPointsID, int CurrentRoundPointsID, int PointsPlayer1, int PointsPlayer2) {
        ContentValues values = new ContentValues();
        //values.put(DbHelper.COLUMN_ROUNDPOINTS_ID, RoundPointsID);
        values.put(DbHelper.COLUMN_CURRENTROUNDPOINTS, CurrentRoundPointsID);
        values.put(DbHelper.COLUMN_POINTSPLAYER1, PointsPlayer1);
        values.put(DbHelper.COLUMN_POINTSPLAYER2, PointsPlayer2);
        values.put(DbHelper.COLUMN_HIDDENPOINTSPLAYER1, 0);
        values.put(DbHelper.COLUMN_HIDDENPOINTSPLAYER2, 0);
        values.put(DbHelper.COLUMN_ROUND_MOVES, 0);
        values.put(DbHelper.COLUMN_ROUND_PHASE, 0);
        values.put(DbHelper.COLUMN_ROUND_TRUMPEXCHANGED, 0);
        values.put(DbHelper.COLUMN_ROUND_SIGHTJOKERPLAYER1, 0);
        values.put(DbHelper.COLUMN_ROUND_SIGHTJOKERPLAYER2, 0);
        values.put(DbHelper.COLUMN_ROUND_PARRY_SIGHTJOKERPLAYER1, 0);
        values.put(DbHelper.COLUMN_ROUND_PARRY_SIGHTJOKERPLAYER2, 0);
        values.put(DbHelper.COLUMN_ROUND_CARD_EXCHANGE_PLAYER1, 0);
        values.put(DbHelper.COLUMN_ROUND_CARD_EXCHANGE_PLAYER2, 0);

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
        int idHiddenPointsPlayer1 = cursor.getColumnIndex(DbHelper.COLUMN_HIDDENPOINTSPLAYER1);
        int idHiddenPointsPlayer2 = cursor.getColumnIndex(DbHelper.COLUMN_HIDDENPOINTSPLAYER2);
        int idMoves = cursor.getColumnIndex(DbHelper.COLUMN_ROUND_MOVES);
        int idRoundPhase = cursor.getColumnIndex(DbHelper.COLUMN_ROUND_PHASE);
        int idTrumpExchanged = cursor.getColumnIndex(DbHelper.COLUMN_ROUND_TRUMPEXCHANGED);
        int idSightJoker1 = cursor.getColumnIndex(DbHelper.COLUMN_ROUND_SIGHTJOKERPLAYER1);
        int idSightJoker2 = cursor.getColumnIndex(DbHelper.COLUMN_ROUND_SIGHTJOKERPLAYER2);
        int idParrySightJoker1 = cursor.getColumnIndex(DbHelper.COLUMN_ROUND_PARRY_SIGHTJOKERPLAYER1);
        int idParrySightJoker2 = cursor.getColumnIndex(DbHelper.COLUMN_ROUND_PARRY_SIGHTJOKERPLAYER2);
        int idCardExchange1 = cursor.getColumnIndex(DbHelper.COLUMN_ROUND_CARD_EXCHANGE_PLAYER1);
        int idCardExchange2 = cursor.getColumnIndex(DbHelper.COLUMN_ROUND_CARD_EXCHANGE_PLAYER2);


        int RoundPointsID = cursor.getInt(idRoundPointsID);
        int CurrentRoundPoints = cursor.getInt(idCurrentRoundPoints);
        int PointsPlayer1 = cursor.getInt(idPointsPlayer1);
        int PointsPlayer2 = cursor.getInt(idPointsPlayer2);
        int HiddenPointsPlayer1 = cursor.getInt(idHiddenPointsPlayer1);
        int HiddenPointsPlayer2 = cursor.getInt(idHiddenPointsPlayer2);
        int Moves = cursor.getInt(idMoves);
        int RoundPhase = cursor.getInt(idRoundPhase);
        int TrumpExchanged = cursor.getInt(idTrumpExchanged);
        int SightJoker1 = cursor.getInt(idSightJoker1);
        int SightJoker2 = cursor.getInt(idSightJoker2);
        int ParrySightJoker1 = cursor.getInt(idParrySightJoker1);
        int ParrySightJoker2 = cursor.getInt(idParrySightJoker2);
        int CardExchange1 = cursor.getInt(idCardExchange1);
        int CardExchange2 = cursor.getInt(idCardExchange2);

        long id = cursor.getLong(idRoundPointsID);

        RoundPoints roundPoints;
        roundPoints = new RoundPoints(RoundPointsID, CurrentRoundPoints, PointsPlayer1, PointsPlayer2,
                HiddenPointsPlayer1, HiddenPointsPlayer2, Moves,
                RoundPhase, TrumpExchanged, SightJoker1, SightJoker2, ParrySightJoker1,
                ParrySightJoker2, CardExchange1, CardExchange2);

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
            //Log.d(LOG_TAG, "ID: " + roundPoints.getRoundpointsID() + ", Inhalt: " + roundPoints.toString());
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

    //richtigen Werte werden bereits übergeben.
    public void updateRoundPoints(RoundPoints pointsToTransfer){
        RoundPoints rp = new RoundPoints(1,1,0,0);
        rp = this.getCurrentRoundPointsObject();

        rp.setPointsplayer1(pointsToTransfer.getPointsplayer1());
        rp.setPointsplayer2(pointsToTransfer.getPointsplayer2());
        rp.setHiddenpointsplayer1(pointsToTransfer.getHiddenpointsplayer1());
        rp.setHiddenpointsplayer2(pointsToTransfer.getHiddenpointsplayer2());


        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_CURRENTROUNDPOINTS, rp.getCurrentroundpoints());
        values.put(DbHelper.COLUMN_ROUNDPOINTS_ID, insertId);
        values.put(DbHelper.COLUMN_POINTSPLAYER1, rp.getPointsplayer1());
        values.put(DbHelper.COLUMN_POINTSPLAYER2, rp.getPointsplayer2());
        values.put(DbHelper.COLUMN_HIDDENPOINTSPLAYER1, rp.getHiddenpointsplayer1());
        values.put(DbHelper.COLUMN_HIDDENPOINTSPLAYER2, rp.getHiddenpointsplayer2());

        database.update(DbHelper.TABLE_ROUNDPOINTS_LIST,
                values,
                DbHelper.COLUMN_CURRENTROUNDPOINTS + "=" + 1,
                null);

        getAllRoundPoints();

    }

    // hier werden die übergebenen Punkte addiert
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
        rp.setHiddenpointsplayer1(rp.getHiddenpointsplayer1() + pointsToSave.getHiddenpointsplayer1());
        rp.setHiddenpointsplayer2(rp.getHiddenpointsplayer2() + pointsToSave.getHiddenpointsplayer2());



        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_CURRENTROUNDPOINTS, rp.getCurrentroundpoints());
        values.put(DbHelper.COLUMN_ROUNDPOINTS_ID, insertId);
        values.put(DbHelper.COLUMN_POINTSPLAYER1, rp.getPointsplayer1());
        values.put(DbHelper.COLUMN_POINTSPLAYER2, rp.getPointsplayer2());
        values.put(DbHelper.COLUMN_HIDDENPOINTSPLAYER1, rp.getHiddenpointsplayer1());
        values.put(DbHelper.COLUMN_HIDDENPOINTSPLAYER2, rp.getHiddenpointsplayer2());

        database.update(DbHelper.TABLE_ROUNDPOINTS_LIST,
                values,
                DbHelper.COLUMN_CURRENTROUNDPOINTS + "=" + 1,
                null);

        getAllRoundPoints();
    }

    public void updtateTrumpExchanged(RoundPoints rp){
        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_ROUND_TRUMPEXCHANGED, rp.getTrumpExchanged());

        database.update(DbHelper.TABLE_ROUNDPOINTS_LIST,
                values,
                DbHelper.COLUMN_CURRENTROUNDPOINTS + "=" + 1,
                null);

        getAllRoundPoints();

    }

    public void updateJoker(RoundPoints rp){

        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_ROUND_SIGHTJOKERPLAYER1, rp.getSightJokerPlayer1());
        values.put(DbHelper.COLUMN_ROUND_SIGHTJOKERPLAYER2, rp.getSightJokerPlayer2());
        values.put(DbHelper.COLUMN_ROUND_PARRY_SIGHTJOKERPLAYER1, rp.getParrySightJokerPlayer1());
        values.put(DbHelper.COLUMN_ROUND_PARRY_SIGHTJOKERPLAYER2, rp.getParrySightJokerPlayer2());
        values.put(DbHelper.COLUMN_ROUND_CARD_EXCHANGE_PLAYER1, rp.getCardExchangeJokerPlayer1());
        values.put(DbHelper.COLUMN_ROUND_CARD_EXCHANGE_PLAYER2, rp.getCardExchangeJokerPlayer2());

        database.update(DbHelper.TABLE_ROUNDPOINTS_LIST,
                values,
                DbHelper.COLUMN_CURRENTROUNDPOINTS + "=" + 1,
                null);

        getAllRoundPoints();

    }

    public void increaseMoves(){

        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_ROUND_MOVES, getCurrentRoundPointsObject().getMoves() + 1);

        database.update(DbHelper.TABLE_ROUNDPOINTS_LIST,
                values,
                DbHelper.COLUMN_CURRENTROUNDPOINTS + "=" + 1,
                null);

        getAllRoundPoints();

    }

    //löscht alle Einträge im GamePointstable
    public void deleteRoundPointsTable() {
        int anzahl_gelöschte_einträge = database.delete(DbHelper.TABLE_ROUNDPOINTS_LIST,
                null,
                null);

        //Log.d(LOG_TAG, "Es wurden " + anzahl_gelöschte_einträge + " Einträg im RoundPointstable gelöscht");
    }

}


