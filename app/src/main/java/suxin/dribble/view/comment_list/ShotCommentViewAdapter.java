package suxin.dribble.view.comment_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import suxin.dribble.R;
import suxin.dribble.model.Comment;
import suxin.dribble.model.User;
import suxin.dribble.utils.ImageUtil;
import suxin.dribble.utils.ModelUtil;
import suxin.dribble.view.base.BaseViewHolder;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.shot_detail.ShotFragment;
import suxin.dribble.view.user_list.UserActivity;

/**
 * Created by suxin on 10/24/16.
 */

public class ShotCommentViewAdapter extends InfiniteAdapter<Comment> {

    private final ShotListCommentFragment shotListCommentFragment;

    public ShotCommentViewAdapter(@NonNull ShotListCommentFragment shotListCommentFragment, @NonNull List<Comment> data, @NonNull LoadMoreListener loadMoreListener) {
        super(shotListCommentFragment.getContext(), data, loadMoreListener);
        this.shotListCommentFragment = shotListCommentFragment;
    }



    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.list_item_comment, parent, false);
        return new ShotCommentViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {
        ShotCommentViewHolder shotCommentViewHolder = (ShotCommentViewHolder) holder;

        final Comment comment = getData().get(position);
        shotCommentViewHolder.commentAuthorName.setText(comment.user.name);

        String plainText = Html.fromHtml(comment.body).toString();
        shotCommentViewHolder.comment.setText(plainText);
//        shotCommentViewHolder.commentLikeCount.setText(String.valueOf(comment.like_count));

        ImageUtil.loadUserPicture(getContext(),
                shotCommentViewHolder.commentAuthorPicture,
                comment.user.avatar_url);

        shotCommentViewHolder.commentAuthorPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra(UserActivity.USERINFO, ModelUtil.toString(comment.user, new TypeToken<User>(){}));
                intent.putExtra(UserActivity.USERNAME, comment.user.name);
                shotListCommentFragment.startActivity(intent);
            }
        });
    }
}
