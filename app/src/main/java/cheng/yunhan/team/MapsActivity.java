package cheng.yunhan.team;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cheng.yunhan.team.Service.LocationService;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
        View infoView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            ((TextView)infoView.findViewById(R.id.textView)).setText(marker.getTitle());
            ((TextView)infoView.findViewById(R.id.textView2)).setText(marker.getTag().toString());

            return infoView;
        }
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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 10));
                return false;
            }
        });

        LocationService.getCurrentLocation(getApplicationContext(), new LocationService.LocationGotListner() {
            @Override
            public void onLocationGot(Location location) {
                Double latitude = location.getLatitude();
                Double longituede = location.getLongitude();
                LatLng sydney = new LatLng(latitude, longituede);
                LatLng sydney1 = new LatLng(latitude - 0.1, longituede);
                LatLng sydney2 = new LatLng(latitude - 0.3, longituede);
                LatLng sydney3 = new LatLng(latitude - 0.5, longituede);

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {

                    List<Address> addresses =  geocoder.getFromLocation(
                            location.getLatitude(),
                            location.getLongitude(),
                            // In this sample, get just a single address.
                            1);
                    Address address = addresses.get(0);
                    String addstr = address.getAddressLine(address.getMaxAddressLineIndex());
                    String local = address.getLocality();
                    Log.e("","");


                } catch (IOException e) {
                    e.printStackTrace();
                }

                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney")).setTag("adfb");
                mMap.addMarker(new MarkerOptions().position(sydney1).title("Marker in Sydney")).setTag("addd");
                mMap.addMarker(new MarkerOptions().position(sydney2).title("Marker in Sydney")).setTag("dfdfbbbbbdfdf");
                mMap.addMarker(new MarkerOptions().position(sydney3).title("Marker in Sydney")).setTag("xxx");
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        });
    }
}
