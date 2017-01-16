package suxin.dribble.view.tag_list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import suxin.dribble.R;
import suxin.dribble.dribble.Dribbble;
import suxin.dribble.model.Comment;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.base.SpaceItemDecoration;
import suxin.dribble.view.base.SpaceItemDecorationForGrid;
import suxin.dribble.view.comment_list.ShotCommentViewAdapter;
import suxin.dribble.view.comment_list.ShotListCommentFragment;

/**
 * Created by suxin on 10/24/16.
 */

public class TagFragment extends Fragment {

    public static final String TAGS = "tags";
    private ArrayList<String> tags;

    @BindView(R.id.tag_recycler_view) RecyclerView recyclerView;

    private TagViewAdapter tagViewAdapter;


    public static TagFragment newInstance(ArrayList<String> tags) {
        Bundle args = new Bundle();
        args.putStringArrayList(TAGS, tags);
        TagFragment fragment = new TagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tag_swipe_recycler_view, container, false);
        ButterKnife.bind(this, view);

        tags = getArguments().getStringArrayList(TAGS);

        Toast.makeText(getContext(), "Click to show Tag shots", Toast.LENGTH_LONG).show();

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);


        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new SpaceItemDecorationForGrid(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));
        tagViewAdapter = new TagViewAdapter(this, tags);
        recyclerView.setAdapter(tagViewAdapter);
    }
}
