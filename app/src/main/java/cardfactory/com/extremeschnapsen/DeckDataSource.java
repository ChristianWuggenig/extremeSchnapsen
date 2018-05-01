package cardfactory.com.extremeschnapsen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by NapeStar on 21.04.18.
 */

public class DeckDataSource {

    //LOG_TAG for filtering in logcat
    private static final String LOG_TAG = DeckDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DbHelper dbHelper;


    private String[] columns = {
            DbHelper.COLUMN_DECK_ID,
            DbHelper.COLUMN_DECK_CARD_ID,
            DbHelper.COLUMN_DECKSUIT,
            DbHelper.COLUMN_DECKRANK,
            DbHelper.COLUMN_DECKVALUE,
            DbHelper.COLUMN_DECKSTATUS,
            DbHelper.COLUMN_DECKTRUMP
    };

    public DeckDataSource(Context context) {
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

    public Deck createDeck(long cardID, String cardSuit, String cardRank, int cardValue, int deckStatus, int deckTrump) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_DECK_CARD_ID, cardID);
        values.put(DbHelper.COLUMN_DECKSUIT, cardSuit);
        values.put(DbHelper.COLUMN_DECKRANK, cardRank);
        values.put(DbHelper.COLUMN_DECKVALUE, cardValue);
        values.put(DbHelper.COLUMN_DECKSTATUS, deckStatus);
        values.put(DbHelper.COLUMN_DECKTRUMP, deckTrump);

        long insertId = database.insert(DbHelper.TABLE_DECK_LIST, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_DECK_LIST,
                columns, DbHelper.COLUMN_DECK_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Deck deck = cursorToDeck(cursor);
        cursor.close();

        return deck;
    }

    public Deck createDeck(Deck givenDeck) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_DECK_CARD_ID, givenDeck.getCardID());
        values.put(DbHelper.COLUMN_DECKSUIT, givenDeck.getCardSuit());
        values.put(DbHelper.COLUMN_DECKRANK, givenDeck.getCardRank());
        values.put(DbHelper.COLUMN_DECKVALUE, givenDeck.getCardValue());
        values.put(DbHelper.COLUMN_DECKSTATUS, givenDeck.getDeckStatus());
        values.put(DbHelper.COLUMN_DECKTRUMP, givenDeck.getDeckTrump());

        long insertId = database.insert(DbHelper.TABLE_DECK_LIST, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_DECK_LIST,
                columns, DbHelper.COLUMN_DECK_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Deck deck = cursorToDeck(cursor);
        cursor.close();

        return deck;
    }

    private Deck cursorToDeck(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_DECK_ID);
        int idCardIndex = cursor.getColumnIndex(DbHelper.COLUMN_DECK_CARD_ID);
        int idDeckSuit = cursor.getColumnIndex(DbHelper.COLUMN_DECKSUIT);
        int idDeckRank = cursor.getColumnIndex(DbHelper.COLUMN_DECKRANK);
        int idDeckValue = cursor.getColumnIndex(DbHelper.COLUMN_DECKVALUE);
        int idDeckStatus = cursor.getColumnIndex(DbHelper.COLUMN_DECKSTATUS);
        int idDeckTrump = cursor.getColumnIndex(DbHelper.COLUMN_DECKTRUMP);

        long card_id = cursor.getLong(idCardIndex);
        String deckSuit = cursor.getString(idDeckSuit);
        String deckRank = cursor.getString(idDeckRank);
        int deckValue = cursor.getInt(idDeckValue);
        int deckStatus = cursor.getInt(idDeckStatus);
        int deckTrump = cursor.getInt(idDeckTrump);

        long id = cursor.getLong(idIndex);

        Deck deck = new Deck(id, card_id, deckSuit, deckRank, deckValue, deckStatus, deckTrump);

        return deck;
    }

    public List<Deck> getAllDeck() {
        List<Deck> deckList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_DECK_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        Deck deck;

        while(!cursor.isAfterLast()) {
            deck = cursorToDeck(cursor);
            deckList.add(deck);
            Log.d(LOG_TAG, "ID: " + deck.getDeckID() + ", Inhalt: " + deck.toString());
            cursor.moveToNext();
        }

        cursor.close();
        return deckList;
    }

    //Gegenspieler baut sich aus übergebenen int array und der cardList
    //den Decktable zusammen

    public List<Deck> receiveShuffeldDeck(int a[], List<Card> cardList){
        List<Deck> deckList = new ArrayList<>();

        Deck deck;

        for (int index : a) {
            for (Card card : cardList) {
                if (index == card.getCardID()){
                    deck = new Deck(card);
                    deckList.add(deck);
                }
            }
        }

        for (int i = 0; i <=4; i++){
            deckList.get(i).setDeckStatus(1);
        }

        for (int i =5; i <=9; i++){
            deckList.get(i).setDeckStatus(2);
        }

        deckList.get(10).setDeckStatus(3);

        for (int i =11; i <=19; i++){
            deckList.get(i).setDeckStatus(4);
        }

        for (Deck deck1: deckList){
            if (deck1.getCardSuit().equals(deckList.get(10).getCardSuit())){
                deck1.setDeckTrump(1);
            }
            createDeck(deck1);
        }

        return deckList;

    }

    //deckliste wird erstellt
    public List<Deck> shuffelDeck(List<Card> cardList){
        List<Deck> deckList = new ArrayList<>();
        //mischt cardList durch
        Collections.shuffle(cardList);
        Deck deck;

        for (Card card : cardList){
            //Instanzen der Klasse Deck werden angelegt und der deckList hinzugefügt.
            deck = new Deck(card);
            deckList.add(deck);
        }

        for (int i = 0; i <=4; i++){
            deckList.get(i).setDeckStatus(1);
        }

        for (int i =5; i <=9; i++){
            deckList.get(i).setDeckStatus(2);
        }

        deckList.get(10).setDeckStatus(3);

        for (int i =11; i <=19; i++){
            deckList.get(i).setDeckStatus(4);
        }

        for (Deck deck1: deckList){
            if (deck1.getCardSuit().equals(deckList.get(10).getCardSuit())){
                deck1.setDeckTrump(1);
            }
            createDeck(deck1);
        }

        return deckList;

    }

    //updatet status einer Karte im Decktable
    public Deck updateDeckStatus(long card_id, int new_status) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_DECKSTATUS, new_status);

        database.update(DbHelper.TABLE_DECK_LIST,
                values,
                DbHelper.COLUMN_DECK_CARD_ID + "=" + card_id,
                null);

        Cursor cursor = database.query(DbHelper.TABLE_DECK_LIST,
                columns, DbHelper.COLUMN_DECK_CARD_ID + "=" + card_id,
                null, null, null, null);

        cursor.moveToFirst();
        Deck deck = cursorToDeck(cursor);
        cursor.close();

        return deck;
    }

    //löscht alle Einträge im Decktabel
    public void deleteDeckTable() {
        int anzahl_gelöschte_einträge = database.delete(DbHelper.TABLE_DECK_LIST,
                null,
                null);

        Log.d(LOG_TAG, "Es wurden " + anzahl_gelöschte_einträge + " Einträg im Decktable gelöscht");
    }


}
