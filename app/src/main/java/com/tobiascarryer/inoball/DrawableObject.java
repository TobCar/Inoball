package com.tobiascarryer.inoball;

import android.graphics.Canvas;

/**
 * Created by Tobias Carryer on 2017-04-17.
 */

interface DrawableObject {
    /**
     * Pre: Canvas is being displayed
     * @param canvas The game's canvas. Cannot be null.
     */
    void draw(Canvas canvas);
}
