package cardfactory.com.extremeschnapsen;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class SendMessageClientAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Socket socket = (Socket)objects[0];

            //OutputStream outputStream = socket.getOutputStream();
            //BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            Log.d("SendMessageClient", "Preparing for sending data to server");

            if(objects[1].getClass() == ArrayList.class) {
                List<Deck> currentDeck = (List<Deck>) objects[1];

                String message = "";

                for (Deck deck : currentDeck) {
                    message += deck.getCardID() + ";";
                    //outputStream.write((String.valueOf(deck.getCardID()) + ";").getBytes(StandardCharsets.UTF_8));
                    //printWriter.write(deck.getCardID() + ";");
                }

                //outputStream.write("\n".getBytes(StandardCharsets.UTF_8));

                byte[] byteMessage = message.getBytes(StandardCharsets.UTF_8);
                printWriter.write(message, 0, message.length());
                //outputStream.write(byteMessage, 0, byteMessage.length);

                //updateDecks(currentDeck);
            } else if(objects[1].getClass() == Integer.class) {
                //outputStream.write(String.valueOf((Integer)objects[1] + "+").getBytes(StandardCharsets.UTF_8));

                //updateCardPlayed((int)objects[1]);

                //startServer();
            }

            printWriter.flush();
            printWriter.close();
            socket.close();
            //outputStream.flush();

            Log.d("SendMessageClient", "Successfully sent data to server");

            waitForCard();

        } catch (IOException ex) {
            Log.d("ClientError", ex.getMessage());
        }

        return null;
    }

    public abstract void waitForCard();
}
