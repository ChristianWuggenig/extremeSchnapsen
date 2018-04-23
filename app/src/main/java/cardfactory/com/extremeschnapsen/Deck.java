package cardfactory.com.extremeschnapsen;

/**
 * Created by NapeStar on 21.04.18.
 */

public class Deck extends Card {

    private int deckStatus;
    private int deckTrump;

    public Deck(long cardID, String cardSuit, String cardRank, int cardValue, int deckStatus, int deckTrump) {
        super(cardID, cardSuit, cardRank, cardValue);
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
        this.deckStatus = 1;
        this.deckTrump = 0;
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
