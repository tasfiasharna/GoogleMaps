package com.tasfia.googlemaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tasfia.googlemaps.DirectionHelpers.TaskLoadedCallback;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    //google map object
    private GoogleMap mMap;
    Button getDirection;
    private MarkerOptions cwakBazar, agrabad;
    private Polyline currentPolyline;

    List<MarkerOptions> markerOptionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        getDirection = findViewById(R.id.btnGetDirection);
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*new FetchURL(MainActivity.this).execute(getUrl(cwakBazar.getPosition(), agrabad.getPosition(), "driving"), "driving");*/
                String cost =  getCeilValue(new LatLng(22.352148, 91.835520), new LatLng(22.326223, 91.813346)) + " Tk";
                Toast.makeText(MapsActivity.this, cost, Toast.LENGTH_SHORT).show();
                Log.e("Hell", cost);
            }
        });

        cwakBazar = new MarkerOptions().position(new LatLng(22.352148, 91.835520)).title("CwakBazar");
        agrabad = new MarkerOptions().position(new LatLng(22.326223, 91.813346)).title("Agrabad");

        markerOptionsList.add(cwakBazar);
        markerOptionsList.add(agrabad);

        MapFragment mapFragment=(MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.addMarker(cwakBazar);
        mMap.addMarker(agrabad);
        showAllMarkers();

    }

    public double getCeilValue(LatLng from, LatLng to) {
        int Radius = 6371;// radius of earth in Km
        double fromLat = from.latitude;
        double toLat = to.latitude;
        double fromLng = from.longitude;
        double toLng = to.longitude;
        double dLat = Math.toRadians(toLat - fromLat);
        double dLon = Math.toRadians(toLng - fromLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(fromLat))
                * Math.cos(Math.toRadians(toLat)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double distance = valueResult / 1; //Km
        double baseFare = 15;
        double travelCost = baseFare * distance;
        return Math.ceil(travelCost);
    }

    private void showAllMarkers() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (MarkerOptions m : markerOptionsList) {
            builder.include(m.getPosition());
        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cameraUpdate);

    }

    private String getUrl(LatLng origin, LatLng destination, String directionmode) {
        String str_origin = "origin"+origin.latitude+","+origin.longitude;

        String str_dest = "destination"+destination.latitude+","+destination.longitude;

        String mode = "mode"+directionmode;

        String parameter = str_origin+"&"+str_dest+"&"+mode;
        String format = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+format+"?"+parameter+"&key=AIzaSyBBFx8euxkwLd0CuflQYr77kElGwVTK62M";

        return url;

    }

    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null) {
            currentPolyline.remove();

            currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        }
    }
}
