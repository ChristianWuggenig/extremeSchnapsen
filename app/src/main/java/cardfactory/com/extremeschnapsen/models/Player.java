package cardfactory.com.extremeschnapsen.models;

/**
 * Created by NapeStar on 03.04.18.
 * Instance of this class (Player)can store data of corresponding SQLite-dataset and
 * represent the dataset in the code.
 * methods: getter und setter, constructor and toString()
 */

public class Player {

    private long id;
    private String username;
    private int played_games;
    private int won_games;


    public Player (long id, String username){
        this.id = id;
        this.username = username;
        this.played_games = 0;
        this.won_games = 0;
    }

    public Player (){

    }

    public Player(long id, String username, int played_games, int won_games) {
        this.id = id;
        this.username = username;
        this.played_games = played_games;
        this.won_games = won_games;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPlayed_games() {
        return played_games;
    }

    public void setPlayed_games(int played_games) {
        this.played_games = played_games;
    }

    public int getWon_games() {
        return won_games;
    }

    public void setWon_games(int won_games) {
        this.won_games = won_games;
    }

    public String toString(){
        String output = id + " " + username + " " + played_games + " " + won_games + " ";
     return output;
    }
}
