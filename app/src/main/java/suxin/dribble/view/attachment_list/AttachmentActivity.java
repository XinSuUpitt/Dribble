package suxin.dribble.view.attachment_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import suxin.dribble.view.base.SingleFragmentActivity;
import suxin.dribble.view.shot_detail.ShotActivity;

/**
 * Created by suxin on 10/30/16.
 */

public class AttachmentActivity extends SingleFragmentActivity {

    @NonNull
    @Override
    protected Fragment newFragment() {
        return AttachmentListFragment.newInstance(getIntent().getStringExtra(ShotActivity.KEY_SHOT_ID));
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return "Attachments";
    }
}
