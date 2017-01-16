package suxin.dribble.view.shot_list;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import suxin.dribble.R;
import suxin.dribble.model.Shot;
import suxin.dribble.utils.ImageUtil;
import suxin.dribble.utils.ModelUtil;
import suxin.dribble.view.base.BaseViewHolder;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.shot_detail.ShotActivity;
import suxin.dribble.view.shot_detail.ShotFragment;

/**
 * Created by suxin on 10/22/16.
 */

class ShotListAdapter extends InfiniteAdapter<Shot> {

    private final ShotListFragment shotListFragment;
    private String username;
    private String userfigure;
    private boolean flag;

    public ShotListAdapter(@NonNull ShotListFragment shotListFragment,
                           @NonNull List<Shot> data,
                           @NonNull InfiniteAdapter.LoadMoreListener loadMoreListener) {
        super(shotListFragment.getContext(), data, loadMoreListener);
        this.shotListFragment = shotListFragment;
    }

    public ShotListAdapter(@NonNull ShotListFragment shotListFragment,
                           @NonNull List<Shot> data,
                           @NonNull InfiniteAdapter.LoadMoreListener loadMoreListener,
                           @NonNull String username,
                           @NonNull String userfigure) {
        super(shotListFragment.getContext(), data, loadMoreListener);
        this.shotListFragment = shotListFragment;
        this.username = username;
        this.userfigure = userfigure;
        flag = true;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.list_item_shot, parent, false);
        return new ShotViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {
        ShotViewHolder shotViewHolder = (ShotViewHolder) holder;

        final Shot shot = getData().get(position);


        shotViewHolder.commentCount.setText(String.valueOf(shot.comments_count));
        shotViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
        shotViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
        shotViewHolder.viewCount.setText(String.valueOf(shot.views_count));

        ImageUtil.loadShotImage(shot, shotViewHolder.image);

        shotViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShotActivity.class);
                intent.putExtra(ShotFragment.KEY_SHOT,
                        ModelUtil.toString(shot, new TypeToken<Shot>() {
                        }));
                intent.putExtra(ShotActivity.KEY_SHOT_TITLE, shot.title);
                if (flag) {
                    intent.putExtra(ShotActivity.KEY_SHOT_USERNAME, username);
                    intent.putExtra(ShotActivity.KEY_SHOT_USERFIGURE, userfigure);
                    intent.putExtra(ShotActivity.FLAG, flag);
                }
                shotListFragment.startActivityForResult(intent, ShotListFragment.REQ_CODE_SHOT);
            }
        });
    }
}
