package com.indooratlas.android.sdk.examples.Activities;

/**
 * Created by Daniel on 05/05/2016.
 */
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.indooratlas.android.sdk.examples.Adapter.ExampleAdapter;
import com.indooratlas.android.sdk.examples.R;
import com.indooratlas.android.sdk.examples.imageview.ImageViewActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates = true;

    int cont = 0;
    String myString;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if (savedInstanceState==null){
            Bundle extras = getIntent().getExtras();
            if (extras==null){
                myString=null;
            }
            else
                myString=extras.getString("StringIneed");
        }

        Toast.makeText(getApplicationContext(), myString, Toast.LENGTH_SHORT).show();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
        /////////////WIN?//////////////////
        //new HttpRequest().execute("https://maps.googleapis.com/maps/api/directions/json?origin=Aalto+University&destination="+60.21888+","+24.81300+"&mode=walking&key=AIzaSyBt-apG9orMv4393_zm4vX8mXpMA1glRMA"); // Aqui hay que poner el destino
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // Toast.makeText(getApplicationContext(), "Map Ready", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));

            LatLng currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            marker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_blue_dot)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, (float) 15));
            //Toast.makeText(getApplicationContext(), menu_activity.str_goal, Toast.LENGTH_SHORT).show();
            if (myString != null){ // Vas fuera del Sello
                new HttpRequest().execute("https://maps.googleapis.com/maps/api/directions/json?origin="+currentLatLng.latitude+","+currentLatLng.longitude+"&destination="+myString+"&mode=walking&key=AIzaSyBt-apG9orMv4393_zm4vX8mXpMA1glRMA"); // Aqui hay que poner el destino
                //menu_activity.str_goal = null;
            }
            else{// Vas al Sello
                new HttpRequest().execute("https://maps.googleapis.com/maps/api/directions/json?origin="+currentLatLng.latitude+","+currentLatLng.longitude+"&destination="+60.21888+","+24.81300+"&mode=walking&key=AIzaSyBt-apG9orMv4393_zm4vX8mXpMA1glRMA"); // Aqui hay que poner el destino
            }


        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        //mSensorManager.unregisterListener(this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        //mSensorManager.registerListener( this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onLocationChanged(Location location) {

        marker.remove();
        mCurrentLocation = location;
        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_blue_dot)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, (float) 15));

        if ( MainActivity.SELLO && cont<1) {

            cont++;

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("You are in Sello!\nDo you want to continue with indoor guidance?");

            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    Intent browserIntent = new Intent(MapsActivity.this, ImageViewActivity.class );
                    startActivity(browserIntent);
                    finish();
                    //Toast.makeText(MapsActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                }
            });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            }

    }

    class HttpRequest extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strUrl) {

            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(strUrl[0]);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while( ( line = br.readLine()) != null){
                    sb.append(line);
                }

                data = sb.toString();
                br.close();

            }catch(Exception e){
                Toast.makeText(getApplicationContext(), "Exception", Toast.LENGTH_LONG).show();
            }finally{
                //Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                if (urlConnection != null) urlConnection.disconnect();
            }

            ////JSON
            List<List<HashMap<String, String>>> routes = null;
            try {

                JSONObject jsonObject = new JSONObject(data);

                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jsonObject);

            } catch (Exception eJ){
                Toast.makeText(getApplicationContext(), "JSON Exception", Toast.LENGTH_LONG).show();
            }
            ////////

            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points_dir = null;
            PolylineOptions lineOptions = new PolylineOptions();

            for(int i=0;i<result.size();i++) {
                points_dir = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points_dir.add(position);

                }

                lineOptions.addAll(points_dir);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
            }

            //Paint something

            mMap.addPolyline(lineOptions);

        }

        @Override
        protected void onPreExecute() {}

    }

}