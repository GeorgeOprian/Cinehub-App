package com.example.cinehub.API;

import com.example.cinehub.Movie.MovieModel;
import com.example.cinehub.SearchMovieAction.SearchResults;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OMDBAPIService {

    @GET("/")
    Call<MovieModel> getMovieByIMDBId(
            @Query("apiKey") String apiKey,
            @Query("i") String ID
    );
    @GET("/")
    Call<SearchResults> searchMoviesByTitle(
            @Query("apiKey") String apiKey,
            @Query("s") String search
    );


}
