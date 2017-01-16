package suxin.dribble.view.tag_list;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import suxin.dribble.R;

/**
 * Created by suxin on 10/24/16.
 */

public class TagViewAdapter extends RecyclerView.Adapter {

    private final TagFragment tagFragment;
    private ArrayList<String> list;

    public TagViewAdapter(@NonNull TagFragment tagFragment, @NonNull ArrayList<String> list) {
        this.tagFragment = tagFragment;
        this.list = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(tagFragment.getContext())
                .inflate(R.layout.tag_cardview, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TagViewHolder tagViewHolder = (TagViewHolder) holder;
        final String tag = list.get(position);
        tagViewHolder.tagContent.setText(tag);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
