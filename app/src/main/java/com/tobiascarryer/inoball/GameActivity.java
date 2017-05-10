package com.tobiascarryer.inoball;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by Tobias Carryer on 2017-04-17.
 */

public final class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //If the Android version is lower than Jellybean, use this call to hide the status bar.
        if ( Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            if( actionBar != null )
                actionBar.hide();
            else
                Log.e("Unexpected Null", "ActionBar could not be found");
        }

        setContentView(new GamePanel(this));
    }
}
