package suxin.dribble.view.shot_detail;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import suxin.dribble.view.base.SingleFragmentActivity;

/**
 * Created by suxin on 10/22/16.
 */

public class ShotActivity extends SingleFragmentActivity {

    public static final String KEY_SHOT_TITLE = "shot_title";
    public static final String KEY_SHOT_ID = "shot_id";
    public static final String KEY_SHOT_USERNAME = "shot_username";
    public static final String KEY_SHOT_USERFIGURE = "shot_userfigure";
    public static final String FLAG = "flag";
    @NonNull
    @Override
    protected Fragment newFragment() {
        return ShotFragment.newInstance(getIntent().getExtras());
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_SHOT_TITLE);
    }
}
