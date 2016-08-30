package com.example.sriram.photoBook;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sriram on 7/23/2016.
 */
public class UserInformation {

    private String userName;
    private String emailId;
    private String phoneNumber;
    private String companyName;
    private String catchPhrase;
    private String website;
    private String bs;
    private String Street;
    private String suit;
    private String city;
    private String zipCode;
    private LatLng location;

    public UserInformation(String userName, String emailId, String phoneNumber, String companyName, String catchPhrase,
                           String website, String bs, String street, String suit, String city, String zipCode,LatLng location) {
        this.userName = userName;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
        this.companyName = companyName;
        this.catchPhrase = catchPhrase;
        this.website = website;
        this.bs = bs;
        Street = street;
        this.suit = suit;
        this.city = city;
        this.zipCode = zipCode;
        this.location = location;
    }

    public LatLng getLocation(){
        return location;
    }

    public String getWebsite() {
        return website;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCatchPhrase() {
        return catchPhrase;
    }

    public String getBs() {
        return bs;
    }

    public String getStreet() {
        return Street;
    }

    public String getSuit() {
        return suit;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

}
