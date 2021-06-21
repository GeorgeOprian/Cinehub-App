package com.example.cinehub.Movie;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetBookingsDTO {

    @SerializedName("ListOfBookings")
    @Expose
    private List<BookingDTO> bookingsList;

    public List<BookingDTO> getBookingsList() {
        return bookingsList;
    }

    public void setBookingsList(List<BookingDTO> bookingsList) {
        this.bookingsList = bookingsList;
    }
}
