package cardfactory.com.extremeschnapsen;

public interface INetworkDisplay {
    /**
     * display a given message on the activity
     * @param message the given message
     */
    void displayStatus(String message);

    /**
     * update the deck with a given deck (client only!)
     * @param shuffledDeckIDs
     */
    void displayShuffledDeck(int[] shuffledDeckIDs);

    /**
     * set my turn if necessary and provide the id of the played card used for the comparation algorithm to get the winner
     * @param cardPlayed
     */
    void setMyTurn(int cardPlayed);

    void dismissDialog();
}
