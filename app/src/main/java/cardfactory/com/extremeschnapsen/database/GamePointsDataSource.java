package cardfactory.com.extremeschnapsen.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.models.GamePoints;

/**
 * Created by NapeStar on 08.05.18.
 */

public class GamePointsDataSource implements Serializable {


    //LOG_TAG for filtering in logcat
    private static final String LOG_TAG = GamePointsDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DbHelper dbHelper;


    private String[] columns = {
            DbHelper.COLUMN_GAME_POINTS_ID,
            DbHelper.COLUMN_GAME_ID,
            DbHelper.COLUMN_GAME_POINTS_PLAYER1,
            DbHelper.COLUMN_GAME_POINTS_PLAYER2
    };

    public GamePointsDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DeckDataSource erzeugt jetzt den dbHelper.");
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

    public GamePoints getCurrentGamePointsObject(){
        List<GamePoints> gamePointsList = getAllGamePoints();
        GamePoints gp =null;

        for (GamePoints gpl : gamePointsList){
            if (gpl.getGameID() == 1){
                gp = gpl;
                break;
            }
        }

        return gp;

    }

    public GamePoints createGamePoints(long gameID, int pointsplayer1, int pointsplayer2) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_GAME_ID, gameID);
        values.put(DbHelper.COLUMN_GAME_POINTS_PLAYER1, pointsplayer1);
        values.put(DbHelper.COLUMN_GAME_POINTS_PLAYER2, pointsplayer2);

        long insertId = database.insert(DbHelper.TABLE_GAME_POINTS_LIST, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_GAME_POINTS_LIST,
                columns, DbHelper.COLUMN_GAME_POINTS_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        GamePoints gamepoints = cursorToGamePoints(cursor);
        cursor.close();

        return gamepoints;
    }

    private GamePoints cursorToGamePoints(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_GAME_POINTS_ID);
        int idGameIndex = cursor.getColumnIndex(DbHelper.COLUMN_GAME_ID);
        int idGamePointsPlayer1 = cursor.getColumnIndex(DbHelper.COLUMN_GAME_POINTS_PLAYER1);
        int idGamePointsPlayer2 = cursor.getColumnIndex(DbHelper.COLUMN_GAME_POINTS_PLAYER2);

        int game_id = cursor.getInt(idGameIndex);
        int gamePointsPlayer1 = cursor.getInt(idGamePointsPlayer1);
        int gamePointsPlayer2 = cursor.getInt(idGamePointsPlayer2);

        long id = cursor.getLong(idIndex);

        GamePoints gamePoints = new GamePoints(id, game_id, gamePointsPlayer1, gamePointsPlayer2);

        return gamePoints;
    }

    public List<GamePoints> getAllGamePoints() {
        List<GamePoints> gamePointsList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_GAME_POINTS_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        GamePoints gamePoints;

        while(!cursor.isAfterLast()) {
            gamePoints = cursorToGamePoints(cursor);
            gamePointsList.add(gamePoints);
            Log.d(LOG_TAG, "ID: " + gamePoints.getGamePointsID() + ", Inhalt: " + gamePoints.toString());
            cursor.moveToNext();
        }

        cursor.close();
        return gamePointsList;
    }

    //updatet Punkte von Player 1 und Player 1
    public GamePoints updateGamePoints(int gameID, int gamePointsPlayer1, int gamePointsPlayer2) {
        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_GAME_POINTS_PLAYER1, gamePointsPlayer1);
        values.put(DbHelper.COLUMN_GAME_POINTS_PLAYER2, gamePointsPlayer2);

        database.update(DbHelper.TABLE_GAME_POINTS_LIST,
                values,
                DbHelper.COLUMN_GAME_ID + "=" + gameID,
                null);

        Cursor cursor = database.query(DbHelper.TABLE_GAME_POINTS_LIST,
                columns, DbHelper.COLUMN_GAME_ID + "=" + gameID,
                null, null, null, null);

        cursor.moveToFirst();
        GamePoints gamePoints = cursorToGamePoints(cursor);
        cursor.close();

        return gamePoints;
    }

    //löscht alle Einträge im GamePointstable
    public void deleteGamePoinsTable() {
        int anzahl_gelöschte_einträge = database.delete(DbHelper.TABLE_GAME_POINTS_LIST,
                null,
                null);

        Log.d(LOG_TAG, "Es wurden " + anzahl_gelöschte_einträge + " Einträg im GamePointstable gelöscht");
    }

}
