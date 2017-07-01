package com.samples.location.mapzoom;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

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
            printLocation(null);
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

    }
}
