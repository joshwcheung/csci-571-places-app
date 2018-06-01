package com.jwcheung.hw9;

public class PlaceItem {

    private String name;
    private String address;
    private String icon;
    private String placeid;
    private double lat;
    private double lng;

    public PlaceItem(String name, String address, String icon, String placeid, double lat, double lng) {
        this.name = name;
        this.address = address;
        this.icon = icon;
        this.placeid = placeid;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() { return name; }

    public String getAddress() { return address; }

    public String getIcon() { return icon; }

    public String getPlaceId() { return placeid; }

    public double getLat() { return lat; }

    public double getLng() { return lng; }
}
