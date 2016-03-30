package com.example.jayasudha.myapp;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by jayasudha on 3/26/16.
 */
public class ReadFile {
    public ArrayList<LatLng> readFile(String fileName){
        File file = new File(fileName);
        ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
        try {
            file = new File("jazz.txt");

            String line;

            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] latlon = line.split(",");
                int lat = Integer.parseInt(latlon[0]);
                int lon = Integer.parseInt(latlon[1]);
                System.out.println(latlon[0]+" "+latlon[1]);

                LatLng latLngObj = new LatLng(lat,lon);
                latLngs.add(latLngObj);

            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return latLngs;
    }

}
