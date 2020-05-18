package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread {

    int port;
    String coin;
    TextView view;
    Socket socket;

    static String client = "CLIENT";

    public Client(int port, String coin, TextView view) {
        this.port = port;
        this.coin = coin;
        this.view = view;
    }

    @Override
    public void run() {
        try {
            Log.i(client, "new cliet " + port);
            socket = new Socket("127.0.0.1", port);
            BufferedReader reader = Utils.getReader(socket);
            PrintWriter writer = Utils.getWriter(socket);

            writer.println(coin);
            writer.flush();

            Log.i(client, "SEND " + coin);

            String info;

            while ((info = reader.readLine()) != null) {
                final String finalInfo = info;
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setText(finalInfo);
                    }
                });
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
