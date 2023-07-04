package com.example.super_cep.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalisationProvider {


    private Activity activity;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Address address;
    private static int locationRequestCode = 1000;

    private List<LocalisationProviderListener> listeners;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public static void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                locationRequestCode);
    }
    public LocalisationProvider(Activity activity) {
        this.activity = activity;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        listeners = new ArrayList<>();
        setupFuseLocationClient();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationRequest =
                    new LocationRequest.Builder(
                            LocationRequest.QUALITY_HIGH_ACCURACY
                    ).build();
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        onNewLocation(location);
                    }
                }
            }
        };

    }

    private void onNewLocation(Location location){
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        // Utilisez les coordonnées comme vous le souhaitez
        Geocoder geocoder = new Geocoder(activity, Locale.FRANCE);

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0);
            Log.i("LocalisationProvider", "Localisation changed: " + getLocalisation());
            for (LocalisationProviderListener listener : listeners) {
                listener.onLocalisationChanged(address);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerListener(LocalisationProviderListener listener) {
        listeners.add(listener);
    }

    @SuppressLint("MissingPermission")
    private void setupFuseLocationClient() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // La dernière localisation connue peut être nulle
                        if (location != null) {
                            onNewLocation(location);
                        }
                    }
                });
    }

    public Address getAddress() {
        return address;
    }

    public String getLocalisation(){
        if(address == null){
            return null;
        }

        String formattedAddress = String.format("%s, %s, %s, %s",
                address.getAddressLine(0),  // Renvoie le premier élément de l'adresse: généralement le numéro de la rue et le nom de la rue.
                address.getLocality(),  // Renvoie la ville
                address.getAdminArea(),  // Renvoie l'état, la province ou la région, selon le pays
                address.getCountryName());  // Renvoie le pays
        return formattedAddress;
    }
}
