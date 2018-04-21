package gauravtak.gpstrackingpoc.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import gauravtak.gpstrackingpoc.R;
import gauravtak.gpstrackingpoc.constant_classes.IntentConstants;
import gauravtak.gpstrackingpoc.gson_classes.LocationDetailsOfUser;
import gauravtak.gpstrackingpoc.network.GPSTracker;
import gauravtak.gpstrackingpoc.utils_classes.AlertDialogUtil;
import gauravtak.gpstrackingpoc.utils_classes.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final int REQUEST_LOCATION_PERMISSIONS = 101 ;
    private double latitude;
    private double longitude;
    private Context context=this;
    private Button btnShowLocation,btnMapLocation;
    private TextView textViewLocation;
    private GPSTracker gps;
    private String locationDetails;
    private LocationDetailsOfUser locationDetailsOfUser;
    private Activity mActivity;


    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            mActivity = this;
            btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
            textViewLocation = (TextView) findViewById(R.id.textViewAddress);
            btnMapLocation = (Button) findViewById(R.id.btnMapLocation);
            btnShowLocation.setOnClickListener(this);
            btnMapLocation.setOnClickListener(this);
            if(!isLocationPermissionGranted())
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestLocationPermissions();
                }
            }

        }
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocationPermissions()
    {

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
    }
    public void getlocation()
    {
       final Handler handler = new GeocoderHandler();
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List < Address > addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        String completeAddress = address.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = address.getLocality();
                        String state = address.getAdminArea();
                        String country = address.getCountryName();
                        String postalCode = address.getPostalCode();
                        String knownName = address.getFeatureName();
                        Log.d("Location_Details", completeAddress + " N " + city + " N " + state + " N " + country + " N " + postalCode + " N " + knownName);
                         locationDetails= "<b>Complete Address :</b> " +completeAddress
                                 + " <br><br><b>City :</b> " + city + " <br><br> <b>State :</b> " + state + " <br><br> <b>Country :</b> " + country
                                 + " <br><br> <b>Postal Code :</b> " + postalCode + " <br><br> <b>Known Name :</b> " + knownName;
                       String locationDetails2 = completeAddress;

                        locationDetailsOfUser = new LocationDetailsOfUser();
                        locationDetailsOfUser.setLatitudeValue(latitude);
                        locationDetailsOfUser.setLongitudeValue(longitude);
                        locationDetailsOfUser.setLocationDescription(locationDetails2);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnMapLocation.setVisibility(View.VISIBLE);
                                textViewLocation.setText(Html.fromHtml(locationDetails));

                            }
                        });
                         StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)); //.append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e("Location Address Loader", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = " Unable to get address for this location.please try again";
                       final String addressResult = result;
                       bundle.putString("address", result);
                        message.setData(bundle);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnMapLocation.setVisibility(View.GONE);
                                textViewLocation.setText(addressResult);

                            }
                        });
                    }
                    message.sendToTarget();

                }
            }
        };
        thread.start();


    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnShowLocation:
                if(!isLocationPermissionGranted())
                {
                    AlertDialogUtil.showAlertDialogWithFinishActivity(mActivity,getString(R.string.not_provided_required_permission));
                return;
                }
                getLocationDetails();
                break;
            case R.id.btnMapLocation:
                if(Utils.isNetworkAvailable(this))
                {
                    AlertDialogUtil.showErrorDialog(mActivity,getString(R.string.not_connected_with_internet));
                    return;
                }
                Intent intent = new Intent(MainActivity.this,MapActivity.class);
                intent.putExtra(IntentConstants.LOCATION_DETAILS_OF_USER,locationDetailsOfUser);
                startActivity(intent);
                break;
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Log.d("Location2",locationAddress);
          //  tvAddress.setText(locationAddress);
        }
    }
    private void getLocationDetails() {
        // create class object
        gps = new GPSTracker(MainActivity.this);


        if (gps.canGetLocation()) {

             latitude = gps.getLatitude();
             longitude = gps.getLongitude();



            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            getlocation();
        } else {


            gps.showSettingsAlert();
        }

    }
    private boolean isLocationPermissionGranted()
    {

        return (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

}



