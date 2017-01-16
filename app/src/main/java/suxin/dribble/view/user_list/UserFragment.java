package suxin.dribble.view.user_list;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;
import suxin.dribble.R;
import suxin.dribble.dribble.Dribbble;
import suxin.dribble.dribble.DribbbleException;
import suxin.dribble.model.User;
import suxin.dribble.utils.ImageUtil;
import suxin.dribble.utils.ModelUtil;
import suxin.dribble.view.base.DribbbleTask;
import suxin.dribble.view.user_list.user_shots_list.UserShotsListActivity;

/**
 * Created by suxin on 10/27/16.
 */

public class UserFragment extends Fragment {

    public static final String KEY_USER = "user";
    public static final String KEY_USER_ID = "user_id";

    public String userName;
    private User user;
    private String userInfo;
    private boolean isFollowing;

    @BindView(R.id.info_author_picture) ImageView imageView;
    @BindView(R.id.info_author_name) TextView infoAuthorName;
    @BindView(R.id.info_author_location) TextView infoAuthorLocation;
    @BindView(R.id.info_website) TextView infoWebsite;
    @BindView(R.id.info_twitter) TextView infoTwitter;
    @BindView(R.id.info_shots) TextView infoShots;
    @BindView(R.id.info_shots_layout) View infoShotsLayout;
    @BindView(R.id.info_followers) TextView infoFollowers;
    @BindView(R.id.info_followers_layout) View infoFollowersLayout;
    @BindView(R.id.info_following) TextView infoFollowing;
    @BindView(R.id.info_following_layout) View infoFollowingLayout;
    @BindView(R.id.info_team) TextView infoTeam;
    @BindView(R.id.info_team_layout) View infoTeamLayout;
    @BindView(R.id.info_like) TextView infoLike;
    @BindView(R.id.info_like_layout) View infoLikeLayout;
    @BindView(R.id.follow_button) ImageButton followBtn;

    public static UserFragment newInstance(String userinfo) {
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userinfo);
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.author_info, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        userInfo = getArguments().getString(KEY_USER_ID);

        user = ModelUtil.toObject(userInfo, new TypeToken<User>(){});

        ImageUtil.loadUserPicture(getContext(),
                imageView,
                user.avatar_url);

        infoAuthorName.setText(user.name);
        infoAuthorLocation.setText(user.location);
        infoWebsite.setText(user.links.get("web"));
        infoTwitter.setText(user.links.get("twitter"));
        infoShots.setText(user.shots_count);
        infoFollowers.setText(user.followers_count);
        infoFollowing.setText(user.followings_count);
        infoTeam.setText(user.teams_count);
        infoLike.setText(user.likes_count);

        AsyncTaskCompat.executeParallel(new CheckFollowTask(user.id));

        Drawable likeDrawable = isFollowing
                ? ContextCompat.getDrawable(getContext(), R.mipmap.dribbble)
                : ContextCompat.getDrawable(getContext(), R.mipmap.unfollow);
        followBtn.setImageDrawable(likeDrawable);

        if (!isFollowing) {
            Toast.makeText(getContext(), R.string.follow, Toast.LENGTH_SHORT).show();
        }

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFollowing) {
                    Toast.makeText(getContext(), "Unfollow " + infoAuthorName.getText(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Follow " + infoAuthorName.getText() + " successfully!", Toast.LENGTH_SHORT).show();
                }
                follow(user.id, isFollowing);
            }
        });

        if (user.links.get("web") != null) {
            infoWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.links.get("web")));
                    startActivity(browserIntent);
                }
            });
        }

        if (user.links.get("twitter") != null) {
            infoTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.links.get("twitter")));
                    startActivity(browserIntent);
                }
            });
        }

        infoShotsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserShotsListActivity.class);
                intent.putExtra(UserShotsListActivity.KEY_USERSHOTS_USERID, user.id);
                intent.putExtra(UserShotsListActivity.KEY_USERSHOTS_USERNAME, user.name);
                intent.putExtra(UserShotsListActivity.KEY_USERSHOTS_USERFIGURE, user.avatar_url);
                startActivity(intent);
            }
        });

        infoFollowersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), infoAuthorName.getText() + " has " + infoFollowers.getText() + " followers", Toast.LENGTH_SHORT).show();
            }
        });

        infoFollowingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), infoAuthorName.getText() + " has " + infoFollowing.getText() + " followings", Toast.LENGTH_SHORT).show();

            }
        });


        infoTeamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), infoAuthorName.getText() + " has " + infoTeam.getText() + " teams", Toast.LENGTH_SHORT).show();

            }
        });

        infoLikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), infoAuthorName.getText() + " has " + infoLike.getText() + " likes", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void follow(String id, boolean isFollowing) {
        AsyncTaskCompat.executeParallel(new FollowTask(id, isFollowing));
    }


    private class CheckFollowTask extends DribbbleTask<Void, Void, Boolean> {

        private String id;

        public CheckFollowTask(String userid) {
            this.id = userid;
        }

        @Override
        protected Boolean doJob(Void... params) throws DribbbleException {
            return Dribbble.isFollowingUser(id);
        }

        @Override
        protected void onSuccess(Boolean result) {
            isFollowing = result;
            if (result) {
                //isFollowing = true;
                Drawable likeDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.dribbble);
                followBtn.setImageDrawable(likeDrawable);
                followBtn.setBackgroundColor(Color.TRANSPARENT);
            } else {
                Drawable likeDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.unfollow);
                followBtn.setImageDrawable(likeDrawable);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            isFollowing = false;
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }


    private class FollowTask extends DribbbleTask<Void, Void, Void> {

        private String id;
        private boolean follow;

        public FollowTask(String id, boolean follow) {
            this.id = id;
            this.follow = follow;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            if (!follow) {
                Dribbble.followUser(id);
            } else {
                Dribbble.unfollowUser(id);
            }
            return null;
        }

        @Override
        protected void onSuccess(Void s) {
            if (follow) {
                isFollowing = false;
            } else {
                isFollowing = true;
            }
            Drawable likeDrawable = isFollowing
                    ? ContextCompat.getDrawable(getContext(), R.mipmap.dribbble)
                    : ContextCompat.getDrawable(getContext(), R.mipmap.unfollow);
            followBtn.setImageDrawable(likeDrawable);
        }

        @Override
        protected void onFailed(DribbbleException e) {
            if (!follow) {
                isFollowing = false;
            } else {
                isFollowing = true;
            }
            Drawable likeDrawable = isFollowing
                    ? ContextCompat.getDrawable(getContext(), R.mipmap.unfollow)
                    : ContextCompat.getDrawable(getContext(), R.mipmap.dribbble);
            followBtn.setImageDrawable(likeDrawable);
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}
