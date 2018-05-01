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

public abstract class StartClientAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d("StartClient", "Starting ClientAsyncTask");

            Socket socket = new Socket();
            socket.bind(null);
            socket.setTcpNoDelay(true);

            Log.d("StartClient", "Trying to connect to ip " + (String)objects[0]);

            socket.connect((new InetSocketAddress((String)objects[0], 8888)), 0);

            Log.d("StartClient", "Socket is created and connected to " + (String)objects[0]);

            Log.d("StartClient", "Processing finished, returning server-socket");

            getServerSocket(socket);

        } catch (IOException ex) {
            Log.d("error", ex.getMessage());
        }

        return null;
    }

    public abstract void getServerSocket(Socket serverSocket);
}
