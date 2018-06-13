package cardfactory.com.extremeschnapsen.networking;

public interface INetworkDisplay {
    /**
     * display a given message on the activity
     * @param message the given message
     */
    void displayUserInformation(String message);

    /**
     * update the deck with a given deck (client only!)
     * @param shuffledDeckIDs
     */
    void displayShuffledDeck(int[] shuffledDeckIDs, String playerName);

    /**
     * display the player name on receive
     * @param playerName contains the name of the opposite player
     */
    void displayPlayer(String playerName);

    /**
     * dismiss the "waiting for opposite player" dialog
     */
    void dismissDialog();

    /**
     * client only: wait for a card played by server
     */
    void waitForCard();

    /**
     * update the deck on the ui
     */
    void updateDeck();

    /**
     * called when an action is received over the network (card played, trump exchanged, etc)
     * @param action the action ("trump", "turn",...)
     * @param value the value ("true", "pik",...)
     */
    void receiveAction(String action, String value);
}
