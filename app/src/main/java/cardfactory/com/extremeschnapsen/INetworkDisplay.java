package cardfactory.com.extremeschnapsen;

public interface INetworkDisplay {
    public void displayStatus(String message);

    public void displayShuffledDeck(int[] shuffledDeckIDs);

    public void setMyTurn(int cardPlayed);
}
