package utd.cs.contactmanagerp1;

/* The mapActivity will get the address from second activity and find the geolocation
    the mapActivity will locate the address and return the lat and long from the address to be stored with the contact
    Using geocoder we can extract latitude and longitude of the address and display it on the map
    Using GoogleApiClient we can check for permissions and locate the device location and from there on calculate the distance between the
    two locations and extract info from them.
    Written by Ahmad Ghafori
 */


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
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

import java.io.IOException;
import java.util.List;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class mapActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    //Variables
    private GoogleMap map;
    double lat, lng, currentLat, currentLng;
    LatLng loc, newLoc;
    private Location mLocation;
    private Location newLocation;
    private Marker currentLocationMarker;
    String strAddress;
    String result;
    Geocoder coder;
    List<Address> addresses;
    private static final String TAG = "MapActivity";
    Button mapButton;

    int LOCATION_REFRESH_TIME = 1000;
    int LOCATION_REFRESH_DISTANCE = 5;
    private static final int Request_User_Permission_Code = 99;

    private FusedLocationProviderClient FusedLocationClient;
    private LocationRequest locationRequest;
    private GoogleApiClient mGoogleApiClient;
    //Permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //math vars
    static double PI_RAD = Math.PI / 180.0;
    double phi1 = 0.0, phi2 = 0.0, lam1 = 0.0, lam2 = 0.0;
    double dist = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }

        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

        }
        FusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapButton = findViewById(R.id.mapButton);
        mapButton.setVisibility(View.INVISIBLE);

        //start intent
        final Intent intent = getIntent();

        //get address from intent
        strAddress = intent.getStringExtra("address");

        Toast.makeText(this, strAddress, Toast.LENGTH_LONG).show();

        String address = "9025 Roanoke Ct, Mckinney, Texas, 75071";

        coder = new Geocoder(getApplicationContext());
        //send the address through geocoder to get the lat and long values
        try {
            addresses = coder.getFromLocationName(strAddress, 1);
            if (addresses == null) {
                //if no address is added and the Map location was pressed
                Toast.makeText(this, "No Address available", Toast.LENGTH_LONG).show();
            }
            Address location = addresses.get(0);

            //get lat and long values
            lat = location.getLatitude();
            lng = location.getLongitude();
            newLocation = new Location("");
            newLocation.setLatitude(lat);
            newLocation.setLongitude(lng);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //check user permission
    public boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String []{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Permission_Code);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String []{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Permission_Code);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case Request_User_Permission_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (mGoogleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    //Build Google API Client
    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    //display map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }

        loc = new LatLng(lat, lng);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //location of address entered
        map.addMarker(new MarkerOptions().position(loc)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 10.0f));
        mapButton.setVisibility(View.VISIBLE);


    }

    //Option menu
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_mapAddy) {

            //calculate distance
            phi1 = lat * PI_RAD;
            phi2 = currentLat * PI_RAD;
            lam1 = lng * PI_RAD;
            lam2 = currentLng * PI_RAD;

            dist = 6371.01 * acos(sin(phi1) * sin(phi2) * cos(phi1) * cos(phi2) * cos(lam2 - lam1));

            Toast.makeText(this, "You are " + dist + "miles away.", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }*/

    //Get current location
    @Override
    public void onLocationChanged(Location location) {
        //current location
        mLocation = location;

        if (currentLocationMarker != null)
        {
            currentLocationMarker.remove();
        }
        //set up latlng
        newLoc = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

        //get lat and lng values for dist calculation
        currentLat = mLocation.getLatitude();
        currentLng = mLocation.getLongitude();

        //add marker with position of current location
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(newLoc);
        markerOptions.title("Current User Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        currentLocationMarker = map.addMarker(markerOptions);

        if(mGoogleApiClient != null)
        {
            //deprecated but i could not get anything else to work
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
//when googleapi is connected create location request
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, (LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //on click the distance will be calculated and sent back through intent.
    //toast will display the lat and long of the address location
    public void mapClick(View view) {
        Intent intent = new Intent();
        //calculate distance
        phi1 = currentLat * PI_RAD;
        phi2 = lat * PI_RAD;
        lam1 = currentLng * PI_RAD;
        lam2 = lng * PI_RAD;

        double distanceInMeters = (double) mLocation.distanceTo(newLocation);
        //convert from meters to miles
        double distanceInMiles = (distanceInMeters / 1000) / 1.6;
        result = String.valueOf(distanceInMiles);

        //display Lat and Long of entered address
        Toast.makeText(this, "Lat: " + lat + ", Long: " + lng, Toast.LENGTH_LONG).show();

        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        //send back the distance to secondActivity
        intent.putExtra("dist", distanceInMiles);
        setResult(RESULT_OK, intent);
        finish();

    }
}