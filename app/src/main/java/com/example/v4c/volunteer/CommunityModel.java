package com.example.v4c.volunteer;

public class CommunityModel {
    public String name;
    public String tagline;
    public String about;
    public String email;
    public int experience;
    public String main_image;
    public String sub_image;
    public String place;
    public double rating;
    public int volunteers;
    public String why_join_us;

    public CommunityModel() {
        // Required empty constructor for Firebase
    }

    public CommunityModel(String name, String tagline, String about, String email, int experience,
                    String main_image, String sub_image, String place, double rating,
                    int volunteers, String why_join_us) {
        this.name = name;
        this.tagline = tagline;
        this.about = about;
        this.email = email;
        this.experience = experience;
        this.main_image = main_image;
        this.sub_image = sub_image;
        this.place = place;
        this.rating = rating;
        this.volunteers = volunteers;
        this.why_join_us = why_join_us;
    }

    // Getters and setters
    public String getName() { return name; }
    public String getTagline() { return tagline; }
    public String getAbout() { return about; }
    public String getEmail() { return email; }
    public int getExperience() { return experience; }
    public String getMain_image() { return main_image; }
    public String getSub_image() { return sub_image; }
    public String getPlace() { return place; }
    public double getRating() { return rating; }
    public int getVolunteers() { return volunteers; }
    public String getWhy_join_us() { return why_join_us; }

    public void setName(String name) { this.name = name; }
    public void setTagline(String tagline) { this.tagline = tagline; }
    public void setAbout(String about) { this.about = about; }
    public void setEmail(String email) { this.email = email; }
    public void setExperience(int experience) { this.experience = experience; }
    public void setMain_image(String main_image) { this.main_image = main_image; }
    public void setSub_image(String sub_image) { this.sub_image = sub_image; }
    public void setPlace(String place) { this.place = place; }
    public void setRating(double rating) { this.rating = rating; }
    public void setVolunteers(int volunteers) { this.volunteers = volunteers; }
    public void setWhy_join_us(String why_join_us) { this.why_join_us = why_join_us; }
}
