package cardfactory.com.extremeschnapsen.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.models.Player;

/**
 * Created by NapeStar on 04.04.18.
 *This class is a Data Acces Object (DAO) and resposible for the CRUD management.
 *It maintains the database connection and add, read and (delete) dataset in Table Player_List
 *Converts database entries into Player objects
 *methods: constructor, open(), close(), createPlayer(String username),
 * cursorToPlayer(Cursor cursor), getAllPlayers(),
 */

public class PlayerDataSource {

    //LOG_TAG for filtering in logcat
    private static final String LOG_TAG = PlayerDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DbHelper dbHelper;

    private String[] columns = {
            DbHelper.COLUMN_PLAYER_ID,
            DbHelper.COLUMN_PLAYER_USERNAME
    };

    public PlayerDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
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

    public Player createPlayer(String username) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_PLAYER_USERNAME, username);

        long insertId = database.insert(DbHelper.TABLE_PLAYER_LIST, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_PLAYER_LIST,
                columns, DbHelper.COLUMN_CARD_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Player player = cursorToPlayer(cursor);
        cursor.close();

        return player;
    }

    private Player cursorToPlayer(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_PLAYER_ID);
        int idUsername = cursor.getColumnIndex(DbHelper.COLUMN_PLAYER_USERNAME);

        String username = cursor.getString(idUsername);
        long id = cursor.getLong(idIndex);

        Player player = new Player(id, username);

        return player;
    }

    public List<Player> getAllPlayers() {
        List<Player> playerList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_PLAYER_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        Player player;

        while(!cursor.isAfterLast()) {
            player = cursorToPlayer(cursor);
            playerList.add(player);
            Log.d(LOG_TAG, "ID: " + player.getId() + ", Inhalt: " + player.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return playerList;
    }

}