package com.samples.location.mapzoom;

import android.location.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapViewActivity extends AppCompatActivity
        implements OnMapReadyCallback{

    private static final int ZOOM_MAX = 21;
    private static final int ZOOM_INIT = 17;

    private TextView text;
    private TextView zoom;
    private SupportMapFragment map;
    private SeekBar seek;
    private LatLng point;
    private Location location;

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {
            location = loc;
            printLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            printLocation(null);
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            zoom.setText(String.valueOf(seekBar.getProgress() + 1));
            printLocation(location);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        map = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        text = (TextView)findViewById(R.id.text);
        zoom = (TextView)findViewById(R.id.zoom);
        seek = (SeekBar)findViewById(R.id.seekBar);

        seek.setMax(ZOOM_MAX);
        seek.setProgress(ZOOM_INIT);
        zoom.setText(String.valueOf(ZOOM_INIT));

        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, listener);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        printLocation(location);

        // Подключаем обработчик события перемещения для ползунка
        seek.setOnSeekBarChangeListener(seekBarListener);

    }


    public void printLocation(Location location){
        if (location != null){

            double lat = location.getLatitude();
            double lon = location.getLongitude();

            point = new LatLng(lat, lon);
            map.getMapAsync(this);

            // Отображаем координаты местоположения на экране
            text.setText(
                    String.format("Lat: %4.2f, Long: %4.2f", lat, lon));

            try{
                // Используем Geocoder для получения информации
                // о местоположении и выводим ее на экран
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses.size() > 0){
                    Address address = addresses.get(0);
                    for (int i = 0; i<address.getMaxAddressLineIndex(); i++){
                        text.append(", " + address.getAddressLine(i));
                    }
                    text.append(", " + address.getCountryName());
                }
            }
            catch (IOException e){
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            text.setText("Unabale get location");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, seek.getProgress() + 1));
        googleMap.addMarker(new MarkerOptions().title("Vous ete ici").position(point));
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(seek.getProgress() + 1), 2000, null);
    }
}
