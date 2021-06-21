package com.example.cinehub.Movie;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetMoviesDTO {

    @SerializedName("ListOfMovies")
    @Expose
    private List<MovieDTO> moviesList;

    public List<MovieDTO> getMoviesList() {
        return moviesList;
    }

    public void setMoviesList(List<MovieDTO> moviesList) {
        this.moviesList = moviesList;
    }
}
