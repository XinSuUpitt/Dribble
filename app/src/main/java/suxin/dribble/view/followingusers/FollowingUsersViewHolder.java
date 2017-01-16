package suxin.dribble.view.followingusers;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import suxin.dribble.R;
import suxin.dribble.view.base.BaseViewHolder;

/**
 * Created by suxin on 12/5/16.
 */

public class FollowingUsersViewHolder extends BaseViewHolder {

    @BindView(R.id.userfollowers_cardview)
    CardView cardView;
    @BindView(R.id.userfollowers_author)
    TextView userfollowersauthor;
    @BindView(R.id.userfollowers_author_picture)
    ImageView userfollowersauthorimage;
    @BindView(R.id.userfollowers_location) TextView userfollowerslocation;
    @BindView(R.id.userfollowers_weblink) TextView userfollowersweblink;

    public FollowingUsersViewHolder(View itemView) {
        super(itemView);
    }
}
