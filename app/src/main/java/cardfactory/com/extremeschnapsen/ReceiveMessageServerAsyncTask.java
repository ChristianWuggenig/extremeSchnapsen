package cardfactory.com.extremeschnapsen;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class ReceiveMessageServerAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d("ReceiveMessageServer", "Waiting for client messages");

            Socket client = (Socket)objects[0];

            InputStream inputStream = client.getInputStream();

            List<Deck> currentDeck;

            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String message = scanner.hasNext() ? scanner.next() : "";

            Log.d("ReceiveMessageServer", "Successfully received data from client");

            if(message.contains(";")) {
                Log.d("ReceiveMessageServer", "Message contains decklist, proceeding input...");

                String[] splitted = message.split(";");

                List<Deck> wrongDeck = (List<Deck>) objects[1];
                currentDeck = new ArrayList<>();

                for (int i = 0; i < splitted.length; i++) {
                    for (int j = 0; j < wrongDeck.size(); j++) {
                        if(Long.parseLong(splitted[i]) == wrongDeck.get(j).getCardID()) {
                            currentDeck.add(wrongDeck.get(j));
                        }
                    }
                }

                Log.d("ReceiveMessageServer", "Successfully received deck, updating GUI");

                //updateIP(client.getInetAddress().toString().substring(1));

                updateDecks(currentDeck);

            } else if (message.contains("+")){
                Log.d("ReceiveMessageServer", "Message contains played card, proceeding input...");

                updateCardPlayed(Integer.parseInt(message.substring(0, 1)));
            }

            Log.d("ReceiveMessageServer", "Processing done, finishing thread");

        } catch (IOException ex) {
            Log.d("ServerReceiveError", ex.getMessage());
        }

        return null;
    }

    public abstract void updateDecks(List<Deck> currentDeckFromClient);

    public abstract void updateCardPlayed(int cardID);
}
