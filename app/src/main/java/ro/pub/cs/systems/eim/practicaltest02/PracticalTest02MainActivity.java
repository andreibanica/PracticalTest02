package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PracticalTest02MainActivity extends AppCompatActivity {

    EditText serverPort;
    Button connect;

    EditText clientPort;
    EditText coin;
    Button request;

    TextView result;

    Server server;
    Client client;

    ConnectButtonListener connectButtonListener =new ConnectButtonListener();
    private class ConnectButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            String port = serverPort.getText().toString();
            server = new Server(Integer.parseInt(port));
            server.start();
        }
    }

    RequestListener requestListener = new RequestListener();
    class RequestListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            String port = clientPort.getText().toString();
            String c = coin.getText().toString();

            result.setText("");

            client = new Client(Integer.parseInt(port), c, result);
            client.start();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPort = findViewById(R.id.server_port);
        connect = findViewById(R.id.connect);
        connect.setOnClickListener(connectButtonListener);

        clientPort = findViewById(R.id.client_port);
        coin = findViewById(R.id.coin);
        request = findViewById(R.id.request);
        request.setOnClickListener(requestListener);

        result = findViewById(R.id.result);
    }

    @Override
    protected void onDestroy() {
        if (server != null) {
            server.stopThread();
        }
        super.onDestroy();
    }
}
