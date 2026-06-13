package com.example.youjurental.entity;

public class House {
    private int id;
    private String communityName;
    private String city;
    private String district;
    private String area;
    private int monthlyRent;
    private String houseType;
    private double buildingArea;
    private String orientation;
    private String rentType;
    private String decoration;
    private String landlordName;
    private String landlordPhone;
    private String description;
    private String tags;
    private String imageUrls;
    private String bannerUrl;

    public House() {
    }

    public House(int id, String communityName, String city, String district, String area,
                 int monthlyRent, String houseType, double buildingArea, String orientation,
                 String rentType, String decoration, String landlordName, String landlordPhone,
                 String description, String tags, String imageUrls, String bannerUrl) {
        this.id = id;
        this.communityName = communityName;
        this.city = city;
        this.district = district;
        this.area = area;
        this.monthlyRent = monthlyRent;
        this.houseType = houseType;
        this.buildingArea = buildingArea;
        this.orientation = orientation;
        this.rentType = rentType;
        this.decoration = decoration;
        this.landlordName = landlordName;
        this.landlordPhone = landlordPhone;
        this.description = description;
        this.tags = tags;
        this.imageUrls = imageUrls;
        this.bannerUrl = bannerUrl;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public int getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(int monthlyRent) { this.monthlyRent = monthlyRent; }
    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }
    public double getBuildingArea() { return buildingArea; }
    public void setBuildingArea(double buildingArea) { this.buildingArea = buildingArea; }
    public String getOrientation() { return orientation; }
    public void setOrientation(String orientation) { this.orientation = orientation; }
    public String getRentType() { return rentType; }
    public void setRentType(String rentType) { this.rentType = rentType; }
    public String getDecoration() { return decoration; }
    public void setDecoration(String decoration) { this.decoration = decoration; }
    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    public String getLandlordPhone() { return landlordPhone; }
    public void setLandlordPhone(String landlordPhone) { this.landlordPhone = landlordPhone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }
    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
}
