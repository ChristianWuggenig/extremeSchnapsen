package cardfactory.com.extremeschnapsen.models;

/**
 * Created by NapeStar on 21.04.18.
 */

public class Deck extends Card {

    private long deckID;
    private int deckStatus;
    private int deckTrump;

    public Deck() {
        super(0, null, null, 0);
        this.deckID = 0;
        this.deckStatus = 0;
        this.deckTrump = 0;
    }

    public Deck(long deckID, long cardID, String cardSuit, String cardRank, int cardValue, int deckStatus, int deckTrump) {
        super(cardID, cardSuit, cardRank, cardValue);
        this.deckID = deckID;
        this.deckStatus = deckStatus;
        this.deckTrump = deckTrump;
    }

    public Deck(long cardID, String cardSuit, String cardRank, int cardValue) {
        super(cardID, cardSuit, cardRank, cardValue);

        this.deckStatus = 1;
        this.deckTrump = 0;
    }

    public Deck(Card card){

        super(card.getCardID(), card.getCardSuit(), card.getCardRank(), card.getCardValue());
        this.deckID = 1;
        this.deckStatus = 1;
        this.deckTrump = 0;
    }

    public long getDeckID() {
        return deckID;
    }

    public void setDeckID(long deckID) {
        this.deckID = deckID;
    }

    public int getDeckStatus() {
        return deckStatus;
    }

    public void setDeckStatus(int deckStatus) {
        this.deckStatus = deckStatus;
    }

    public int getDeckTrump() {
        return deckTrump;
    }

    public void setDeckTrump(int deckTrump) {
        this.deckTrump = deckTrump;
    }

    public String toString(){
        String output = super.toString() + " " + deckStatus + " " + deckTrump;
        return output;
    }
}
