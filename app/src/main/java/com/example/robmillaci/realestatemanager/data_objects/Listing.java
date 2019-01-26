package com.example.robmillaci.realestatemanager.data_objects;

import com.example.robmillaci.realestatemanager.utils.DecimalFormatter;
import com.example.robmillaci.realestatemanager.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Listing implements Serializable {
    private static final long serialVersionUID = 137356756725454L;
    public static final String DEFAULT_LISTING_ID = "0";


    private String id;
    private String type;
    private double price;
    private double surfaceArea;
    private int numbOfBedRooms;
    private String descr;
    private List<byte[]> photos;
    private ArrayList<String> firebasePhotos;
    private String[] photoDescriptions;
    private String address_postcode;
    private String address_number;
    private String address_street;
    private String address_town;
    private String address_county;
    private String poi;
    private String postedDate;
    private String saleDate;
    private String agent;
    private String lastUpdateTime;
    private boolean forSaleStatus;
    private String buyOrLet;

    public Listing(String id, String type, double price, double surfaceArea, int numbOfBedRooms, String descr, List<byte[]> photo, String[] photoDescriptions,
                   String address_postcode, String address_number, String address_street, String address_town, String address_county, String poi, String postedDate, String saleDate,
                   String agent, String lastUpdateTime, String buyOrLet,boolean forSaleStatus) {
        this.id = id.equals(DEFAULT_LISTING_ID) ? createTransactionID() : id;
        this.type = type;
        this.price = price;
        this.surfaceArea = surfaceArea;
        this.numbOfBedRooms = numbOfBedRooms;
        this.descr = descr;
        this.photos = photo;
        this.photoDescriptions = photoDescriptions;
        this.address_postcode = address_postcode;
        this.address_number = address_number;
        this.address_street = address_street;
        this.address_town = address_town;
        this.address_county = address_county;
        this.poi = poi;
        this.postedDate = postedDate;
        this.agent = agent;
        this.saleDate = saleDate;
        this.lastUpdateTime = lastUpdateTime;
        this.forSaleStatus = forSaleStatus;
        this.buyOrLet = buyOrLet;

    }

    public Listing(String id, String type, double price, double surfaceArea, int numbOfBedRooms, String descr, ArrayList<String> photo, String[] photoDescriptions,
                   String address_postcode, String address_number, String address_street, String address_town, String address_county, String poi, String postedDate, String saleDate,
                   String agent, String lastUpdateTime, String buyOrLet, boolean forSaleStatus) {
        this.id = id.equals(DEFAULT_LISTING_ID) ? createTransactionID() : id;
        this.type = type;
        this.price = price;
        this.surfaceArea = surfaceArea;
        this.numbOfBedRooms = numbOfBedRooms;
        this.descr = descr;
        this.firebasePhotos = photo;
        this.photoDescriptions = photoDescriptions;
        this.address_postcode = address_postcode;
        this.address_number = address_number;
        this.address_street = address_street;
        this.address_town = address_town;
        this.address_county = address_county;
        this.poi = poi;
        this.postedDate = postedDate;
        this.agent = agent;
        this.saleDate = saleDate;
        this.lastUpdateTime = lastUpdateTime;
        this.forSaleStatus = forSaleStatus;
        this.buyOrLet = buyOrLet;

    }


    private String createTransactionID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase() + Utils.getTodayDate().replaceAll("/", "").toLowerCase()
                .replaceAll(":", "").replaceAll(" ", "").trim();
    }

    public String getBuyOrLet() {
        return buyOrLet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public String getFormattedPrice(){
       return DecimalFormatter.formatNumber(((Double) this.getPrice()).intValue());
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSurfaceArea() {
        return surfaceArea;
    }

    public int getNumbOfBedRooms() {
        return numbOfBedRooms;
    }

    public String getDescr() {
        return descr;
    }

    public List<byte[]> getPhotos() {
        return photos;
    }

    public ArrayList<String> getFirebasePhotos() {
        return firebasePhotos;
    }

    public void setFirebasePhotos(ArrayList<String> firebasePhotos) {
        this.firebasePhotos = firebasePhotos;
    }

    public void setPhotos(ArrayList<byte[]> photos) {
        this.photos = photos;
    }

    public String[] getPhotoDescriptions() {
        return photoDescriptions;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public String getPoi() {
        return poi;
    }


    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public String getFormattedPostedDate(){
        return postedDate.substring(0,10);
    }

    public String getFormattedLastUpdateTime(){
        return lastUpdateTime.substring(0,10);
    }

    public String getAgent() {
        return agent;
    }

    public String getAddress_postcode() {
        return address_postcode;
    }

    public String getAddress_number() {
        return address_number;
    }

    public String getAddress_town() {
        return address_town;
    }

    public String getAddress_county() {
        return address_county;
    }

    public boolean isForSale() {
        return forSaleStatus;
    }

    public String getAddress_street() {
        return address_street;
    }
}
