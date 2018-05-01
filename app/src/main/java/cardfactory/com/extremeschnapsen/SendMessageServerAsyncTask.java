package cardfactory.com.extremeschnapsen;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SendMessageServerAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Socket client = (Socket)objects[0];

            Log.d("SendMessageServer", "Preparing for sending data to client");

            /*OutputStream outputStream = client.getOutputStream();
            outputStream.write(((String)objects[1]).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();*/

            PrintWriter printWriter = new PrintWriter(client.getOutputStream(), true);
            printWriter.write((String)objects[1], 0, ((String)objects[1]).length());
            printWriter.flush();
            printWriter.close();

            Log.d("SendMessageServer", "Data successfully sent to client, finishing thread");

        } catch (IOException ex) {
            Log.d("ServerSendError", ex.getMessage());
        }

        return null;
    }
}
