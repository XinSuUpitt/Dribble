package suxin.dribble.view.comment_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import suxin.dribble.R;
import suxin.dribble.view.base.BaseViewHolder;

/**
 * Created by suxin on 10/24/16.
 */

public class ShotCommentViewHolder extends BaseViewHolder {

    @BindView(R.id.comment_author_picture) ImageView commentAuthorPicture;
    @BindView(R.id.comment_author) TextView commentAuthorName;
    @BindView(R.id.shot_comment_item) TextView comment;
//    @BindView(R.id.comment_like_count) TextView commentLikeCount;

    public ShotCommentViewHolder(View itemView) {
        super(itemView);
    }
}
