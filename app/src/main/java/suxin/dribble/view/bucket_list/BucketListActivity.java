package suxin.dribble.view.bucket_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import suxin.dribble.R;
import suxin.dribble.view.base.SingleFragmentActivity;

/**
 * Created by suxin on 10/22/16.
 */

public class BucketListActivity extends SingleFragmentActivity {

    boolean isDeletingMode;
    @NonNull
    @Override
    protected Fragment newFragment() {
        boolean isChoosingMode = getIntent().getExtras().getBoolean(
                BucketListFragment.KEY_CHOOSING_MODE);
        isDeletingMode = getIntent().getExtras().getBoolean(BucketListFragment.KEY_DELETE_MODE);
        ArrayList<String> chosenBucketIds = getIntent().getExtras().getStringArrayList(
                BucketListFragment.KEY_COLLECTED_BUCKET_IDS);
        return BucketListFragment.newInstance(null, isChoosingMode, chosenBucketIds, isDeletingMode);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return isDeletingMode ? getString(R.string.delete_bucket) : getString(R.string.choose_bucket);
    }
}
