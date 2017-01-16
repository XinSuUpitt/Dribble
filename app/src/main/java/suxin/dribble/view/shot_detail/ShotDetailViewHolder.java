package suxin.dribble.view.shot_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import suxin.dribble.R;
import suxin.dribble.view.base.BaseViewHolder;

/**
 * Created by suxin on 10/22/16.
 */

class ShotDetailViewHolder extends BaseViewHolder {

    @BindView(R.id.shot_title)
    TextView title;
    @BindView(R.id.shot_description) TextView description;
    @BindView(R.id.shot_author_picture)
    ImageView authorPicture;
    @BindView(R.id.shot_author_name) TextView authorName;
    @BindView(R.id.shot_comment_count) TextView commentCount;
    @BindView(R.id.shot_like_count) TextView likeCount;
    @BindView(R.id.shot_view_count) TextView viewCount;
    @BindView(R.id.shot_bucket_count) TextView bucketCount;
    @BindView(R.id.shot_action_comment) ImageButton commentButton;
    @BindView(R.id.shot_action_like)
    ImageButton likeButton;
    @BindView(R.id.shot_action_bucket) ImageButton bucketButton;
    @BindView(R.id.shot_action_share) TextView shareButton;
    @BindView(R.id.tag_numbers) TextView tagNumbers;
    @BindView(R.id.tag_button) ImageButton tagButton;
    @BindView(R.id.shot_attachment_count) TextView attachments;
    @BindView(R.id.attachmentbutton) ImageButton attachmentBtn;

    public ShotDetailViewHolder(View itemView) {
        super(itemView);
    }
}
