package albertperez.healthsites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class HealthSite implements Serializable, ClusterItem {

    private String id;
    private String name;
    private String description;
    private String companyName;
    private String companyCIF;
    private String website;
    private String webMail;
    private String phoneNumber;
    private String address;
    private String countryCode;
    private String country;
    private String adminArea;
    private String city;
    private String postalCode;
    private String street;
    private String number;
    private Double latitude;
    private Double longitude;
    private Boolean freeGluten;
    private Boolean freeLactose;
    private Boolean vegan;
    private Boolean vegetarian;
    private Boolean ecologic;
    private Boolean restaurant;
    private Boolean store;
    private Boolean verified = false;

    public HealthSite(){ }

    public HealthSite(HealthSite hs){
        this.id = hs.getId();
        this.name = hs.getName();
        this.description = hs.getDescription();
        this.companyName = hs.getCompanyName();
        this.companyCIF = hs.getCompanyCIF();
        this.website = hs.getWebsite();
        this.webMail = hs.getWebMail();
        this.phoneNumber = hs.getPhoneNumber();
        this.address = hs.getAddress();
        this.countryCode = hs.getCountryCode();
        this.country = hs.getCountry();
        this.adminArea = hs.getAdminArea();
        this.city = hs.getCity();
        this.postalCode = hs.getPostalCode();
        this.street = hs.getStreet();
        this.number = hs.getNumber();
        this.latitude = hs.getLatitude();
        this.longitude = hs.getLongitude();
        this.freeGluten = hs.isFreeGluten();
        this.freeLactose = hs.isFreeLactose();
        this.vegan = hs.isVegan();
        this.vegetarian = hs.isVegetarian();
        this.ecologic = hs.isEcologic();
        this.restaurant = hs.isRestaurant();
        this.store = hs.isStore();
        this.verified = hs.isVerified();
    }

    public HealthSite(String name, String description, String companyName, String companyCIF, String phoneNumber,
                      String address, String countryCode, String country, String adminArea, String city, String postalCode, String street, String number,
                      Double latitude, Double longitude, String website, String webMail,
                      Boolean freeGluten, Boolean freeLactose, Boolean veganOffer, Boolean vegetarianOffer, Boolean ecologicOffer,
                      Boolean restaurant, Boolean store) {
        this.name = name;
        this.description = description;
        this.companyName = companyName;
        this.companyCIF = companyCIF;
        this.website = website;
        this.webMail = webMail;
        this.phoneNumber = phoneNumber;
        this.address = address; //placepicker
        this.countryCode = countryCode; //placepicker
        this.country = country; //placepicker
        this.adminArea = adminArea;
        this.city = city; //placepicker
        this.postalCode = postalCode; //placepicker
        this.street = street; //placepicker
        this.number = number; //placepicker
        this.freeGluten = freeGluten;
        this.freeLactose = freeLactose;
        this.vegan = veganOffer;
        this.vegetarian = vegetarianOffer;
        this.ecologic = ecologicOffer;
        this.latitude = latitude;
        this.longitude = longitude;
        this.restaurant = restaurant;
        this.store = store;
    }

    public String getId() {
        return this.id;
    }

    public String setId(String id) {
        return this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCIF() {
        return companyCIF;
    }

    public void setCompanyCIF(String companyCIF) {
        this.companyCIF = companyCIF;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean isFreeGluten() {
        return freeGluten;
    }

    public void setFreeGluten(Boolean freeGluten) {
        this.freeGluten = freeGluten;
    }

    public Boolean  isFreeLactose() {
        return freeLactose;
    }

    public void setFreeLactose(Boolean freeLactose) {
        this.freeLactose = freeLactose;
    }

    public Boolean isVegan() {
        return vegan;
    }

    public void setVegan(Boolean vegan) {
        this.vegan = vegan;
    }

    public Boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(Boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public Boolean isEcologic() {
        return ecologic;
    }

    public void setEcologic(Boolean ecologic) {
        this.ecologic = ecologic;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getWebMail() {
        return webMail;
    }

    public void setWebMail(String webMail) {
        this.webMail = webMail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }

    public Boolean isRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Boolean restaurant) {
        this.restaurant = restaurant;
    }

    public Boolean isStore() {
        return store;
    }

    public void setStore(Boolean store) {
        this.store = store;
    }

    public Boolean isVerified() {
        return verified;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return new LatLng(this.latitude, this.longitude);
    }

    @Nullable
    @Override
    public String getTitle() {
        return this.name;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return this.description;
    }
}
