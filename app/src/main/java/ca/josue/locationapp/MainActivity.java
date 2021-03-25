package ca.josue.locationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private Button btnMyLocation;
    private TextView tv_latitude;
    private TextView tv_longitude;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFLClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMyLocation = findViewById(R.id.myLocation);
        btnMyLocation.setOnClickListener(this::seeMyLocation);

        tv_latitude = findViewById(R.id.Latitude);
        tv_longitude = findViewById(R.id.Longitude);

    }

    /**
     * Methode permettant de demander à voir ma localisation
     * */
    private void seeMyLocation(View view) {
        if(!mLocationPermissionGranted)
            getLocationPermission();
        else
            retrieveLocation();
    }


    /**
     * Methode Permettant de demander la Permission de Localisation
     * */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            retrieveLocation();
        }
        else
            askPermissionuser();
    }

    /**
     * Methode permettant de demander la permission d'activer la localisation à l'Utilisateur
     * */
    private void askPermissionuser(){
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                205);
    }


    /**
     * La methode sera demarrée si la permission n'avait pas été donné ou activer
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case 205: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    // on va demander la localisation
                    retrieveLocation();
                }
            }
        }
    }


    /**
     * Methode permettant d'aller chercher la Localisation du mobile
     * */
    @SuppressLint("SetTextI18n")
    protected void retrieveLocation() {
        mFLClient = LocationServices
                .getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    205);
            return;
        }

        mFLClient.getLastLocation()
                .addOnSuccessListener(this,
                        location -> {
                            if (location != null) {
                                // Logic to handle location object
                                tv_latitude.setText("Latitude : " + location.getLatitude());
                                tv_longitude.setText("Longitude : " + location.getLongitude());
                            }
                        });


    }
}