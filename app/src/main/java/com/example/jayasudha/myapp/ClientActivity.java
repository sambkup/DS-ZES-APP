package com.example.jayasudha.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


import com.example.jayasudha.myapp.process.TestBench;
import com.google.android.gms.maps.model.LatLng;

import junit.framework.Test;

import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;



public class ClientActivity extends AppCompatActivity {
    static String jsonRoute;
    private Socket client;
    private PrintWriter printWriter;
    private String pos;
    private String dest;
    private Button button;
    private String message;
    private LatLng position;


    static ClientActivity clientActivity;
    public Send send;
    final static String PUBLIC_STATIC_STRING_IDENTIFIER = "jsonObject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientActivity = this;
        send = new Send();
        pos = null;
        dest = null;

        setContentView(R.layout.activity_client);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    pos = extras.getString("currentPosition");
                    dest = extras.getString("destinationLocation");
                    System.out.println("position " + pos);
                    message = pos;
                    String[] coordinates = pos.split(",");
                    Double lat = Double.parseDouble(coordinates[0]);
                    Double lon = Double.parseDouble(coordinates[1]);
                    position = new LatLng(lat, lon);
                }

                System.out.println("creating send object");
                Send sendObj = new Send();
                sendObj.execute();
               /* Intent newSocketIntent = new Intent(v.getContext(),
                        SocketActivity.class);
                startActivityForResult(newSocketIntent, 0);
                finish();
*/


            }
        });

    }

    public static ClientActivity getInstance() {
        return clientActivity;
    }

    public Send getSendInstance() {
        return this.send;
    }

    public void setJSONObjectToSendInstance(JSONObject jsonObject) {
        send.setJsonObject(jsonObject);
    }

    class Send extends AsyncTask<Void, Void, Void> implements Serializable {


        JSONObject jsonObject = new JSONObject();

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        public void setJsonObject(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        protected Void doInBackground(Void... params){
            //get destloc and latlng from mapsactivity
            //make an object of class testbench, pass destination and latlng to it
            //wait till a valid JSON is obtained from startnode
            //switch back to maps and display it
            System.out.println("Inside client activity, my position is " + pos + "destination location is  " + dest);
            TestBench test = new TestBench();
            test.testBench(pos,dest);

            try {
                while (TestBench.finalRoute==null) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }
                }
            }catch(Exception ex){
                ex.toString();
            }
            System.out.println("RECEIVED JSONOBJECT");
            System.out.println(TestBench.finalRoute.toString());
            Intent resultIntent = new Intent();
            resultIntent.putExtra(PUBLIC_STATIC_STRING_IDENTIFIER, TestBench.finalRoute.toString());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();

/*
            try {
                client = new Socket("192.168.0.18", 1029);
            }catch (Exception ex){
                ex.printStackTrace();
            }


                try {

                    InputStream is = null;
                    ObjectInputStream ois = null;

                    while (true) {
                        printWriter = new PrintWriter(client.getOutputStream());
                        printWriter.write(message);
                        System.out.println("sending message");
                        printWriter.flush();

                        Boolean receiveFlag = false;
                        try {
                            is = client.getInputStream();

                        } catch (Exception ex) {
                            System.err.println(ex.toString());
                            ex.printStackTrace();
                        }
                        try {
                            ois = new ObjectInputStream(is);
                        } catch (Exception ex) {
                            System.err.println(ex.toString());
                            ex.printStackTrace();
                        }
                        try {

                            String s = (String) ois.readObject();
                            System.out.print(s);
                            jsonObject = new JSONObject(s);
                             if (jsonObject == null) {
                                System.err.println("null json");

                                break;
                            } else {
                                System.out.println(jsonObject.toString());
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(PUBLIC_STATIC_STRING_IDENTIFIER, jsonObject.toString());
                                setResult(Activity.RESULT_OK, resultIntent);
                                printWriter.close();
                                ois.close();
                                is.close();
                                client.close();
                                finish();

                                break;

                            }

                        } catch (Exception ex) {
                            System.out.println("error in reading json");
                         //   System.err.println(ex.toString());
                          //  ex.printStackTrace();
                            break;
                        }
                    }
                    printWriter.close();
                    ois.close();
                    is.close();
                    client.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;

            }*/
            return null;

        }

    }

}
