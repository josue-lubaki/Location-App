package ca.josue.locationapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private Button btnMyLocation;
    private TextView tv_latitude;
    private TextView tv_longitude;
    private TextView tv_latitudeInstant;
    private TextView tv_longitudeInstant;

    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    /** Le Callback de la methode UpdateLocation */
    private final LocationCallback mLocationCallback = new LocationCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                tv_latitudeInstant.setText("Latitude : " + location.getLatitude());
                tv_longitudeInstant.setText("Longitude : " + location.getLongitude());
                Log.i("XXXX", "Latitude_Instant: " + location.getLatitude() + "\tLongitude_Instant : " + location.getLongitude());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMyLocation = findViewById(R.id.myLocation);
        btnMyLocation.setOnClickListener(this::seeMyLocation);

        tv_latitude = findViewById(R.id.Latitude);
        tv_longitude = findViewById(R.id.Longitude);
        tv_latitudeInstant = findViewById(R.id.LatitudeInstant);
        tv_longitudeInstant = findViewById(R.id.LongitudeInstant);

        getLocationPermission();

        if (mLocationPermissionGranted) {

            LocationSettingsRequest.Builder builder =
                    new LocationSettingsRequest.Builder()
                            .addLocationRequest(createLocationRequest());

            SettingsClient client =
                    LocationServices.getSettingsClient(this);

            Task<LocationSettingsResponse> task =
                    client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this,
                    response -> {
                        // demander la localisation ici
                        startLocationUpdates();
                    });

            task.addOnFailureListener(this, e -> {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                205);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            });

        }
    }

    /**
     * Methode permettant de demander à voir ma localisation
     */
    private void seeMyLocation(View view) {
        if (!mLocationPermissionGranted)
            getLocationPermission();
        else
            retrieveLocation();
    }

    /**
     * La methode qui permet de créer la requete d'une nouvelle demande de position
     * */
    protected LocationRequest createLocationRequest() {
        mLocationRequest =
                new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest
                .PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, null);
    }

    /**
     * La methode qui permet de stopper l'écoute au callBack du update
     * */
    private void stopLocationUpdate() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    /**
     * Methode Permettant de demander la Permission de Localisation
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else
            askPermissionUser();
    }


    /**
     * Methode permettant de demander la permission d'activer la localisation à l'Utilisateur
     */
    private void askPermissionUser() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                205);
    }


    /**
     * Lancer par @Method {askPermissionUser}
     * La methode sera demarrée (ou qui réagit) si la permission n'avait pas été donné ou activer
     */
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
                    retrieveLocation();
                }
            }
        }
    }


    /**
     * Methode permettant d'aller chercher la Localisation du mobile au click du Boutton
     */
    @SuppressLint("SetTextI18n")
    protected void retrieveLocation() {
        FusedLocationProviderClient mFLClient = LocationServices
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
                                Log.i("XXXX", "\nLatitude : " + location.getLatitude() + "\tLongitude : " + location.getLongitude());
                            }
                        });

    }
}