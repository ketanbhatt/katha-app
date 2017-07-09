package com.ketanbhatt.kathaapp.books;

public class BookItem {

    public final String name;
    public Boolean isOffline;
    public final String details;
    public String url;

    public BookItem(String name) {
        this.name = name;
        this.isOffline = false;
        this.details = "Some details";
        this.url = "https://www.dropbox.com/s/f5cdga4em5gyhje/mota%20raja%20patli%20raani.zip?dl=1";
    }
}
