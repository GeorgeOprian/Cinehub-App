package com.example.cinehub;

import com.example.cinehub.Movie.BookingDTO;
import com.example.cinehub.Movie.MovieDTO;
import com.example.cinehub.Movie.MovieModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SharedBetweenFragments {

    private static SharedBetweenFragments instance;
    private MovieModel movieToAddDisplayData;
    private MovieDTO movieToPassForDetailsDisplay;
    public static final int COULD_NOT_INSERT_IN_DB = 512;
    public static final int BOOKINGS_LINKED_TO_MOVIE = 513;
    public static final int RESOURCE_NOT_FOUND = 204;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int MAX_NUMBER_OF_SEATS_IN_HALL = 50;
    private FirebaseUser user;
    private List<MovieDTO> listOfRunningMovies;
    private MovieDTO movieToBeUpdated;
    private BookingDTO bookingToBeDisplayed;
    private List<BookingDTO> listOfBookings;

    private SharedBetweenFragments() { }

    public static SharedBetweenFragments getInstance() {
        if (instance == null) {
            instance = new SharedBetweenFragments();
        }
        return instance;
    }

    public MovieModel getMovieToAddDisplayData() {
        return movieToAddDisplayData;
    }

    public void setMovieToAddDisplayData(MovieModel movieToAddDisplayData) {
        this.movieToAddDisplayData = movieToAddDisplayData;
    }

    public MovieDTO getMovieToPassForDetailsDisplay() {
        return movieToPassForDetailsDisplay;
    }

    public void setMovieToPassForDetailsDisplay(MovieDTO movieToPassForDetailsDisplay) {
        this.movieToPassForDetailsDisplay = movieToPassForDetailsDisplay;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public static int calculateFontSize(String string) {
        if (string.length() <= 10) {
            return 30;
        }
        if (string.length() <= 20) {
            return 26;
        }
        if (string.length() <= 30) {
            return 20;
        }
        return 18;
    }

    public List<MovieDTO> getListOfRunningMovies() {
        return listOfRunningMovies;
    }

    public void setListOfRunningMovies(List<MovieDTO> listOfRunningMovies) {
        this.listOfRunningMovies = listOfRunningMovies;
    }

    public MovieDTO getMovieToBeUpdated() {
        return movieToBeUpdated;
    }

    public void setMovieToBeUpdated(MovieDTO movieToBeUpdated) {
        this.movieToBeUpdated = movieToBeUpdated;
    }

    public BookingDTO getBookingToBeDisplayed() {
        return bookingToBeDisplayed;
    }

    public void setBookingToBeDisplayed(BookingDTO bookingToBeDisplayed) {
        this.bookingToBeDisplayed = bookingToBeDisplayed;
    }

    public List<BookingDTO> getListOfBookings() {
        return listOfBookings;
    }

    public void setListOfBookings(List<BookingDTO> listOfBookings) {
        this.listOfBookings = listOfBookings;
    }
}
