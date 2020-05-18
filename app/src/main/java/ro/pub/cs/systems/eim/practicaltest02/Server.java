package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

public class Server extends Thread {

    int port;
    ServerSocket socket;
    HashMap<String, String> data;

    public Date time;

    static String server = "SERVER";

    boolean initial = true;

    public Server(int port) {
        this.port = port;

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        data = new HashMap<>();
        time = new Date();

        Log.i(server, "SERVER started " + port);
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                Socket s = socket.accept();
                Log.i(server, "New connection");
                Comm comm = new Comm(this, s);
                comm.start();;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        interrupt();

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void setData(String coin, String info) {
        data.put(coin, info);
    }

    public synchronized HashMap<String, String> getData() {
        return data;
    }

}
