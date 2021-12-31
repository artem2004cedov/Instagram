package com.example.instagram.Model;

public class Stories {

    private String imageurl;
    private String srorisid;
    private String publisher;

    public Stories() {
    }

    public Stories(String imageurl, String srorisid, String publisher) {
        this.imageurl = imageurl;
        this.srorisid = srorisid;
        this.publisher = publisher;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getSrorisid() {
        return srorisid;
    }

    public void setSrorisid(String srorisid) {
        this.srorisid = srorisid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
