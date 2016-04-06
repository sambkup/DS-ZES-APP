package com.example.jayasudha.myapp;

import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jayasudha on 3/31/16.
 */
public class NetworkClass {
    private LatLng currentPosition;
    private static Socket socket = null;
    private static ServerSocket listenSocket = null;
    static Object message = null;
    static Socket createClientSocket(String ip,int port){

        try{
            socket = new Socket(ip,port);

        }catch(Exception ex){
          System.err.print(ex);
            ex.printStackTrace();
        }
        if(socket==null){
            System.err.println("error in establishing client socket");
        }
        return socket;
    }
    static boolean sendRequest(LatLng currentPosition){
        currentPosition = currentPosition;
        String message = Double.toString(currentPosition.latitude)+","+Double.toString(currentPosition.longitude);
        OutputStream os = null;
        ObjectOutput oos = null;
        try{
            os = socket.getOutputStream();

        }catch(Exception ex){
            System.err.print("Socket with sensor node doesnt exist");
            return false;
        }
        try{
            oos = new ObjectOutputStream(os);
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
        try{
            oos.writeObject(message);
            oos.flush();
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;

    }
    private static Object receiveMessage(Socket socket){
        InputStream is = null;
        ObjectInputStream ois = null;

        try {
            is = socket.getInputStream();
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        try{
            ois = new ObjectInputStream(is);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        try{
            message = ois.readObject();

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return message;

    }
    static ServerSocket createServerSocket(int port){

        try{
            listenSocket = new ServerSocket(port);

        }catch(Exception ex){
            System.err.print(ex);
            ex.printStackTrace();
        }
        if(listenSocket == null){
            System.err.print("error in establishing server socket");
        }
        //start a listenerthread
        return listenSocket;
    }

    static class ListenThread implements Runnable{
        public void run(){
            Socket serverSocket = null;
            if(NetworkClass.listenSocket==null){
                System.err.print("no listen socket setup");
                return;
            }
            while(true){
                try{
                    serverSocket = listenSocket.accept();

                }catch(Exception ex){
                    ex.printStackTrace();
                }
                ReceiveThread newReceiver = new ReceiveThread(serverSocket);
                Thread receiveThread = new Thread(newReceiver);
                receiveThread.start();


            }

        }
    }
    static class ReceiveThread implements Runnable{
        private Socket listenSocket;

        private ReceiveThread(Socket socket){
            listenSocket = socket;
        }
        public void run(){
            while(true){

                    Object receivedMessage = NetworkClass.receiveMessage(listenSocket);
                    if(receivedMessage==null){
                        continue;
                    }
                return;


            }

        }


    }


}
