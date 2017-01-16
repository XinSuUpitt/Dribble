package suxin.dribble;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by suxin on 10/22/16.
 */

public class DribbbleApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
