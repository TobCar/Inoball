package com.tobiascarryer.inoball;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * Created by Tobias Carryer on 2017-04-17.
 * A ball that jumps up and horizontally in a random direction every time it is tapped.
 * The ball ends the game if it falls off the bottom edge of the game panel.
 */

class Ball implements DrawableObject {
    private int mGameX = 0;
    private int mGameY = 0;

    private WeakReference<Bitmap> mBallImage;
    private int mRadius;
    private Resources mResources;

    //Velocities are measured in game pixels per frame
    private double mXVelocity, mYVelocity, mHorizontalDeceleration, mGravity;
    private double mMinXVelocity, mMinYVelocity;
    private double mMaxXVelocity, mMaxYVelocity, mMaxHorizontalDeceleration;

    /**
     * Create a new ball object at position (0,0)
     *
     * @param resources
     * @param radius The radius of the ball in pixels
     * @param fps The number of times ball.update() is called in a second.
     */
    Ball( Resources resources, int radius, int fps ) {
        mResources = resources;
        mRadius = radius;

        mBallImage = new WeakReference<>(createScaledBall());

        setVelocityUpperBounds(fps);
        setVelocityLowerBounds();
    }

    /**
     * Post: mMaxXVelocity, mMaxYVelocity, and mMaxHorizontalDeceleration are set to non-zero values.
     * @param fps The number of times ball.update() is called in a second.
     */
    private void setVelocityUpperBounds(int fps) {
        /* This formula is a rearranged version of the area equation for a velocity over time graph.
        velocity * time / 2 = the distance travelled. Rearranged to: velocity = 2 * distance travelled / time
        In this case, the distance travelled to the edge of the screen and back is known
        (GamePanel.WIDTH)-getRadius() and an arbitrary number of seconds is picked. */
        final int secondsToReachEdge = fps/2; //Half a second
        mMaxXVelocity = 2*((GamePanel.WIDTH)-getRadius())/secondsToReachEdge;

        //Slope of the line in the velocity over time diagram.
        mMaxHorizontalDeceleration = mMaxXVelocity / secondsToReachEdge;

        assert(mMaxXVelocity > 0);

        /* This formula is like the max X velocity formula. The distance travelled to the top of
        the screen is the height of the ball and an arbitrary number of seconds is picked. */
        final int secondsToReachTop = (int)(1.1 * fps);
        mMaxYVelocity = 2*(GamePanel.HEIGHT-(getRadius()*2))/secondsToReachTop;

        assert(mMaxYVelocity > 0);
    }

    /**
     * Post: mMinXVelocity and mMinYVelocity are set to a percentage of the upper bounds.
     */
    private void setVelocityLowerBounds() {
        mMinXVelocity = mMaxXVelocity * 0.75;
        mMinYVelocity = mMaxYVelocity * 0.75;
    }

    /**
     * Pre: GamePanel WIDTH and HEIGHT are initialized.
     * Post: Ball is at the bottom center of the game screen. Velocities are zero.
     */
    final void resetToStartingPosition() {
        setGameX((GamePanel.WIDTH/2)-getRadius());
        setGameY(GamePanel.HEIGHT-(getRadius()*2));
        mXVelocity = 0;
        mYVelocity = 0;
        mHorizontalDeceleration = 0;
        mGravity = 0;
    }

    /**
     * @param newX the Ball's new X position
     * Post: getGameX() returns newX
     */
    final void setGameX( int newX ) {
        mGameX = newX;
    }

    /**
     * @param newY the Ball's new Y position
     * Post: getGameY() returns newY
     */
    final void setGameY( int newY ) {
        mGameY = newY;
    }

    /**
     * Pre: The Ball has an X position within the game panel
     * @return The X position within the game panel.
     */
    final int getGameX() {
        return mGameX;
    }

    /**
     * Pre: The Ball has an Y position within the game panel
     * @return The Y position within the game panel.
     */
    final int getGameY() {
        return mGameY;
    }

    /**
     * Pre: The Ball has a width.
     * @return Half the ball's width / the radius of the ball.
     */
    final int getRadius() {
        return mRadius;
    }

    /**
     * Pre: The Ball has an X position.
     * @return The center of the ball along the X axis.
     */
    final int getCenterX() {
        return getGameX()+getRadius();
    }

    /**
     * Pre: The Ball has a Y position.
     * @return The center of the ball along the Y axis.
     */
    final int getCenterY() {
        return getGameY()+getRadius();
    }

    /**
     * Pre: scoreManager is not null. The ball is moving downwards.
     * Post: The ball is moving in a random direction and random speed upwards.
     * @param fps The number of times ball.update() is called in a second.
     */
    final void wasTapped(ScoreManager scoreManager, int fps, Activity activity) {
        if( scoreManager == null ) {
            Log.e("ERROR", "scoreManager was unexpectedly null.");
        } else if( mYVelocity >= 0 ){
            scoreManager.increaseScore(activity);
            randomizeVelocity(fps);
        }
    }

    /**
     * Post: Horizontal and vertical velocities are randomized within their limits.
     *       Horizontal deceleration and gravity are randomized to non-zero values.
     * @param fps The number of times ball.update() is called in a second.
     */
    final void randomizeVelocity(int fps) {
        //Randomize magnitude
        mXVelocity = (new Random().nextDouble() * (mMaxXVelocity - mMinXVelocity)) + mMinXVelocity;
        mYVelocity = -((new Random().nextDouble() * (mMaxYVelocity - mMinYVelocity)) + mMinYVelocity); //Negative velocity = up

        //Randomize direction
        if( new Random().nextBoolean() ) {
            mXVelocity = -mXVelocity;
        }

        //Randomize deceleration and gravity
        mHorizontalDeceleration = new Random().nextDouble() * mMaxHorizontalDeceleration;

        int minSecondsToFall = 2 * fps;
        minSecondsToFall *= Math.random() + 1; //Can increase the seconds by nearly 100%
        mGravity = Math.abs(mYVelocity / minSecondsToFall);
    }

    /**
     * Pre: A frame has passed in the game.
     * Post: The ball has changed positions depending on its velocity. The game has ended if
     * the ball fell off the bottom of the screen.
     */
    final void update(ScoreManager scoreManager) {
        final int ballDiameter = getRadius()*2;

        mGameX += mXVelocity;
        mGameY += mYVelocity;

        if( mXVelocity > 0 ) {
            mXVelocity -= mHorizontalDeceleration;
            if( mXVelocity < 0 )
                mXVelocity = 0;
        } else if( mXVelocity < 0 ) {
            mXVelocity += mHorizontalDeceleration;
            if( mXVelocity > 0 )
                mXVelocity = 0;
        }

        //Stop ball from moving out of the screen horizontally
        if( getGameX() < 0 ) {
            setGameX(0);
            mXVelocity = -mXVelocity;
        } else if ( getGameX()+ballDiameter > GamePanel.WIDTH ) {
            setGameX(GamePanel.WIDTH-ballDiameter);
            mXVelocity = -mXVelocity;
        }

        //Prevent ball from going to far above the screen
        if( getGameY() < -ballDiameter*2 ) {
            mYVelocity = mGravity; //If this was 0, it would get stuck
        }

        //Game is over is ball falls off the bottom of the screen
        mYVelocity += mGravity;
        mGravity *= 1.07;
        if( scoreManager != null && mGameY > GamePanel.HEIGHT )
            gameOver(scoreManager);
    }

    /**
     * Pre: scoreManager is not null.
     * Post: The game has ended and the high score has been saved to the phone's memory.
     */
    private void gameOver(ScoreManager scoreManager) {
        if( scoreManager == null ) {
            Log.e("ERROR", "scoreManager is unexpectedly null");
        } else {
            resetToStartingPosition();
            scoreManager.resetScore();
        }
    }

    /**
     * Pre: Canvas is being displayed
     * @param canvas The game's canvas. Cannot be null.
     */
    public void draw(Canvas canvas) {
        assert canvas != null;

        if( mBallImage.get() != null ) {
            canvas.drawBitmap(mBallImage.get(), getGameX(), getGameY(), null);
        } else {
            Log.i("WeakReference", "Recreated scaled ball image");
            mBallImage = new WeakReference<>(createScaledBall());
        }
    }

    /**
     * Pre: There is a drawable named "ball", mResources is not null, radius is positive.
     * @return A Bitmap scaled to the radius specified.
     */
    Bitmap createScaledBall() {
        assert  mResources != null;

        Bitmap originalBallImage = BitmapFactory.decodeResource(mResources, R.drawable.ball);
        int newDim = getRadius()*2;
        return Bitmap.createScaledBitmap(originalBallImage, newDim, newDim, true );
    }
}
