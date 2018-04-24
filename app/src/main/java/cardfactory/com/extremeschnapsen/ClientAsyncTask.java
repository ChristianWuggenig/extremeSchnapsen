package cardfactory.com.extremeschnapsen;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class ClientAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Socket socket = new Socket();
            socket.bind(null);
            socket.connect((new InetSocketAddress("192.168.49.1", 8888)), 500);

            OutputStream outputStream = socket.getOutputStream();

            if(objects[0].getClass() == ArrayList.class) {
                List<Deck> currentDeck = (List<Deck>) objects[0];

                for (Deck deck : currentDeck) {
                    outputStream.write((String.valueOf(deck.getCardID()) + ";").getBytes(StandardCharsets.UTF_8));
                }

                outputStream.close();

                updateDecks(currentDeck);
            } else if(objects[0].getClass() == int.class) {
                outputStream.write((int)objects[0]);

                updateCardPlayed((int)objects[0]);

                outputStream.close();
            }

            socket.close();

        } catch (IOException ex) {
            Log.d("error", ex.getMessage());
        }

        return null;
    }

    public abstract void updateDecks(List<Deck> currentDeck);

    public abstract void updateCardPlayed(int cardID);
}
