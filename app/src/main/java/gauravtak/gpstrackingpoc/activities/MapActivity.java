package gauravtak.gpstrackingpoc.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import gauravtak.gpstrackingpoc.R;
import gauravtak.gpstrackingpoc.constant_classes.IntentConstants;
import gauravtak.gpstrackingpoc.gson_classes.LocationDetailsOfUser;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Double longitude;
    private Double latitude;
    private String locationDescription;
    private float zoomLevel = 15;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getIntentValues();

        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);


        // Enabling MyLocation Layer of Google Map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // Getting GoogleMap object from the fragment
        fm.getMapAsync(this);


    }

    private void getIntentValues() {
        LocationDetailsOfUser locationDetailsOfUser = (LocationDetailsOfUser)getIntent().getSerializableExtra(IntentConstants.LOCATION_DETAILS_OF_USER);
        longitude = locationDetailsOfUser.getLongitudeValue();
        latitude = locationDetailsOfUser.getLatitudeValue();
        locationDescription = locationDetailsOfUser.getLocationDescription();
    }

    private void updateLocation() {

                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng).title(locationDescription);



                googleMap.addMarker(markerOptions);

                  LatLng zoomLatLng = latLng;
            try
            {
                CameraUpdate center = CameraUpdateFactory.newLatLng(zoomLatLng);
                googleMap.moveCamera(center);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomLevel);
                googleMap.animateCamera(zoom);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(70)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap = googleMap;
        updateLocation();
    }
}