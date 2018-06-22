package cardfactory.com.extremeschnapsen.networking;

/**
 * This interface is used on start of the game to display the game mode on the ui which was received over the network
 */
public interface IStartGame {

    /**
     * set the game mode on receiving of the information to display it on the ui
     * @param mode
     */
    void setGameMode(String mode);
}
