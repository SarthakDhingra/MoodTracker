/*
 * Location
 *
 * Version 1.0
 *
 * 11/8/2019
 *
 * MIT License
 *
 * Copyright (c) 2019 CMPUT301F19T26
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.moodtracker.model;

/**
 * Constructor, getters and setters for location
 */
public class Location {
    //declare attributes
    double latitude;
    double longitude;
    String mood;

    /**
     * Constructor
     * @param lat latitude of location
     * @param lon longitude of location
     * @param mood_event the mood event tied to the location
     */
    public Location (double lat, double lon, String mood_event) {
        latitude = lat;
        longitude = lon;
        mood = mood_event;

    }

    //implement getters

    /**
     * Gets Latitude
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets longitude
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * gets mood event
     * @return mood event
     */
    public String getMood() {
        return mood;
    }
}
