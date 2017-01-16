package suxin.dribble.view.attachment_list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;

import java.text.DecimalFormat;
import java.util.List;

import suxin.dribble.R;
import suxin.dribble.model.Attachment;
import suxin.dribble.utils.ImageUtil;
import suxin.dribble.view.base.BaseViewHolder;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.comment_list.ShotCommentViewHolder;
import suxin.dribble.view.shot_detail.CapturePhotoUtils;

/**
 * Created by suxin on 10/30/16.
 */

public class AttachmentListViewAdapter extends InfiniteAdapter<Attachment> {

    private AttachmentListFragment attachmentListFragment;
    private String shotid;

    public AttachmentListViewAdapter(@NonNull AttachmentListFragment attachmentListFragment, @NonNull List<Attachment> data, @NonNull LoadMoreListener loadMoreListener, @NonNull String shotid) {
        super(attachmentListFragment.getContext(), data, loadMoreListener);
        this.attachmentListFragment = attachmentListFragment;
        this.shotid = shotid;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.attachment_cardview, parent, false);
        return new AttachmentListViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {
        final AttachmentListViewHolder attachmentListViewHolder = (AttachmentListViewHolder) holder;
        final Attachment attachment = getData().get(position);
        float size = new Float(attachment.size)/1024/1024;
        DecimalFormat df = new DecimalFormat("#.00");
        attachmentListViewHolder.attachment_sizeview.setText(String.valueOf(df.format(size)) + "Mb");
        attachmentListViewHolder.attachment_view_count.setText(String.valueOf(attachment.views_count));

        GenericDraweeHierarchy hierarchy = attachmentListViewHolder.attachment_image.getHierarchy();
        ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
        progressBarDrawable.setColor(0x80EA4C89);
        progressBarDrawable.setBarWidth(12);
        hierarchy.setProgressBarImage(progressBarDrawable);

        String imageUrl = attachment.thumbnail_url;
        //String imageUrl = attachment.url;
        if (!TextUtils.isEmpty(imageUrl)) {
            attachmentListViewHolder.attachment_image.setImageURI(Uri.parse(imageUrl));
        }

        attachmentListViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(attachment.url));
                getContext().startActivity(browserIntent);
                Toast.makeText(getContext(), R.string.how_to_save_image, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
