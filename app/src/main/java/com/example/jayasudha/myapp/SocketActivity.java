package com.example.jayasudha.myapp;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;


public class SocketActivity extends AppCompatActivity {
    LatLng position = null;
    private TextView myText = null;
    private TextView mesg = null;
    private TextView info = null;
    private TextView ip = null;
    static private ServerSocket serverSocket = null;
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        mesg = (TextView) findViewById(R.id.message);
        info = (TextView) findViewById(R.id.info);
        ip = (TextView) findViewById(R.id.ip);
        mesg.setText("Socket activity started");
        ip.setText(getIP());
        //create a serversocket
        //create a clientsocket with closest node
        //start a listener thread
        //when a message is received, goback to mapActivity
        /*NetworkClass.createServerSocket(8080);
        Thread listenThread = new Thread(new NetworkClass.ListenThread());
        listenThread.start();

        Socket clientSocket = NetworkClass.createClientSocket("10.237.84.244",1025);
        NetworkClass.sendRequest(position);
        while(true){
            if(NetworkClass.message==null){
                continue;
            }
            else {
                //pass message to mapsactivity
                this.finish();
            }*/
        Thread socketServerThread = new Thread(new SocketServerThread());


        socketServerThread.start();
    }

    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread

    {
        static final int SocketServerPORT = 8080;

        public void run() {
            Socket socket = null;

            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                if(serverSocket==null){
                    System.err.println("null server socket");
                }
                SocketActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        info.setText("Waiting here : " + serverSocket.getLocalPort());
                    }
                });
                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    String messageFromClient = "";
                    //if no message received, block the program
                    messageFromClient = dataInputStream.readUTF();
                    message = "from" + socket.getInetAddress() + ":" + socket.getPort() + "\n message from client" + messageFromClient + "\n";
                    SocketActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mesg.setText(message);
                        }
                    });
                    String sendMessage = "Received Message at android";
                    dataOutputStream.writeUTF(sendMessage);
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
                SocketActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mesg.setText(ex.toString());
                    }
                });
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private String getIP(){
        String ip = "";
        try{
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(enumNetworkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()){
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if(inetAddress.isSiteLocalAddress()){
                        ip = ip+"SiteLocalAddress: "+inetAddress.getHostAddress()+" \n";
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();

        }
        return ip;


    }


}
