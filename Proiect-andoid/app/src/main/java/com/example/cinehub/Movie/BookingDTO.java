package com.example.cinehub.Movie;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookingDTO {

    @SerializedName("BookingId")
    @Expose
    private String bookingId;
    //for bookings card
    @SerializedName("MovieTitle")
    @Expose
    private String movieTitle;
    @SerializedName("Poster")
    @Expose
    private String poster;
    @SerializedName("RunningDate")
    @Expose
    private String runningDate;
    @SerializedName("RunningTime")
    @Expose
    private String runningTime;
    @SerializedName("ReservedSeats")
    @Expose
    private List<Integer> listOfReservedSeats;
    //-----
    @SerializedName("UserId")
    @Expose
    private String userId;
    @SerializedName("RunningId")
    @Expose
    private String runningId;
    @SerializedName("DateTime")
    @Expose
    private String dateTime;
    @SerializedName("HallNumber")
    @Expose
    private String hallNumber;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getRunningDate() {
        return runningDate;
    }

    public void setRunningDate(String runningDate) {
        this.runningDate = runningDate;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    public List<Integer> getListOfReservedSeats() {
        return listOfReservedSeats;
    }

    public void setListOfReservedSeats(List<Integer> listOfReservedSeats) {
        this.listOfReservedSeats = listOfReservedSeats;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRunningId() {
        return runningId;
    }

    public void setRunningId(String runningId) {
        this.runningId = runningId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(String hallNumber) {
        this.hallNumber = hallNumber;
    }
}
