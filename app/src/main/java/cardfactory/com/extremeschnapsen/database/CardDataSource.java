package cardfactory.com.extremeschnapsen.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.models.Card;

/**
 * Created by NapeStar on 20.04.18.
 */

public class CardDataSource {
    //LOG_TAG for filtering in logcat
    private static final String LOG_TAG = CardDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DbHelper dbHelper;


    private String[] columns = {
            DbHelper.COLUMN_CARD_ID,
            DbHelper.COLUMN_CARDSUIT,
            DbHelper.COLUMN_CARDRANK,
            DbHelper.COLUMN_CARDVALUE
    };

    public CardDataSource(Context context) {
        //Log.d(LOG_TAG, "Unsere CardDataSource erzeugt jetzt den dbHelper.");
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

    public Card createCard(String cardSuit, String cardRank, int cardValue) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_CARDSUIT, cardSuit);
        values.put(DbHelper.COLUMN_CARDRANK, cardRank);
        values.put(DbHelper.COLUMN_CARDVALUE, cardValue);

        long insertId = database.insert(DbHelper.TABLE_CARD_LIST, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_CARD_LIST,
                columns, DbHelper.COLUMN_CARD_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Card card = cursorToCard(cursor);
        cursor.close();

        return card;
    }

    private Card cursorToCard(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_CARD_ID);
        int idCardSuit = cursor.getColumnIndex(DbHelper.COLUMN_CARDSUIT);
        int idCardRank = cursor.getColumnIndex(DbHelper.COLUMN_CARDRANK);
        int idCardValue = cursor.getColumnIndex(DbHelper.COLUMN_CARDVALUE);

        String cardSuit = cursor.getString(idCardSuit);
        String cardRank = cursor.getString(idCardRank);
        int cardValue = cursor.getInt(idCardValue);

        long id = cursor.getLong(idIndex);

        Card card = new Card(id, cardSuit, cardRank, cardValue);

        return card;
    }

    public List<Card> getAllCards() {
        List<Card> cardList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_CARD_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        Card card;

        while(!cursor.isAfterLast()) {
            card = cursorToCard(cursor);
            cardList.add(card);
            //Log.d(LOG_TAG, "ID: " + card.getCardID() + ", Inhalt: " + card.toString());
            cursor.moveToNext();
        }

        cursor.close();
        return cardList;
    }

}
