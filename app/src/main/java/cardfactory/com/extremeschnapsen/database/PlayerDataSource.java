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
            DbHelper.COLUMN_PLAYER_USERNAME,
            DbHelper.COLUMN_PLAYER_PLAYED_GAMES,
            DbHelper.COLUMN_PLAYER_WON_GAMES,
            DbHelper.COLUMN_PLAYER_GAME_MODE
    };

    public PlayerDataSource(Context context) {
        //Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
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

    public Player createPlayer(String username) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_PLAYER_USERNAME, username);
        values.put(DbHelper.COLUMN_PLAYER_PLAYED_GAMES, 0);
        values.put(DbHelper.COLUMN_PLAYER_WON_GAMES, 0);
        values.put(DbHelper.COLUMN_PLAYER_GAME_MODE, "normal");

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
        int idPlayedGames = cursor.getColumnIndex(DbHelper.COLUMN_PLAYER_PLAYED_GAMES);
        int idWonGames = cursor.getColumnIndex(DbHelper.COLUMN_PLAYER_WON_GAMES);
        int idGameMode = cursor.getColumnIndex(DbHelper.COLUMN_PLAYER_GAME_MODE);

        String username = cursor.getString(idUsername);
        int playedGames = cursor.getInt(idPlayedGames);
        int wonGames = cursor.getInt(idWonGames);
        long id = cursor.getLong(idIndex);
        String gameMode = cursor.getString(idGameMode);

        Player player = new Player(id, username, playedGames, wonGames, gameMode);

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

    public Player getCurrentPlayerObject(){
        List<Player> playerList = getAllPlayers();
        Player p =null;

        for (Player pl : playerList){
            if (pl.getId() != 0){
                p = pl;
                break;
            }
        }

        return p;

    }

    public void updatePlayerStatistics(int won_games){
        Player player = getCurrentPlayerObject();
        player.setPlayed_games(player.getPlayed_games() + 1);
        player.setWon_games(player.getWon_games() + won_games);

        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_PLAYER_PLAYED_GAMES, player.getPlayed_games());
        values.put(DbHelper.COLUMN_PLAYER_WON_GAMES, player.getWon_games());

        database.update(DbHelper.TABLE_PLAYER_LIST,
                values,
                DbHelper.COLUMN_PLAYER_ID + ">=" + 1,
                null);

        getAllPlayers();

    }

    public void updateGameMode(String mode) {
        Player player = getCurrentPlayerObject();
        player.setGame_mode(mode);

        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_PLAYER_GAME_MODE, player.getGame_mode());

        database.update(DbHelper.TABLE_PLAYER_LIST,
                values,
                DbHelper.COLUMN_PLAYER_ID + ">=" + 1,
                null);
    }
}