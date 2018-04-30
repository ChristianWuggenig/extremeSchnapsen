package cardfactory.com.extremeschnapsen;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class ClientAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d("Client", "starting ClientAsyncTask");

            Socket socket = new Socket();
            socket.bind(null);

            Log.d("Client", "Trying to connect to ip " + (String)objects[0]);

            socket.connect((new InetSocketAddress((String)objects[0], 8888)), 0);

            Log.d("Client", "Socket is created and connected to " + (String)objects[0]);

            OutputStream outputStream = socket.getOutputStream();

            Log.d("Client", "received outputStream");

            if(objects[1].getClass() == ArrayList.class) {
                List<Deck> currentDeck = (List<Deck>) objects[1];

                for (Deck deck : currentDeck) {
                    outputStream.write((String.valueOf(deck.getCardID()) + ";").getBytes(StandardCharsets.UTF_8));
                }

                updateDecks(currentDeck);
            } else if(objects[1].getClass() == Integer.class) {
                outputStream.write(String.valueOf((Integer)objects[1] + "+").getBytes(StandardCharsets.UTF_8));

                updateCardPlayed((int)objects[1]);

                startServer();
            }

            Log.d("Client", "Closing outputStream");

            InputStream inputStream = socket.getInputStream();

            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String message = scanner.hasNext() ? scanner.next() : "";

            Log.d("testtest", message);

            Log.d("Client", "Closing socket");

            outputStream.close();
            inputStream.close();
            socket.close();

            Log.d("Client", "Socket closed successfully, finishing thread");

        } catch (IOException ex) {
            Log.d("error", ex.getMessage());
        }

        cancel(true);

        return null;
    }

    public abstract void updateDecks(List<Deck> currentDeck);

    public abstract void updateCardPlayed(int cardID);

    public abstract void startServer();
}
