package suxin.dribble.view.bucket_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import suxin.dribble.R;
import suxin.dribble.model.Bucket;
import suxin.dribble.view.base.BaseViewHolder;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.shot_list.ShotListFragment;

/**
 * Created by suxin on 10/22/16.
 */

public class BucketListAdapter extends InfiniteAdapter<Bucket> {

    private boolean isChoosingMode;
    private boolean isDeletingMode;

    public BucketListAdapter(@NonNull Context context,
                             @NonNull List<Bucket> data,
                             @NonNull InfiniteAdapter.LoadMoreListener loadMoreListener,
                             boolean isChoosingMode,
                             boolean isDeletingMode) {
        super(context, data, loadMoreListener);
        this.isChoosingMode = isChoosingMode;
        this.isDeletingMode = isDeletingMode;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.list_item_bucket, parent, false);
        return new BucketViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, final int position) {
        final Bucket bucket = getData().get(position);
        final BucketViewHolder bucketViewHolder = (BucketViewHolder) holder;

        bucketViewHolder.bucketName.setText(bucket.name);
        bucketViewHolder.bucketCount.setText(formatShotCount(bucket.shots_count));

        if (isChoosingMode) {
            bucketViewHolder.bucketChosen.setVisibility(View.VISIBLE);
            bucketViewHolder.bucketChosen.setImageDrawable(
                    bucket.isChoosing
                            ? ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_black_24dp)
                            : ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_outline_blank_black_24dp));
            bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bucket.isChoosing = !bucket.isChoosing;
                    notifyItemChanged(position);
                }
            });
        } else {
            bucketViewHolder.bucketChosen.setVisibility(View.GONE);
            bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), BucketShotListActivity.class);
                    intent.putExtra(ShotListFragment.KEY_BUCKET_ID, bucket.id);
                    intent.putExtra(BucketShotListActivity.KEY_BUCKET_NAME, bucket.name);
                    getContext().startActivity(intent);
                }
            });
        }
    }

    private String formatShotCount(int shotCount) {
        return shotCount == 0
                ? getContext().getString(R.string.shot_count_single, shotCount)
                : getContext().getString(R.string.shot_count_plural, shotCount);
    }
}
