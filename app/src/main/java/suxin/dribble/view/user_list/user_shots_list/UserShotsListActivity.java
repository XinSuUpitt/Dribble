package suxin.dribble.view.user_list.user_shots_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import suxin.dribble.view.base.SingleFragmentActivity;
import suxin.dribble.view.shot_list.ShotListFragment;

/**
 * Created by suxin on 10/28/16.
 */

public class UserShotsListActivity extends SingleFragmentActivity {

    public static final String KEY_USERSHOTS_USERID = "usershotId";
    public static final String KEY_USERSHOTS_USERNAME = "usershotname";
    public static final String KEY_USERSHOTS_USERFIGURE = "usershotfigure";

    @NonNull
    @Override
    protected Fragment newFragment() {
        String userid = getIntent().getStringExtra(KEY_USERSHOTS_USERID);
        String user_Avatur_url = getIntent().getStringExtra(KEY_USERSHOTS_USERFIGURE);
        String username = getIntent().getStringExtra(KEY_USERSHOTS_USERNAME);
        return ShotListFragment.newUserShotsListInstance(userid, username, user_Avatur_url);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_USERSHOTS_USERNAME);
    }
}
