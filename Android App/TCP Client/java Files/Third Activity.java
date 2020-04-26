package com.example.androidclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThirdActivity extends AppCompatActivity {


    private TextView e1;

    private TextView ipp;
    private TextView pp;

    private int port;
    private String IP;

    private Button btn;
    private Button conn;
    private Button Opn;
    private Button GetFile;

    private ScrollView e2;
    private TextView tv;
    private TextView tRec;

    String response = "";

    private static Socket s;
    private static PrintWriter printWriter;

    String message = "";

    TcpClient mTcpClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        e1 = (TextView) findViewById(R.id.etIP);
        ipp = (TextView) findViewById(R.id.textView);
        pp = (TextView) findViewById(R.id.textView3);
        btn = (Button) findViewById(R.id.btnStop);
        tv = (TextView) findViewById(R.id.tv);
        tRec = (TextView) findViewById(R.id.tvRec);
        Opn = (Button)findViewById(R.id.button2);
        GetFile = (Button)findViewById(R.id.btnGET);


        //receives data from second activity
        final String IP = getIntent().getStringExtra("IP");
        final int port = getIntent().getIntExtra("PORT", 0);

        //shows a toast message
        Toast.makeText(ThirdActivity.this, IP, Toast.LENGTH_LONG).show();
        String txt = e1.getText().toString();

		ipp.setText(IP);

        pp.setText(String.valueOf(port));

		//returns dynamic information about the connection. if it is active then returns the IPv4 address of the device
        WifiManager wm = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        String localIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        e1.setText(localIP);

        Opn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				//launches a new activity
                Intent intent3 = new Intent(ThirdActivity.this, FourthActivity.class);
                startActivity(intent3);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				//if mTcpClient is started then starts stopClient method
                if (mTcpClient !=null){
                    mTcpClient.stopClient();
                }
            }
        });

        GetFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
					//executes ConnectTask() for empty parameters
                    new ConnectTask().execute("");
                    //sends the message to the server
                    if (mTcpClient != null) {
                        mTcpClient.sendMessage("Android Client is ready to Receive file");

                    }
                    //Storage permissions
                    final int REQUEST_EXTERNAL_STORAGE = 1;
                    String[] PERMISSIONS_STORAGE = {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };

                    /**
                     * Checks if the app has permission to write to device storage
                     * If the app does not has permission then the user will be prompted to grant permissions
                     *
                     * @param activity
                     */
     
                    //Check if the app has write permissions
                    int permission = ActivityCompat.checkSelfPermission(ThirdActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if(permission != PackageManager.PERMISSION_GRANTED){
                        //no permission so prompts the user
                        ActivityCompat.requestPermissions(ThirdActivity.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                    }
					//increases port number by 1
                    int p1 = port+1;
					//initializes a server socket with port number p1
                    ServerSocket serverSocket = new ServerSocket(p1);
						//allocates a new Thread calling the method ClientWorker() 
						//Listens for a connection to be made to this socket and accepts it
                        new Thread(new ClientWorker(serverSocket.accept())).start();
                } catch (IOException e) {
                    e.printStackTrace();
                    tv.setText(e.toString());
                }
            }
        });


    }
    public void send_text(View v) throws IOException {
		//checks if file in specified directory exists, if yes it deletes it
        File file = new File("/sdcard/download/Exprt_txt.txt");
        if(file.exists()){
            file.delete();
        }

        //gets an IPv4 address
        WifiManager wm = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        String localIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
		//executes ConnectTask() for empty parameters
        new ConnectTask().execute("");
        //sends the message to the server
        if(mTcpClient !=null){
            mTcpClient.sendMessage("TCP Connection Established with Android Client\r\n\tIP: "+localIP);
        }
        Toast.makeText(getApplicationContext(), "Data Sent", Toast.LENGTH_LONG).show();
    }

    public static class TcpClient{

        public final String TAG = TcpClient.class.getSimpleName();

        //message to send to the server
        private String mServerMessage;

        //sends message received notifications
        private OnMessageReceived mMessageListener = null;

        //while this is true the server will continue running
        private boolean mRun = false;

        //used to send messages
        private PrintWriter mBufferOut;

        //used to read messages from the server
        private BufferedReader mBufferIn;

        /**
         * Constructor of the class. OnMessageReceived listens for the messages received from server
         */
        public TcpClient(OnMessageReceived listener){
            mMessageListener = listener;
        }
		
        /**
        * Sends the message entered by client to the server
        * @param message txt entered by client
        */
        public void sendMessage(final String message){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(mBufferOut != null){
                        Log.d(TAG,"Sending: "+message);
                        mBufferOut.println(message);
                        mBufferOut.flush();
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
        }

        /**
         * Close the connection and release the members
         */
        public void stopClient(){
            mRun = false;
            if(mBufferOut !=null) {
                mBufferOut.flush();
                mBufferOut.close();
            }
            mMessageListener=null;
            mBufferIn =null;
            mBufferOut = null;
            mServerMessage = null;
        }

        public void run(String SERVER_IP, int SERVER_PORT) throws IOException {
            mRun = true;
            try{
                //uses server's IP Address entered in previous activity
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                Log.d("TCP Client", "Connecting...");
                //creates a socket to make the connection with the server
                Socket socket = new Socket(serverAddr,SERVER_PORT);
                try{
                    //sends the message to the server
                    mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                    //receives the message which the server sends back
                    mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //in this while the client listens for messages sent by the server
                    while(mRun){
                        mServerMessage = mBufferIn.readLine();
                        if (mServerMessage != null && mMessageListener != null){
                            //calls the method messageReceived from Activity class
                            mMessageListener.messageReceived("Server: "+mServerMessage);
                        }
                    }
                    Log.d("RESPONSE FROM SERVER","Server: '"+mServerMessage+"'");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally{
                    //the socket must be closed. It is not possible to reconnect to this socket
                    //after it is closed which means a new socket instance has to be created.
                    socket.close();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        //Declares the interface. The method messageReceived(String message) will must be implemented in the Activity
        //class task at on AsyncTask doInBackground
        private interface OnMessageReceived{
            void messageReceived(String message);
        }
    }

    public class ConnectTask extends AsyncTask<String,String,TcpClient>{
        TextView ip = (TextView)findViewById(R.id.textView);
        TextView bort = (TextView)findViewById(R.id.textView3);
        public final String SERVER_IP = ip.getText().toString();
        public final int SERVER_PORT = Integer.parseInt(bort.getText().toString());

        @Override
        protected TcpClient doInBackground(String... strings) {
            //creates a TCPClient object
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived(){

                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            try {
                mTcpClient.run(SERVER_IP,SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void  onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //response received from server
            Log.d("test","response" +values[0]);
            tv.setText(values[0]);
        }
    }
}

     class ClientWorker extends Activity implements Runnable {
		 
         private Socket target_socket;
         private DataInputStream din;
         private DataOutputStream dout;
         private TextView tv;
		 
         public ClientWorker(Socket recv_socket) throws IOException {

             target_socket = recv_socket;
			 //returns an input stream for this socket
             din = new DataInputStream(target_socket.getInputStream());
			 //returns an output stream for this socket
             dout = new DataOutputStream(target_socket.getOutputStream());
         }

         @Override
         public void run() {
             RandomAccessFile rw=null;
             long current_file_pointer=0;
             boolean loop_break=false;
             while (true) {
                 byte[] initialize = new byte[1];
                 try {
					 //reads input stream to bytes
                     din.read(initialize, 0, initialize.length);
                     if (initialize[0] == 2) {
						 //if byte read ==2 then reads next byte position from the stream
                         byte[] cmd_buff = new byte[3];
                         din.read(cmd_buff, 0, cmd_buff.length);
						 //reads received data from stream
                         byte[] recv_data = ReadStream();
                         switch(Integer.parseInt(new String(cmd_buff))){
                             case 125:
							 //initializes a random access file stream and writes the received file name to the specified directory
                                     rw = new RandomAccessFile("sdcard/download/"+ new String(recv_data), "rw");
									 //sends a data packet with command '126' and file pointer = 0
                                     dout.write(CreateDataPacket("126".getBytes("UTF8"), String.valueOf(current_file_pointer).getBytes("UTF8")));
                                     dout.flush();
                                 break;
                             case 127:
							 //Sets the file-pointer offset, measured from the beginning of this file
                                 rw.seek(current_file_pointer);
								 //Writes b.length bytes from the specified byte array to this file, starting at the current file pointer
                                 rw.write(recv_data);
								 //Returns the current offset in this file
                                 current_file_pointer = rw.getFilePointer();
								 //updates progress percentage
                                 System.out.println("Download percentage: "+((float)current_file_pointer/rw.length())*100+"%");
								 //sends a data packet with command '126' and the current file pointer
                                 dout.write(CreateDataPacket("126".getBytes("UTF8"),String.valueOf(current_file_pointer).getBytes("UTF8")));
                                 dout.flush();
                                 break;
                             case 128:
                                 if ("Close".equals(new String(recv_data))){
                                     loop_break=true;
                                 }
                                 break;
                         }

                     }
                     if (loop_break==true){
						 //closes the socket
                         target_socket.close();
                     }
                 }catch (IOException e) {
                     e.printStackTrace();
                     Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE,null,e);
                 }
             }
         }

         //reads integer from the stream and store in b/ if not equal to 4 then appends it to stream
         private byte[] ReadStream() throws IOException {
             byte[] data_buf = null;
             int b = 0;
             String buff_length = "";
             while ((b = din.read()) != 4) {
                 buff_length += (char) b;
             }
             int data_length = Integer.parseInt(buff_length);
             data_buf = new byte[data_length];
             int byte_read = 0;
             int byte_offset = 0;

             while (byte_offset < data_length) {
                 byte_read = din.read(data_buf, byte_offset, data_length - byte_offset);
                 byte_offset += byte_read;
             }
             return data_buf;
         }
		//creates an array of bytes to be sent to the client
         private byte[] CreateDataPacket(byte[] cmd,byte[] data) throws UnsupportedEncodingException {
             byte[] packet = null;

             byte[] initialize = new byte[1];
             initialize[0]=2;
             byte[] separator = new byte[1];
             separator[0]=4;
             byte[] data_length = String.valueOf(data.length).getBytes("UTF-8");
             packet = new byte[initialize.length+cmd.length+separator.length+data_length.length+data.length];

             System.arraycopy(initialize,0,packet,0,initialize.length);
             System.arraycopy(cmd,0,packet,initialize.length,cmd.length);
             System.arraycopy(data_length,0,packet,initialize.length+cmd.length,data_length.length);
             System.arraycopy(separator,0,packet,initialize.length+cmd.length+data_length.length,separator.length);
             System.arraycopy(data,0,packet,initialize.length+cmd.length+data_length.length+separator.length,data.length);
             return packet;
         }
     }
