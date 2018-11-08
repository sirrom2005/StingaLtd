package com.stingaltd.stingaltd.Models;

import java.io.Serializable;

public class ImageData implements Serializable {
    private static final long serialVersionUID = 102L;
    private int JobId;
    private String DateCreated;
    private String PhotoType;
    private String Location;
    private int Uploaded;
    private String Label;
    private String Thumb;
    private String LargeImage;

    public ImageData(int jobId, String dateCreated, String photoType, String location, String label, int uploaded, String thumb, String largeImage) {
        JobId       = jobId;
        DateCreated = dateCreated;
        PhotoType   = photoType;
        Location    = location;
        Label       = label;
        LargeImage  = largeImage;
        Thumb       = thumb;
        Uploaded    = uploaded;
    }

    public int getJobId() {
        return JobId;
    }

    public String getPhotoType() { return PhotoType; }

    public String getDateCreated() { return DateCreated; }

    public String getLocation() { return Location; }

    public int Uploaded() {
        return Uploaded;
    }

    public String getLabel() {
        return Label;
    }

    public String getThumb() {
        return Thumb;
    }

    public String getLargeImage() {
        return LargeImage;
    }
}
