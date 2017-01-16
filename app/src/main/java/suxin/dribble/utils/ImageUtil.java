package suxin.dribble.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import suxin.dribble.R;
import suxin.dribble.model.Shot;

/**
 * Created by suxin on 10/21/16.
 */

public class ImageUtil {
    public static void loadUserPicture(@NonNull final Context context,
                                       @NonNull ImageView imageView,
                                       @NonNull String url) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(ContextCompat.getDrawable(context, R.drawable.user_picture_placeholder))
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        view.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public static void loadShotImage(@NonNull Shot shot, SimpleDraweeView imageView) {

        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
        ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
        progressBarDrawable.setColor(0x80EA4C89);
        progressBarDrawable.setBarWidth(12);
        hierarchy.setProgressBarImage(progressBarDrawable);

        String imageUrl = shot.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            //Uri imageUri = Uri.parse(imageUrl);
            if (shot.animated) {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(imageUrl))
                        .setAutoPlayAnimations(true)
                        .build();
                imageView.setController(controller);
            } else {
                imageView.setImageURI(Uri.parse(imageUrl));
            }
        }

//        String imageUrl = shot.getImageUrl();
//        if (!TextUtils.isEmpty(imageUrl)) {
//            Glide.with(holder.itemView.getContext())
//                    .load(imageUrl)
//                    .placeholder(R.drawable.shot_placeholder)
//                    .into(imageView);
//        }
    }
}
