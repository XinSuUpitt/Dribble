package suxin.dribble.view.shot_detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import butterknife.ButterKnife;
import suxin.dribble.R;
import suxin.dribble.dribble.Dribbble;
import suxin.dribble.dribble.DribbbleException;
import suxin.dribble.model.Shot;
import suxin.dribble.model.User;
import suxin.dribble.utils.ImageUtil;
import suxin.dribble.utils.ModelUtil;
import suxin.dribble.view.attachment_list.AttachmentActivity;
import suxin.dribble.view.base.DribbbleTask;
import suxin.dribble.view.comment_list.CommentActivity;
import suxin.dribble.view.comment_list.ShotListCommentFragment;
import suxin.dribble.view.shot_list.ShotListFragment;
import suxin.dribble.view.tag_list.TagActivity;
import suxin.dribble.view.tag_list.TagFragment;
import suxin.dribble.view.user_list.UserActivity;

/**
 * Created by suxin on 10/22/16.
 */

class ShotAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_SHOT_IMAGE = 0;
    private static final int VIEW_TYPE_SHOT_DETAIL = 1;


    private final ShotFragment shotFragment;
    private final Shot shot;
    private String username;
    private String userfigure;
    private boolean flag;
    private ArrayList<String> tags;

    public ShotAdapter(@NonNull ShotFragment shotFragment, @NonNull Shot shot) {
        this.shotFragment = shotFragment;
        this.shot = shot;
    }

    public ShotAdapter(@NonNull ShotFragment shotFragment, @NonNull Shot shot, @NonNull String username, @NonNull String userfigure) {
        this.shotFragment = shotFragment;
        this.shot = shot;
        this.username = username;
        this.userfigure = userfigure;
        flag = true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item_shot_image, parent, false);
                return new ShotImageViewHolder(view);
            case VIEW_TYPE_SHOT_DETAIL:
                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item_shot_detail, parent, false);
                return new ShotDetailViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                ImageUtil.loadShotImage(shot, ((ShotImageViewHolder) holder).image);
                final ShotImageViewHolder shotImageViewHolder = (ShotImageViewHolder) holder;
                shotImageViewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), R.string.how_to_save_image, Toast.LENGTH_SHORT).show();
                    }
                });
                shotImageViewHolder.image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        shotFragment.download(shot.getImageUrl());

//                        shotImageViewHolder.image.setDrawingCacheEnabled(true);
//                        Bitmap bitmap = shotImageViewHolder.image.getDrawingCache();
//                        //MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, shot.title, shot.description);
//                        CapturePhotoUtils.insertImage(getContext().getContentResolver(), bitmap, shot.title, shot.description);
//                        Toast.makeText(getContext(), R.string.save_to_gallery, Toast.LENGTH_LONG).show();

                        return true;
                    }
                });
                break;
            case VIEW_TYPE_SHOT_DETAIL:
                final ShotDetailViewHolder shotDetailViewHolder = (ShotDetailViewHolder) holder;
                shotDetailViewHolder.title.setText(shot.title);
                if (!flag) {
                    shotDetailViewHolder.authorName.setText(shot.user.name);
                    ImageUtil.loadUserPicture(getContext(),
                            shotDetailViewHolder.authorPicture,
                            shot.user.avatar_url);
                } else {
                    shotDetailViewHolder.authorName.setText(username);
                    ImageUtil.loadUserPicture(getContext(),
                            shotDetailViewHolder.authorPicture,
                            userfigure);
                }

                shotDetailViewHolder.description.setText(Html.fromHtml(
                        shot.description == null ? "" : shot.description));
                shotDetailViewHolder.description.setMovementMethod(LinkMovementMethod.getInstance());


                tags = new ArrayList<String>();

                if (shot.tags != null) {
                    for (int i = 0; i < shot.tags.length; i++) {
                        String str = shot.tags[i];
                        tags.add(str);
                    }
                }

                if (shot.attachments_count != 0) {
                    shotDetailViewHolder.attachmentBtn.setVisibility(View.VISIBLE);
                    shotDetailViewHolder.attachments.setVisibility(View.VISIBLE);
                    shotDetailViewHolder.attachments.setText(String.valueOf(shot.attachments_count));

                    shotDetailViewHolder.attachmentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "attachment count clicked", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), AttachmentActivity.class);
                            intent.putExtra(ShotActivity.KEY_SHOT_ID, shot.id);
                            shotFragment.startActivity(intent);
                        }
                    });

                    shotDetailViewHolder.attachments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "attachment count clicked", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), AttachmentActivity.class);
                            intent.putExtra(ShotActivity.KEY_SHOT_ID, shot.id);
                            shotFragment.startActivity(intent);
                        }
                    });
                } else {
                    shotDetailViewHolder.attachmentBtn.setVisibility(View.INVISIBLE);
                    shotDetailViewHolder.attachments.setVisibility(View.INVISIBLE);
                }


                shotDetailViewHolder.commentCount.setText(String.valueOf(shot.comments_count));
                shotDetailViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
                shotDetailViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
                shotDetailViewHolder.viewCount.setText(String.valueOf(shot.views_count));
                shotDetailViewHolder.tagNumbers.setText(String.valueOf(tags.size()));

                shotDetailViewHolder.tagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "tag count clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), TagActivity.class);
                        intent.putExtra(TagFragment.TAGS, tags);
                        TagFragment.newInstance(tags);
                        shotFragment.startActivity(intent);
                    }
                });

                shotDetailViewHolder.tagNumbers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), TagActivity.class);
                        intent.putExtra(TagFragment.TAGS, tags);
                        TagFragment.newInstance(tags);
                        shotFragment.startActivity(intent);
                    }
                });



                if (!flag) {
                    shotDetailViewHolder.authorPicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), UserActivity.class);
                            intent.putExtra(UserActivity.USERINFO, ModelUtil.toString(shot.user, new TypeToken<User>() {
                            }));
                            intent.putExtra(UserActivity.USERNAME, shot.user.name);
                            shotFragment.startActivity(intent);
                        }
                    });
                }

                shotDetailViewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "comment count clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), CommentActivity.class);
                        intent.putExtra(ShotActivity.KEY_SHOT_ID, shot.id);
                        ShotListCommentFragment.newInstance(shot.id);
                        shotFragment.startActivity(intent);
                    }
                });
                shotDetailViewHolder.commentCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Like count clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), CommentActivity.class);
                        intent.putExtra(ShotActivity.KEY_SHOT_ID, shot.id);
                        ShotListCommentFragment.newInstance(shot.id);
                        shotFragment.startActivity(intent);
                    }
                });
                shotDetailViewHolder.likeCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotFragment.like(shot.id, !shot.liked);
                        Toast.makeText(getContext(), "Like count clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                shotDetailViewHolder.bucketCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotFragment.bucket();
                        Toast.makeText(getContext(), "Bucket count clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                shotDetailViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotFragment.like(shot.id, !shot.liked);
                    }
                });
                shotDetailViewHolder.bucketButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotFragment.bucket();
                    }
                });
                shotDetailViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotFragment.share();
                    }
                });

                Drawable likeDrawable = shot.liked
                        ? ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_dribbble_18dp)
                        : ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_black_18dp);
                shotDetailViewHolder.likeButton.setImageDrawable(likeDrawable);

                Drawable bucketDrawable = shot.bucketed
                        ? ContextCompat.getDrawable(getContext(), R.drawable.ic_inbox_dribbble_18dp)
                        : ContextCompat.getDrawable(getContext(), R.drawable.ic_inbox_black_18dp);
                shotDetailViewHolder.bucketButton.setImageDrawable(bucketDrawable);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_SHOT_IMAGE;
        } else {
            return VIEW_TYPE_SHOT_DETAIL;
        }
    }

    @NonNull
    private Context getContext() {
        return shotFragment.getContext();
    }

}
