package suxin.dribble.view.tag_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import suxin.dribble.view.base.SingleFragmentActivity;

/**
 * Created by suxin on 10/24/16.
 */

public class TagActivity extends SingleFragmentActivity {
    @NonNull
    @Override
    protected Fragment newFragment() {
        return TagFragment.newInstance(getIntent().getStringArrayListExtra(TagFragment.TAGS));
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return "Tags";
    }
}
