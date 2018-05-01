package cardfactory.com.extremeschnapsen;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class StartServerAsyncTask extends AsyncTask{
    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            Log.d("StartServer", "Starting ServerSocket");

            ServerSocket serverSocket = new ServerSocket(8888);

            Log.d("StartServer", "Successfully started ServerSocket, waiting for connections");

            Socket client = serverSocket.accept();
            client.setTcpNoDelay(true);

            Log.d("StartServer", "Processing finished, returning client-socket");

            getClientSocket(client);


        } catch (IOException ex) {
            Log.d("ServerStartError", ex.getMessage());
        }

        return null;
    }

    public abstract void getClientSocket(Socket clientSocket);
}
