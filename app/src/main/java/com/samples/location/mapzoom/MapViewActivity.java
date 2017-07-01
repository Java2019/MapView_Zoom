package com.samples.location.mapzoom;

import android.location.*;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapViewActivity extends MapActivity {

    private static final int ZOOM_MAX = 21;
    private static final int ZOOM_INIT = 17;

    private MapController controller;
    private TextView text;
    private TextView zoom;
    private MapView map;
    private SeekBar seek;

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
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
            //printLocation(null);
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int myZoomLevel = seekBar.getProgress() + 1;
            controller.setZoom(myZoomLevel);
            zoom.setText(String.valueOf(seek.getProgress() + 1));
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

        map = (MapView)findViewById(R.id.map);
        text = (TextView)findViewById(R.id.text);
        zoom = (TextView)findViewById(R.id.zoom);
        seek = (SeekBar)findViewById(R.id.seekBar);

        seek.setMax(ZOOM_MAX);
        seek.setProgress(ZOOM_INIT);
        zoom.setText(String.valueOf(ZOOM_INIT));

        controller = map.getController();
        controller.setZoom(ZOOM_INIT);

        map.setSatellite(true);
        // Добавляем элемент управления масштабированием карты
        map.displayZoomControls(true);
        map.setBuiltInZoomControls(true);

        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, listener);
        printLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

        // Подключаем обработчик события перемещения для ползунка
        seek.setOnSeekBarChangeListener(seekBarListener);

    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public void printLocation(Location location){
        if (location != null){

            double lat = location.getLatitude();
            double lon = location.getLongitude();

            GeoPoint geoPoint = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));

            controller.animateTo(geoPoint);

            /*
            ImageView marker = new ImageView(getApplicationContext());
            marker.setImageResource(R.drawable.star);
            MapView.LayoutParams markerParams = new MapView.LayoutParams(
                    MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT,
                    geoPoint, MapView.LayoutParams.TOP_LEFT );
            map.addView(marker, markerParams);
            */

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
}
