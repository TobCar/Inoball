package com.tobiascarryer.inoball;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by TobiasC on 2017-04-17.
 */

final class ScoreManager {

    private final String highScoreKey = "HIGH_SCORE_KEY";

    private int score = 0;
    private int highScore = 0;

    /**
     * Pre: Activity is not null and it can access the user's private app preferences.
     * Post: The score manager contains the high score saved to the user's device.
     */
    ScoreManager(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        highScore = sharedPref.getInt(highScoreKey, 0); //Default high score of 0
    }

    /**
     * Pre: Activity is not null and it can access the user's private app preferences.
     * Post: Internally stored score is higher by one.
     */
    final void increaseScore(Activity activity) {
        score++;
        if( score > highScore ) {
            highScore = score;
            saveHighScore(activity);
        }
    }

    /**
     * Pre: Activity is not null and it can access the user's private app preferences.
     * Post: The high score is committed to the device's memory in the background.
     */
    private void saveHighScore(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(highScoreKey, highScore);
        editor.apply();
    }

    /**
     * Post: The internally stored score is back to its default value.
     */
    final void resetScore() {
        score = 0;
    }

    /**
     * Pre: canvas is being displayed. Resources is not null.
     * @param canvas The game's canvas. Cannot be null.
     * @param resources The app's resources.
     */
    void draw(Canvas canvas, Resources resources) {
        assert canvas != null;
        assert resources != null;

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(canvas.getHeight()/4);
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(String.valueOf(score), canvas.getWidth()/2, canvas.getHeight()/2+(paint.getTextSize()/3), paint);

        Paint smallPaint = new Paint();
        smallPaint.setColor(Color.WHITE);
        smallPaint.setStyle(Paint.Style.FILL);
        smallPaint.setTextSize(paint.getTextSize()/4);
        smallPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(String.valueOf(highScore), canvas.getWidth()/2, smallPaint.getTextSize()*2, smallPaint);

        Paint xsPaint = new Paint();
        xsPaint.setColor(Color.WHITE);
        xsPaint.setStyle(Paint.Style.FILL);
        xsPaint.setTextSize(smallPaint.getTextSize()/2);
        xsPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(resources.getString(R.string.high_score), canvas.getWidth()/2, smallPaint.getTextSize()*2+xsPaint.getTextSize()*2, xsPaint);
    }
}
