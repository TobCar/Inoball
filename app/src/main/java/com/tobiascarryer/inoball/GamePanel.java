package com.tobiascarryer.inoball;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Tobias Carryer on 2017-04-17.
 *
 * The panel holding all game events. It is running a thread which calls
 * the methods necessary to execute game logic and draw objects.
 * The game panel also defines the width and height of the game to be
 * equal to the dimensions of the screen. This may post an issue if the game
 * were to be allowed to run in split screen.
 */

final class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    static int WIDTH = 128; //Arbitrary filler value
    static int HEIGHT = 128; //Arbitrary filler value

    private MainThread thread;
    private Background mBg;
    private ScoreManager mScoreManager;
    private Ball mBall;
    private Activity mActivity;

    public GamePanel(Activity activity) {
        super(activity);

        //Add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);
        mScoreManager = new ScoreManager(activity);
        mActivity = activity;

        //Make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        while(retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            retry = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        //Set the dimensions of the game
        WIDTH = getWidth();
        HEIGHT = getHeight();

        //Set the background
        mBg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.bg), getWidth(), getHeight());

        //Create the ball
        mBall = new Ball(getResources(), WIDTH/4, MainThread.FPS);
        mBall.resetToStartingPosition();

        //Start the game loop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Only perform an action when the tap is first registered
        if( event.getAction() == MotionEvent.ACTION_DOWN ) {
            final int tapX = (int) (event.getX() * getWidth()/GamePanel.WIDTH);
            final int tapY = (int) (event.getY() * getHeight()/GamePanel.HEIGHT);
            final int a = tapX - mBall.getCenterX();
            final int b = tapY - mBall.getCenterY();
            double tapDistanceFromBall = Math.sqrt(a*a + b*b);
            if( tapDistanceFromBall <= mBall.getRadius() ) {
                mBall.wasTapped(mScoreManager, MainThread.FPS, mActivity);
            }
        }

        return false;
    }

    /**
     * Pre: A frame has passed in the game.
     */
    public final void update() {
        mBall.update(mScoreManager);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if( canvas != null ) {
            //The later a draw event is called, the higher it will be layered on the screen
            mBg.draw(canvas);
            mScoreManager.draw(canvas, getResources());
            mBall.draw(canvas);
        }
    }
}