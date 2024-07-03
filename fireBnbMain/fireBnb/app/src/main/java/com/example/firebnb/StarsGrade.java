package com.example.firebnb;

import java.io.Serializable;
import java.util.List;

public class StarsGrade implements Serializable {
    private List<Integer> stars;
    private int average;

    public StarsGrade(List<Integer> stars, int average) {
        this.stars = stars;
        this.average = average;
    }

    public StarsGrade() {
    }

    // Getters and Setters
    public List<Integer> getStars() {
        return stars;
    }

    public void setStars(List<Integer> stars) {
        this.stars = stars;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

}
