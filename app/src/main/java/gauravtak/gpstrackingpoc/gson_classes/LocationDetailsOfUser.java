package gauravtak.gpstrackingpoc.gson_classes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LocationDetailsOfUser implements Serializable{
    @SerializedName("location_description")
    private String locationDescription;
    @SerializedName("latitude_value")
    private double latitudeValue;
    @SerializedName("longitude_value")
    private double longitudeValue;

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public double getLatitudeValue() {
        return latitudeValue;
    }

    public void setLatitudeValue(double latitudeValue) {
        this.latitudeValue = latitudeValue;
    }

    public double getLongitudeValue() {
        return longitudeValue;
    }

    public void setLongitudeValue(double longitudeValue) {
        this.longitudeValue = longitudeValue;
    }
}
