package com.example.android.booklistingapp;

import java.util.List;

/**
 * Created by Zsolt on 10/2/2017.
 */

public class Books {

    // String for the titel of the book
    private String mTitleBook;

    // String for the author of the book
    private List<String> mAuthorsBook;

    // Website URL of the book's image
    private String mUrlImage;

    // Website URL of the book
    private String mUrlBook;

    /**
     * Constructs a new {@link Books} object.
     *
     * @param titleBook is the titel of the book
     * @param authorsBook is the list of authors of the book
     * @param urlImage is website URL of the book's cover page
     * @param urlBook is the website URL to find more details about the book
     */

    public Books(String titleBook, List<String> authorsBook, String urlImage, String urlBook) {
        mTitleBook = titleBook;
        mAuthorsBook = authorsBook;
        mUrlImage = urlImage;
        mUrlBook = urlBook;
    }

    // Get the titel text
    public String getTitleBook() {
        return mTitleBook;
    }

    // Get the author text
    public List<String> getAuthorsBook() {
        return mAuthorsBook;
    }

    // Get the image URL as text
    public String getUrlImage() {
        return mUrlImage;
    }

    // Returns the website URL to find more information about the book
    public String getUrlBook() {
        return mUrlBook;
    }

}

