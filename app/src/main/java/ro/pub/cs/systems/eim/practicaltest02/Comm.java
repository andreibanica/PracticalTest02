package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Comm extends Thread {

    Server server;
    Socket socket;

    static String comm = "COMM";

    public Comm(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            Log.i(comm, "NEW CHANNEL");

            BufferedReader reader = Utils.getReader(socket);
            PrintWriter writer = Utils.getWriter(socket);

            String coin = reader.readLine();

            HashMap<String, String> data = server.getData();

            HttpClient httpClient = new DefaultHttpClient();
            String page;


            HttpGet httpGet = new HttpGet("https://api.coindesk.com/v1/bpi/currentprice/EUR.json");
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            page = EntityUtils.toString(entity);

            JSONObject content = new JSONObject(page);

            JSONObject time = content.getJSONObject("time");

            // "May 18, 2020 05:17:00 UTC",

            String parsed = time.getString("updated").substring(13, 21);



            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date date = format.parse(parsed);
            date.setHours(date.getHours() + 3);
            Log.i("DATA", date.toString());

            JSONObject bpi = content.getJSONObject("bpi");
            JSONObject usd = bpi.getJSONObject("USD");
            JSONObject eur = bpi.getJSONObject("EUR");

            String euro = eur.getString("rate");
            String dollars = usd.getString("rate");

            Date update = server.time;
            Log.i(comm, "SERVER time " + update.toString());
            String result;

            if (server.initial) {
                server.initial = false;
                server.time = date;
                server.setData("eur", euro);
                server.setData("usd", dollars);
                Log.i(comm, "INITIAL");
            }

            if (update.getMinutes() < date.getMinutes()) {
                Log.i(comm, "new dataset");
                server.setData("eur", euro);
                server.setData("usd", dollars);
                server.time = date;

                if (coin.equals("eur")) {
                    result = euro;
                } else {
                    result = dollars;
                }
            } else {
                Log.i(comm, "FROM CACHE");
                if (coin.equals("eur")) {
                    result = data.get("eur");
                } else {
                    result = data.get("usd");
                }
            }

            writer.println(result);
            writer.flush();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
