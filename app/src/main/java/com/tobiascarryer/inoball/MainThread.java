package com.tobiascarryer.inoball;

import android.graphics.Canvas;
import android.icu.math.BigDecimal;
import android.util.Log;
import android.view.SurfaceHolder;

import java.math.BigInteger;

/**
 * Created by Tobias Carryer on 2017-04-17.
 *
 * This thread endlessly loops while calling game logic and redrawing the screen to make
 * the game interactive.
 *
 * The FPS is extremely low when the game is run on emulators with the debugger attached
 * but it performs fine on real devices.
 */

final class MainThread extends Thread {
    private final int millisecondsPerSecond = 1000;
    static final int FPS = 60;

    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;

    private static Canvas canvas;

    MainThread( SurfaceHolder surfaceHolder, GamePanel gamePanel ) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        long startTime;
        long totalTime = 0;
        int frameCount = 0;

        while(running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                //Start editing the pixels
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    //Call any game logic
                    gamePanel.update();

                    //Redraw the pixels, lockCanvas must be called before this
                    gamePanel.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if( canvas != null ) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch(Exception e){e.printStackTrace();}
                }
            }

            totalTime += System.nanoTime()-startTime;
            frameCount++;

            //If a whole second worth of frames has passed
            if( frameCount == FPS ) {
                //Output the game's average FPS if the game is being debuged
                if( BuildConfig.DEBUG ) {
                    long millisecondsSpentPerFrame = nanosecondsToMilliseconds(totalTime) / frameCount;
                    averageFPS = millisecondsPerSecond/millisecondsSpentPerFrame;
                    Log.i("Average FPS", String.valueOf(averageFPS));
                }

                //Reset values
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    /**
     * Post: The game is running or the game has paused.
     * @param isRunning A boolean
     */
    final void setRunning(boolean isRunning) {
        running = isRunning;
    }

    /**
     * Pre: A Number of nanoseconds but not a BigDecimal or a BigInteger.
     * @return A long with the nanoseconds as milliseconds rounded down.
     */
    final int nanosecondsToMilliseconds(Number nanoNumber) {
        assert !(nanoNumber instanceof BigDecimal);
        assert !(nanoNumber instanceof BigInteger);

        long nanoseconds = nanoNumber.longValue();
        final int nanosecondsPerMillisecond = 1000000;
        return (int) (nanoseconds / nanosecondsPerMillisecond);
    }
}
