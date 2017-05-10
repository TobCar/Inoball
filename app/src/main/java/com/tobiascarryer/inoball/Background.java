package com.tobiascarryer.inoball;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by TobiasC on 2017-04-17.
 */

class Background implements DrawableObject {

    private Bitmap tile;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    Background(Bitmap res, int screenWidth, int screenHeight ) {
        tile = res;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
    }

    /**
     * Pre: Canvas is being displayed
     * @param canvas The game's canvas. Cannot be null.
     */
    public void draw(Canvas canvas) {
        assert canvas != null;

        for( int x = 0; x < mScreenWidth; x += tile.getWidth() ) {
            for( int y = 0; y < mScreenHeight; y += tile.getHeight() ) {
                canvas.drawBitmap(tile, x, y, null);
            }
        }
    }
}
