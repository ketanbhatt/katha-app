package com.ketanbhatt.kathaapp.books;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ktbt on 04/07/17.
 */

public class AvailableBooks {
    public static final List<String> allBooks = Arrays.asList(
            "book 0", "book 1", "mota raja patli raani", "book 3"
    );

    public static List<BookItem> ITEMS = new ArrayList<>();
    public static Map<String, BookItem> ITEM_MAP = new HashMap<>();

    static {
        for (String book : allBooks) {
            addItem(new BookItem(book));
        }
    }

    private static void addItem(BookItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    public static void setOffline(String name) {
        BookItem bookItem = ITEM_MAP.get(name);

        if (bookItem != null) {
            bookItem.isOffline = true;
        }
    }
}
