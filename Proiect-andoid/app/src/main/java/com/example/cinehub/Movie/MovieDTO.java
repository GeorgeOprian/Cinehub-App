package com.example.cinehub.Movie;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieDTO {

    @SerializedName("ImdbID")
    @Expose
    private String imdbID;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Released")
    @Expose
    private String released;
    @SerializedName("Duration")
    @Expose
    private String duration;
    @SerializedName("Genre")
    @Expose
    private String genre;
    @SerializedName("Director")
    @Expose
    private String director;
    @SerializedName("Writer")
    @Expose
    private String writer;
    @SerializedName("Actors")
    @Expose
    private String actors;
    @SerializedName("Plot")
    @Expose
    private String plot;
    @SerializedName("Language")
    @Expose
    private String language;
    @SerializedName("Awards")
    @Expose
    private String awards;
    @SerializedName("Poster")
    @Expose
    private String poster;
    @SerializedName("Ratings")
    @Expose
    private List<Rating> ratings = null;
    @SerializedName("imdbRating")
    @Expose
    private String imdbRating;
    @SerializedName("BoxOffice")
    @Expose
    private String boxOffice;
    @SerializedName("RunningId")
    @Expose
    private String runningId;
    @SerializedName("RunningDate")
    @Expose
    private String runningDate;
    @SerializedName("RunningTime")
    @Expose
    private String runningTime;
    @SerializedName("HallNumber")
    @Expose
    private String hallNumber;
    @SerializedName("OccupiedSeats")
    @Expose
    private List<Integer> occupiedSeats;


    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(String boxOffice) {
        this.boxOffice = boxOffice;
    }

    public String getRunningId() {
        return runningId;
    }

    public void setRunningId(String runningId) {
        this.runningId = runningId;
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

    public String getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(String hallNumber) {
        this.hallNumber = hallNumber;
    }

    public List<Integer> getOccupiedSeats() {
        return occupiedSeats;
    }

    public void setOccupiedSeats(List<Integer> occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
    }

    public String getOccupiedSeatsString() {
        String s = "  ";
        for (int i = 0; i < occupiedSeats.size(); i++) {
            s += occupiedSeats.get(i).toString();
            if (i < occupiedSeats.size() - 1) {
                s += ", ";
            }
        }
        return s;
    }

    public static MovieDTO createMovieDTO(MovieModel current, String date, String time, String hallNumber) {
        MovieDTO dto = new MovieDTO();
        dto.setImdbID(current.getImdbID());
        dto.setTitle(current.getTitle());
        dto.setReleased(current.getReleased());
        dto.setDuration(current.getRuntime());
        dto.setGenre(current.getGenre());
        dto.setDirector(current.getDirector());
        dto.setWriter(current.getWriter());
        dto.setActors(current.getActors());
        dto.setPlot(current.getPlot());
        dto.setLanguage(current.getLanguage());
        dto.setAwards(current.getAwards());
        dto.setPoster(current.getPoster());
        dto.setRatings(current.getRatings());
        dto.setImdbRating(current.getImdbRating());
        dto.setBoxOffice(current.getBoxOffice());

        dto.setRunningDate(date);
        dto.setRunningTime(time);
        dto.setHallNumber(hallNumber);

        return dto;
    }
}