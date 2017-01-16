package suxin.dribble.view.shot_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import suxin.dribble.R;

/**
 * Created by suxin on 10/22/16.
 */

class ShotImageViewHolder extends RecyclerView.ViewHolder {

    SimpleDraweeView image;

    public ShotImageViewHolder(View itemView) {
        super(itemView);
        image = (SimpleDraweeView) itemView;
        
    }
}
