package com.ketanbhatt.kathaapp.books;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookItem {

    public final String name;
    public Boolean isOffline;
    public final String details;

    public BookItem(String name) {
        this.name = name;
        this.isOffline = false;
        this.details = "Some details";
    }
}
