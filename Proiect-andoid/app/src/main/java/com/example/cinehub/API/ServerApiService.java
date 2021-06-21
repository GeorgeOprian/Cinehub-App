package com.example.cinehub.API;

import com.example.cinehub.Movie.BookingDTO;
import com.example.cinehub.Movie.GetBookingsDTO;
import com.example.cinehub.Movie.GetMoviesDTO;
import com.example.cinehub.Movie.MovieDTO;
import com.example.cinehub.Movie.RunningDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServerApiService {
    @GET("all_movies")
    Call<GetMoviesDTO> getMovies();

    @GET("search_movie_by_title")
    Call<GetMoviesDTO> getMoviesByTitle(@Query("movie_title") String movie_title);

    @POST("add_movie")
    Call<MovieDTO> addMovie(@Body MovieDTO movieModel);

    @POST("add_booking")
    Call<BookingDTO> addBooking(@Body BookingDTO booking);

    @GET("bookings_for_user")
    Call<GetBookingsDTO> getBookingsForUser(@Query("user_id") String userId);

    @DELETE("delete_movie")
    Call<Void> deleteMovie(@Query("imdb_id") String imdbId);

    @POST("update_movie")
    Call<RunningDTO> updateMovieRunningDetails(@Body RunningDTO running);

    @DELETE("delete_booking")
    Call<Void> deleteBooking(@Query("booking_id") String bookingId);


}
