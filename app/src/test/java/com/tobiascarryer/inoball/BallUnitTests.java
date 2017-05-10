package com.tobiascarryer.inoball;

import android.graphics.Bitmap;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class BallUnitTests {
    @Test
    public void canChangeX() throws Exception {
        Ball ball = new Ball(null, 20, 2) {
            @Override
            Bitmap createScaledBall() {
                return null;
            }
        };

        assertEquals(0, ball.getGameX());
        ball.setGameX(999999);
        assertEquals(999999, ball.getGameX());
        ball.setGameX(-1);
        assertEquals(-1, ball.getGameX());
        ball.setGameX(100);
        assertEquals(100, ball.getGameX());
        assertEquals(120, ball.getCenterX());
    }

    @Test
    public void canChangeY() throws Exception {
        Ball ball = new Ball(null, 20, 2) {
            @Override
            Bitmap createScaledBall() {
                return null;
            }
        };

        assertEquals(0, ball.getGameY());
        ball.setGameY(999999);
        assertEquals(999999, ball.getGameY());
        ball.setGameY(-1);
        assertEquals(-1, ball.getGameY());
        ball.setGameY(100);
        assertEquals(100, ball.getGameY());
        assertEquals(120, ball.getCenterY());
    }

    @Test
    public void canSetRadius() throws Exception {
        Ball ball = new Ball(null, 10, 2) {
            @Override
            Bitmap createScaledBall() {
                return null;
            }
        };
        assertEquals(10, ball.getRadius());

        Ball otherBall = new Ball(null, 1000000, 2) {
            @Override
            Bitmap createScaledBall() {
                return null;
            }
        };
        assertEquals(1000000, otherBall.getRadius());
    }

    @Test
    public void canRandomizeVelocity() throws Exception {
        Ball ball = new Ball(null, 1, 4) {
            @Override
            Bitmap createScaledBall() {
                return null;
            }
        };

        ball.resetToStartingPosition();
        int oldX = ball.getGameX();
        int oldY = ball.getGameY();
        ball.randomizeVelocity(4); //Actual FPS will be higher. This just proves whether the ball moves any amount.
        ball.update(null);
        assertTrue(oldX!=ball.getGameX());
        assertTrue(oldY!=ball.getGameY());

        ball.resetToStartingPosition();
        oldX = ball.getGameX();
        oldY = ball.getGameY();
        ball.randomizeVelocity(4); //Actual FPS will be higher. This just proves whether the ball moves any amount.
        ball.update(null);
        assertTrue(oldX!=ball.getGameX());
        assertTrue(oldY!=ball.getGameY());
    }
}