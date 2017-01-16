package suxin.dribble.view.attachment_list;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.w3c.dom.Text;

import butterknife.BindView;
import suxin.dribble.R;
import suxin.dribble.view.base.BaseViewHolder;

/**
 * Created by suxin on 10/30/16.
 */

public class AttachmentListViewHolder extends BaseViewHolder {

    @BindView(R.id.attachment_image)
    SimpleDraweeView attachment_image;
    @BindView(R.id.size_view_count)
    TextView attachment_sizeview;
    @BindView(R.id.attachmentview_count)
    TextView attachment_view_count;
    @BindView(R.id.attachment_clickable_cover) View cover;

    public AttachmentListViewHolder(View itemView) {
        super(itemView);
    }
}
