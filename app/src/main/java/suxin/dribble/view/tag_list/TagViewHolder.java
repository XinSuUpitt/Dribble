package suxin.dribble.view.tag_list;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import suxin.dribble.R;
import suxin.dribble.view.base.BaseViewHolder;

/**
 * Created by suxin on 10/24/16.
 */

public class TagViewHolder extends BaseViewHolder {

    @BindView(R.id.tag_cardView) View tagCardView;
    @BindView(R.id.tag_content) TextView tagContent;
    public TagViewHolder(View itemView) {
        super(itemView);
    }
}
