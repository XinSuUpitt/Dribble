package suxin.dribble.view.user_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import suxin.dribble.view.base.SingleFragmentActivity;

/**
 * Created by suxin on 10/27/16.
 */

public class UserActivity extends SingleFragmentActivity {

    public static final String USERINFO = "user_info";
    public static final String USERNAME = "user_name";

    public static final String AUTHORUSER = "author_user";

    @NonNull
    @Override
    protected Fragment newFragment() {
        return UserFragment.newInstance(getIntent().getStringExtra(USERINFO));
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(USERNAME);
    }
}
