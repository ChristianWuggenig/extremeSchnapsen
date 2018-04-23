package cardfactory.com.extremeschnapsen;

/**
 * Created by NapeStar on 23.04.18.
 */

public class Card {

    private long cardID;
    private String cardSuit;
    private String cardRank;
    private int cardValue;

    public Card (long cardID, String cardSuit, String cardRank, int cardValue){
        this.cardID = cardID;
        this.cardSuit = cardSuit;
        this.cardRank = cardRank;
        this.cardValue = cardValue;

    }

    public long getCardID() {
        return cardID;
    }

    public void setCardID(long cardID) {
        this.cardID = cardID;
    }

    public String getCardSuit() {
        return cardSuit;
    }

    public void setCardSuit(String cardSuit) {
        this.cardSuit = cardSuit;
    }

    public String getCardRank() {
        return cardRank;
    }

    public void setCardRank(String cardRank) {
        this.cardRank = cardRank;
    }

    public int getCardValue() {
        return cardValue;
    }

    public void setCardValue(int cardValue) {
        this.cardValue = cardValue;
    }


    public String toString(){
        String output = cardID + " " + cardSuit + " " + cardRank + " " + cardValue;
        return output;
    }


}
