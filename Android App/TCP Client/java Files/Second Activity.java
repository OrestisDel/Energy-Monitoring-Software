package com.example.androidclient;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.net.Socket;

public class SecondActivity extends AppCompatActivity {
    private EditText m_ipaddressTextbox = null;
    private Socket m_socket = null;
    private EditText m_port = null;
    private SensorManager m_sensorManger = null;
    private Sensor m_sensor = null;
    private Button conn;
    private Button Logout;
    private TextView ClientLog;
    private String str1;

    private Socket socket;
    private String SendString;
    public boolean sendDone = true;
    public TextView debugTV;

    public Handler UIHandler;
    public Thread Thread1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
		//catches accidental disk or network access on the application's main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_ipaddressTextbox = (EditText) findViewById(R.id.IPAddressTextbox);
        m_port = (EditText) findViewById(R.id.etPort);
        conn = (Button) findViewById(R.id.btn2);
        Logout = (Button) findViewById(R.id.btn3);
        ClientLog = (TextView) findViewById(R.id.tvConn);

        ClientLog.setText("");

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				//if Logout button is clicked then returns to previous activity
                Intent intent3 = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(intent3);
            }
        });


        conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				//if connect button is clicked calls sendData()
                ClientLog.setText("Loading...");
                sendData();
            }
        });
    }

    private void sendData() {
		//splits IP address string entered
        String iptext = m_ipaddressTextbox.getText().toString().split(":")[0];
        int port = Integer.parseInt(m_port.getText().toString());
		//launches new activity, binding ip and port entered to it
        Intent intent2 = new Intent(SecondActivity.this, ThirdActivity.class);
        intent2.putExtra("IP", iptext);
        intent2.putExtra("PORT", port);
        this.startActivity(intent2);
    }
}
