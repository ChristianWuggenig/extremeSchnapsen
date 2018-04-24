package cardfactory.com.extremeschnapsen;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class ServerAsyncTask extends AsyncTask{
    List<Deck> currentDeck;

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            while (true) {
                ServerSocket serverSocket = new ServerSocket(8888);

                Socket client = serverSocket.accept();
                InputStream inputStream = client.getInputStream();

                Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                String message = scanner.hasNext() ? scanner.next() : "";

                if(message.contains(";")) {
                    String[] splitted = message.split(";");

                    List<Deck> wrongDeck = (List<Deck>) objects[0];
                    currentDeck = new ArrayList<>();

                    for (int i = 0; i < splitted.length; i++) {
                        for (int j = 0; j < wrongDeck.size(); j++) {
                            if(Long.parseLong(splitted[i]) == wrongDeck.get(j).getCardID()) {
                                currentDeck.add(wrongDeck.get(j));
                            }
                        }
                    }

                    updateDecks(currentDeck);
                } else if (message != ""){
                    updateCardPlayed(Integer.parseInt(message));
                }

                serverSocket.close();

                Log.d("success", message);
            }




        } catch (IOException ex) {
            Log.d("error", ex.getMessage());
        }
        return null;
    }

    public abstract void updateDecks(List<Deck> currentDeck);

    public abstract void updateCardPlayed(int cardID);
}
