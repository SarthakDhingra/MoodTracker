/*
 * Mood
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
 *
 */

package com.example.moodtracker.model;
import com.example.moodtracker.R;
import com.example.moodtracker.constants;

/**
 * Constructor, and getters for mood object
 */
public class Mood {
    // Should not need db logic in mood
    private String color;
    private int icon;
    private String moodName;
    private String moodNum;

    /**
     * Constructor
     * @param color color of mood
     * @param icon icon for mood
     * @param moodName name of mood
     * @param moodNum number associated with name of mood
     */
    public Mood(String color, int icon, String moodName, String moodNum) {
        this.color = color;
        this.icon = icon;
        this.moodName = moodName;
        this.moodNum = moodNum;
    }

    /**
     * Gets color
     * @return color of mood object
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets icon
     * @return Icon of mood object
     */
    public int getIcon() {
        return icon;
    }

    /**
     * Get mood name
     * @return name of mood object
     */
    public String getMoodName() {
        return moodName;
    }

    /**
     * Gets mood number
     * @return mood number of mood object
     */
    public String getMoodNum() {
        return moodNum;
    }
}


