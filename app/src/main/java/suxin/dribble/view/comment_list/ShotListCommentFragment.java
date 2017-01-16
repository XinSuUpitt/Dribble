package suxin.dribble.view.comment_list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import suxin.dribble.R;
import suxin.dribble.dribble.Dribbble;
import suxin.dribble.dribble.DribbbleException;
import suxin.dribble.model.Comment;
import suxin.dribble.model.Shot;
import suxin.dribble.view.base.DribbbleTask;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.base.SpaceItemDecoration;

/**
 * Created by suxin on 10/24/16.
 */

public class ShotListCommentFragment extends Fragment {


    public static final String SHOTID = "shotid";
    private String shotid;

    @BindView(R.id.comment_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.comment_swipe_refresh_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private ShotCommentViewAdapter shotCommentViewAdapter;

    private InfiniteAdapter.LoadMoreListener onLoadMore = new InfiniteAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (Dribbble.isLoggedIn()) {
                AsyncTaskCompat.executeParallel(new ShotListCommentFragment.LoadShotsTask(false));
            }
        }
    };

    public static ShotListCommentFragment newInstance(String shotid) {
        Bundle args = new Bundle();
        args.putString(SHOTID, shotid);
        ShotListCommentFragment fragment = new ShotListCommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_swipe_recycler_view, container, false);
        ButterKnife.bind(this, view);

        shotid = getArguments().getString(SHOTID);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new ShotListCommentFragment.LoadShotsTask(true));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));
        shotCommentViewAdapter = new ShotCommentViewAdapter(this, new ArrayList<Comment>(), onLoadMore);
        recyclerView.setAdapter(shotCommentViewAdapter);

    }

    private class LoadShotsTask extends DribbbleTask<Void, Void, List<Comment>> {

        private boolean refresh;

        private LoadShotsTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Comment> doJob(Void... params) throws DribbbleException {
            int page = refresh ? 1 : shotCommentViewAdapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            return Dribbble.getComments(page, shotid);
        }

        @Override
        protected void onSuccess(List<Comment> comments) {
            shotCommentViewAdapter.setShowLoading(comments.size() >= Dribbble.COUNT_PER_LOAD);

            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                shotCommentViewAdapter.setData(comments);
            } else {
                swipeRefreshLayout.setEnabled(true);
                shotCommentViewAdapter.append(comments);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}
