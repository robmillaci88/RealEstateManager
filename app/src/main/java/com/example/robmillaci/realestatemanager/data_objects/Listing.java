package com.example.robmillaci.realestatemanager.data_objects;

import com.example.robmillaci.realestatemanager.utils.DecimalFormatter;
import com.example.robmillaci.realestatemanager.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class is responsible for handling the creation of listing objects
 */
public class Listing implements Serializable {
    private static final long serialVersionUID = 137356756725454L; //the UID for serialization
    public static final String DEFAULT_LISTING_ID = "0"; //the default listing ID

    private String id; //the id of the listing
    private String type; //the type of listing
    private double price; //the price of the listing
    private double surfaceArea; //the surface area of the listing
    private int numbOfBedRooms; //the number of bedrooms
    private String descr; //the description of the listing
    private List<byte[]> photos; //list of byte arrays representing the photos (this is for listings stored on the local database
    private ArrayList<String> firebasePhotos; //List of string URIs that point to firebase storage locations where the photos are saved
    private String[] photoDescriptions; //string array to hold the photo descriptions
    private String address_postcode; //the listings postcode
    private String address_number; //the listings address number
    private String address_street; //the listings street
    private String address_town; //the listings town
    private String address_county; //the listing country
    private String poi; //the listing poi
    private String postedDate; //the listing posted date
    private String saleDate; //the listing sale date
    private String agent; //the agent responsible for the listing
    private String lastUpdateTime; //the last update time of the listing
    private boolean forSaleStatus; //whether the listing is for sale or sold
    private String buyOrLet; //whether the listing is buy or let


    /**
     * 2 constructors for this class. One for local db listings  and one for firebase, depending wether the photos are a list of byte[] or an arraylist of strings
     */
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


    /**
     * creates a unique id for each listing created
     * @return the returned unique id
     */
    private String createTransactionID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase() + Utils.getTodayDate().replaceAll("/", "").toLowerCase()
                .replaceAll(":", "").replaceAll(" ", "").trim();
    }


    //getters and setters
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
