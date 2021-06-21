package com.example.cinehub.Movie;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RunningDTO {

    @SerializedName("ImdbID")
    @Expose
    private String imdbID;
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

    public String getImdbID() {
        return imdbID;
    }

    public String getRunningId() {
        return runningId;
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

    public static RunningDTO createRunningDTO(MovieDTO movie) {
        RunningDTO running = new RunningDTO();
        running.runningId = movie.getRunningId();
        running.imdbID = movie.getImdbID();
        running.runningDate = movie.getRunningDate();
        running.runningTime = movie.getRunningTime();
        running.hallNumber = movie.getHallNumber();

        return running;
    }
}
