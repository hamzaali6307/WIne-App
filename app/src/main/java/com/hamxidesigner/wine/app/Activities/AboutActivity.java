package com.hamxidesigner.wine.app.Activities;

import android.app.Activity;
import android.os.Bundle;
import com.hamxidesigner.wine.app.R;

/**
 * Created by user on 12/31/15.
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.about_layout );

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed ( );
        overridePendingTransition ( R.anim.slide_out, R.anim.slide_in );
        finish ();
    }
}

