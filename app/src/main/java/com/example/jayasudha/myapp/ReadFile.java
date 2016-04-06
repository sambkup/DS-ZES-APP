package com.example.jayasudha.myapp;

import android.content.res.AssetManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by jayasudha on 3/26/16.
 */
public class ReadFile {
    public static ArrayList<LatLng> readFile(BufferedReader br){

        ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
        try {


            String line;


           // FileReader fileReader = new FileReader(file);
          //  BufferedReader br = new BufferedReader(fileReader);

            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] latlon = line.split(",");
                double lat = Double.parseDouble(latlon[0]);
                double lon = Double.parseDouble(latlon[1]);
                System.out.println(latlon[0]+" "+latlon[1]);

                LatLng latLngObj = new LatLng(lat,lon);
                latLngs.add(latLngObj);

            }

        } catch (Exception e) {
            System.out.println(e);
            System.out.println(System.getProperty("user.dir"));
        }
        return latLngs;
    }

}
