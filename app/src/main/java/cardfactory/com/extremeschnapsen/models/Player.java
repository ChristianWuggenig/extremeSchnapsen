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


    public Player (long id, String username){
        this.id = id;
        this.username = username;

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

    public String toString(){
        String output = id + " " + username;
     return output;
    }
}
