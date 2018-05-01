package cardfactory.com.extremeschnapsen;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public abstract class ReceiveMessageClientAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d("ReceiveMessageClient", "Waiting for server messages");

            Socket socket = (Socket)objects[0];

            //InputStream inputStream = socket.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            Log.d("ReceiveMessageClient", "Successfully received data from server");

            //Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            //String message = scanner.hasNext() ? scanner.next() : "";

            String message = reader.readLine();

            if (message.contains("+")){
                Log.d("ReceiveMessageClient", "Message contains played card, proceeding input...");

                updateCardPlayed(Integer.parseInt(message.substring(0, 1)));
            }

            reader.close();

        } catch (IOException ex) {
            Log.d("ClientError", ex.getMessage());
        }

        return null;
    }

    public abstract void updateCardPlayed(int cardID);
}
