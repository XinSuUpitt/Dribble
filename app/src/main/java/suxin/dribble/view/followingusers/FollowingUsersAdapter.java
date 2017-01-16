package suxin.dribble.view.followingusers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import suxin.dribble.R;
import suxin.dribble.model.User;
import suxin.dribble.utils.ImageUtil;
import suxin.dribble.utils.ModelUtil;
import suxin.dribble.view.base.BaseViewHolder;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.user_list.UserActivity;

/**
 * Created by suxin on 12/5/16.
 */

public class FollowingUsersAdapter extends InfiniteAdapter<User> {

    private final FollowingUsersFragment followingUsersFragment;

    public FollowingUsersAdapter(@NonNull FollowingUsersFragment followingUsersFragment, @NonNull List<User> data, @NonNull LoadMoreListener loadMoreListener) {
        super(followingUsersFragment.getContext(), data, loadMoreListener);
        this.followingUsersFragment = followingUsersFragment;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.list_item_userfollowers, parent, false);
        return new FollowingUsersViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {
        FollowingUsersViewHolder followingUsersViewHolder = (FollowingUsersViewHolder) holder;

        final User user = getData().get(position);
        followingUsersViewHolder.userfollowersauthor.setText(user.followee.name);
        ImageUtil.loadUserPicture(getContext(),
                followingUsersViewHolder.userfollowersauthorimage,
                user.followee.avatar_url);

        followingUsersViewHolder.userfollowerslocation.setText(user.followee.location);
        followingUsersViewHolder.userfollowersweblink.setText(user.followee.links.get("web"));



//        followingUsersViewHolder.userfollowersauthorimage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), UserActivity.class);
//                intent.putExtra(UserActivity.USERINFO, ModelUtil.toString(user.followee, new TypeToken<User>() {
//                }));
//                intent.putExtra(UserActivity.USERNAME, user.followee.name);
//                followingUsersFragment.startActivity(intent);
//            }
//        });

        followingUsersViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra(UserActivity.USERINFO, ModelUtil.toString(user.followee, new TypeToken<User>() {
                }));
                intent.putExtra(UserActivity.USERNAME, user.followee.name);
                followingUsersFragment.startActivity(intent);
            }
        });
    }

}
