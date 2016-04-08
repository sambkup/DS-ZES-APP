package com.example.jayasudha.myapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,LocationListener {

    private GoogleMap mMap;
    private LatLng latLng;
    private Marker marker;
    Geocoder geocoder;
    static final int STATIC_INTEGER_VALUE = 1;
    static final String PUBLIC_STATIC_STRING_IDENTIFIER = "jsonObject";
    static String destination;


    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        geocoder = new Geocoder(this);
      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mMap != null) {
            Toast.makeText(MapsActivity.this,"calling setupmap ", Toast.LENGTH_LONG).show();
            setUpMap();
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }



    private void setUpMap() {

     //   Toast.makeText(MapsActivity.this,"inside setupmap ", Toast.LENGTH_LONG).show();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {


            @Override
            public void onMapClick(LatLng point) {
                //save current location
                LatLng latLng = point;
                String message = String.valueOf(point.latitude) + "," + String.valueOf(point.longitude);
                Toast.makeText(MapsActivity.this, message, Toast.LENGTH_LONG).show();
                //set destination
                destination = message;

                 //remove previously placed Marker
                if (marker != null) {
                    marker.remove();
                }

                //place marker where user just clicked
                marker = mMap.addMarker(new MarkerOptions().position(point).title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location


        latLng = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

    }
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        Toast.makeText(this, "on map ready", Toast.LENGTH_SHORT).show();
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        mMap.setMyLocationEnabled(true);


        mMap.getUiSettings().setZoomControlsEnabled(true);


        /*get lat lon values*/
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria,true);

        Location myLocation = locationManager.getLastKnownLocation(provider);
        int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1000;

        //check validity
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        if(myLocation!=null){
            onLocationChanged(myLocation);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
        double latitude = myLocation.getLatitude();

        double longitude = myLocation.getLongitude();

        Context context = getApplicationContext();
        CharSequence text = new StringBuilder().append(String.valueOf(latitude)).append(" ").append(String.valueOf(longitude)).toString();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();



    }

    private void enableMyLocation() {
        Toast.makeText(this, "enableMyLocation", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.

            int LOCATION_PERMISSION_REQUEST_CODE = 1;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);



        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
    public void getRoute(View view)throws IOException{
        Intent startClientActivity = new Intent(this, ClientActivity.class);
        String currentPos = Double.toString(latLng.latitude)+","+Double.toString(latLng.longitude);
        startClientActivity.putExtra("currentPosition",currentPos);
        startClientActivity.putExtra("destinationLocation",destination);
        startActivityForResult(startClientActivity,STATIC_INTEGER_VALUE);
    }
    public void displayRouteFromJSON(String jsonObject)throws IOException,JSONException{
        System.out.println("inside displayRoute");
        JSONObject pointsJSON = null;
        try {
            pointsJSON = new JSONObject(jsonObject);
        }catch (Exception ex){
            System.out.println("error in displayRouteFromJSON in creating JSONOBJ");
          //  ex.printStackTrace();
        }
        Iterator<String> keys = pointsJSON.keys();
        ArrayList<LatLng> latLngs = new ArrayList<>();

        while( keys.hasNext() ) {
            String key = (String)keys.next();
            System.out.println("key"+key);

                String coordinates = pointsJSON.get(key).toString();
                String latlng[] = coordinates.split(",");
                Double lat = Double.parseDouble(latlng[0]);
                Double lng = Double.parseDouble(latlng[1]);
                LatLng coordinate = new LatLng(lat,lng);

                latLngs.add(coordinate);
                System.out.println("latlngs "+coordinate.toString());



        }
        LatLngBounds.Builder b = new LatLngBounds.Builder();



        for(LatLng eachPoint : latLngs){
            MarkerOptions options = new MarkerOptions().position( eachPoint );
            b.include(eachPoint);
            options.icon(BitmapDescriptorFactory.defaultMarker());
            mMap.addMarker(options);


        }
        LatLngBounds bounds = b.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100,100,5);
        mMap.animateCamera(cu);

        Polyline line = mMap.addPolyline(new PolylineOptions().addAll(latLngs).width(7).color(Color.RED));
        line.setPoints(latLngs);




    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        String jsonObject = null;
        System.out.println("inside onActivityResult");
        switch(requestCode) {
            case (STATIC_INTEGER_VALUE) : {
                if (resultCode == Activity.RESULT_OK) {
                    System.out.println("result code is Ok");
                    jsonObject = data.getStringExtra(PUBLIC_STATIC_STRING_IDENTIFIER);
                    try {
                        displayRouteFromJSON(jsonObject);
                    }catch(Exception ex){
                        System.out.println("error in calling displayRoutFromJSON");
                      //  ex.printStackTrace();
                    }
                }
                break;
            }
        }
        try {
            System.out.println("calling display function");
            displayRouteFromJSON(jsonObject);
        }catch(Exception ex){
            System.out.println("error in calling function");
            ex.printStackTrace();
        }
    }


}
