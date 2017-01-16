package suxin.dribble.view.followingusers;

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
import suxin.dribble.model.User;
import suxin.dribble.view.base.DribbbleTask;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.base.SpaceItemDecoration;

/**
 * Created by suxin on 12/5/16.
 */

public class FollowingUsersFragment extends Fragment{

    public static final String USERID = "userid";
    private String userid;

    @BindView(R.id.userfollowers_swipe_refresh_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.userfollowers_recycler_view)
    RecyclerView recyclerView;

    private FollowingUsersAdapter followingUsersAdapter;

    private InfiniteAdapter.LoadMoreListener onLoadMore = new InfiniteAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (Dribbble.isLoggedIn()) {
                AsyncTaskCompat.executeParallel(new LoadUsersTask(false));
            }
        }
    };


    public static FollowingUsersFragment newInstance(String userid) {
        Bundle args = new Bundle();
        args.putString(USERID, userid);
        FollowingUsersFragment fragment = new FollowingUsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.userfollowers_swipe_recycler_view, container, false);
        ButterKnife.bind(this, view);

        userid = getArguments().getString(USERID);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadUsersTask(true));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));
        followingUsersAdapter = new FollowingUsersAdapter(this, new ArrayList<User>(), onLoadMore);
        recyclerView.setAdapter(followingUsersAdapter);

    }

    private class LoadUsersTask extends DribbbleTask<Void, Void, List<User>> {

        private boolean refresh;

        private LoadUsersTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<User> doJob(Void... params) throws DribbbleException {
            int page = refresh ? 1 : followingUsersAdapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            return Dribbble.getUsers(page, userid);
        }

        @Override
        protected void onSuccess(List<User> users) {
            followingUsersAdapter.setShowLoading(users.size() >= Dribbble.COUNT_PER_LOAD);

            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                followingUsersAdapter.setData(users);
            } else {
                swipeRefreshLayout.setEnabled(true);
                followingUsersAdapter.append(users);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }


}
