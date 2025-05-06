package com.example.v4c.volunteer;

public class EventModel {
    private String title;
    private String description;
    private String date;
    private String time;
    private String loc;
    private String category;
    private String imageUrl;

    public EventModel()
    {

    }

    public EventModel(String title, String description, String date, String time, String loc, String category, String imageUrl)
    {
        this.category=category;
        this.time=time;
        this.title=title;
        this.description=description;
        this.date=date;
        this.imageUrl=imageUrl;
        this.loc=loc;
    }

    public String getTitle(){
        return title;
    }

    public String getLoc(){
        return loc;
    }

    public String getDate(){
        return date;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getDescription(){
        return description;
    }

    public String getTime(){
        return time;
    }
    public String getCategory(){
        return category;
    }







}
