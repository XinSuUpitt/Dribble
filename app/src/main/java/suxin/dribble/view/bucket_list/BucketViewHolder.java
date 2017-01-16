package suxin.dribble.view.bucket_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import suxin.dribble.R;
import suxin.dribble.view.base.BaseViewHolder;

/**
 * Created by suxin on 10/22/16.
 */

public class BucketViewHolder extends BaseViewHolder {

    @BindView(R.id.bucket_layout)
    View bucketLayout;
    @BindView(R.id.bucket_name)
    TextView bucketName;
    @BindView(R.id.bucket_shot_count) TextView bucketCount;
    @BindView(R.id.bucket_shot_chosen)
    ImageView bucketChosen;

    public BucketViewHolder(View itemView) {
        super(itemView);
    }
}
