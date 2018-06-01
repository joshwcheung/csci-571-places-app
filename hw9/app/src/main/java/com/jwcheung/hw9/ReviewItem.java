package com.jwcheung.hw9;

import java.util.Comparator;

public class ReviewItem {

    private String author;
    private String rating;
    private String time;
    private String text;
    private String photo;
    private String link;
    private int order;

    public ReviewItem(String author, String rating, String time, String text, String photo, String link, int order) {
        this.author = author;
        this.rating = rating;
        this.time = time;
        this.text = text;
        this.photo = photo;
        this.link = link;
        this.order = order;
    }

    public String getAuthor() { return author; }

    public String getRating() { return rating; }

    public String getTime() { return time; }

    public String getText() { return text; }

    public String getPhoto() { return photo; }

    public String getLink() { return link; }

    public int getOrder() { return order; }
}
