package suxin.dribble.view.shot_list;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import suxin.dribble.R;
import suxin.dribble.dribble.Dribbble;
import suxin.dribble.dribble.DribbbleException;
import suxin.dribble.model.Shot;
import suxin.dribble.utils.ModelUtil;
import suxin.dribble.view.MainActivity;
import suxin.dribble.view.base.DribbbleTask;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.base.SpaceItemDecoration;
import suxin.dribble.view.shot_detail.ShotFragment;

/**
 * Created by suxin on 10/22/16.
 */

public class ShotListFragment extends Fragment {

    public static final int REQ_CODE_SHOT = 100;
    public static final String KEY_LIST_TYPE = "listType";
    public static final String KEY_BUCKET_ID = "bucketId";
    public static final String KEY_USERSHOTS_USER_ID = "userShots_userid";
    public static final String KEY_USERSHOTS_USERNAME = "userShots_username";
    public static final String KEY_USERSHOT_USERFIGURE = "userShots_user_figure";

    public static final int LIST_TYPE_POPULAR = 1;
    public static final int LIST_TYPE_LIKED = 2;
    public static final int LIST_TYPE_BUCKET = 3;
    public static final int LIST_TYPE_ANIMATEDGIFS = 4;
    public static final int LIST_TYPE_ATTACHMENTS = 5;
    public static final int LIST_TYPE_DEBUTS = 6;
    public static final int LIST_TYPE_TEAMSHOTS = 7;
    public static final int LIST_PLAYOFFS = 8;
    public static final int LIST_REBOUNDS = 9;
    public static final int LIST_TYPE_FOLLOWING = 10;

    public static final String TIME_TYPE = "timetype";

    public static final int LIST_RECENT = 110;
    public static final int LIST_MOSTVIEWED = 101;
    public static final int LIST_MOSTCOMMENTED = 102;
    public static final int LIST_PASTWEEk = 103;
    public static final int LIST_PASTMONTH = 104;
    public static final int LIST_PASTYEAR = 105;
    public static final int LIST_ALLTIME = 106;
    public static final int LIST_MOSTVIEWED_PASTWEEK = 107;
    public static final int LIST_MOSTVIEWED_PASTMONTH = 108;
    public static final int LIST_MOSTVIEWED_PASTYEAR = 109;
    public static final int LIST_MOSTVIEWED_ALLTIME = 111;
    public static final int LIST_FOLLOWING = 112;
    public static final int LIST_USERSHOTS = 113;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private ShotListAdapter adapter;
    private MainActivity mainActivity;
    private ShotListFragment shotListFragment;
    private ArrayList<Integer> arrayList;

    private int listType;
    private int timeType = arrayList == null ? 0 : arrayList.get(0);

    private InfiniteAdapter.LoadMoreListener onLoadMore = new InfiniteAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (Dribbble.isLoggedIn()) {
                AsyncTaskCompat.executeParallel(new LoadShotsTask(false));
            }
        }
    };

    public static ShotListFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ShotListFragment newInstance(int listType, int timeType) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);
        args.putInt(TIME_TYPE, timeType);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ShotListFragment newBucketListInstance(@NonNull String bucketId) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_BUCKET);
        args.putString(KEY_BUCKET_ID, bucketId);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ShotListFragment newUserShotsListInstance(@NonNull String userid, @NonNull String username, @NonNull String user_Avatur_url) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_USERSHOTS);
        args.putString(KEY_USERSHOTS_USER_ID, userid);
        args.putString(KEY_USERSHOTS_USERNAME, username);
        args.putString(KEY_USERSHOT_USERFIGURE, user_Avatur_url);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SHOT && resultCode == Activity.RESULT_OK) {
            Shot updatedShot = ModelUtil.toObject(data.getStringExtra(ShotFragment.KEY_SHOT),
                    new TypeToken<Shot>(){});
            for (Shot shot : adapter.getData()) {
                if (TextUtils.equals(shot.id, updatedShot.id)) {
                    shot.likes_count = updatedShot.likes_count;
                    shot.buckets_count = updatedShot.buckets_count;
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_recycler_view, container, false);
        ButterKnife.bind(this, view);

        timeType = getActivity().getIntent().getIntExtra(TIME_TYPE, 0);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listType = getArguments().getInt(KEY_LIST_TYPE);
        String username = getArguments().getString(KEY_USERSHOTS_USERNAME);
        String userfigure = getArguments().getString(KEY_USERSHOT_USERFIGURE);

        ButterKnife.bind(this, view);


        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

       if (listType == LIST_USERSHOTS) {
            adapter = new ShotListAdapter(this, new ArrayList<Shot>(), onLoadMore, username, userfigure);
            recyclerView.setAdapter(adapter);
        } else {
            adapter = new ShotListAdapter(this, new ArrayList<Shot>(), onLoadMore);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getArguments().getInt(KEY_LIST_TYPE) != LIST_TYPE_BUCKET && getArguments().getInt(KEY_LIST_TYPE) != LIST_USERSHOTS) {
            inflater.inflate(R.menu.timeline_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
                break;
            case R.id.recent:
                timeType = LIST_RECENT;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            case R.id.mostviewed:
                timeType = LIST_MOSTVIEWED;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            case R.id.mostcommented:
                timeType = LIST_MOSTCOMMENTED;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            case R.id.pastweek:
                timeType = LIST_PASTWEEk;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            case R.id.pastmonth:
                timeType = LIST_PASTMONTH;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            case R.id.pastyear:
                timeType = LIST_PASTYEAR;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            case R.id.alltime:
                timeType = LIST_ALLTIME;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            case R.id.mostviewed_pastweek:
                timeType = LIST_MOSTVIEWED_PASTWEEK;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            case R.id.mostviewed_pastmonth:
                timeType = LIST_MOSTVIEWED_PASTMONTH;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            case R.id.mostviewed_pastyear:
                timeType = LIST_MOSTVIEWED_PASTYEAR;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            case R.id.mostviewed_alltime:
                timeType = LIST_MOSTVIEWED_ALLTIME;
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
                break;
            default:
                FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                ft1.detach(this).attach(this).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private class LoadShotsTask extends DribbbleTask<Void, Void, List<Shot>> {

        private boolean refresh;

        private LoadShotsTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doJob(Void... params) throws DribbbleException {
            int page = refresh ? 1 : adapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            switch (listType) {
                case LIST_TYPE_POPULAR:
                    if (timeType == LIST_RECENT) {
                        return Dribbble.getRecentShots(page);
                    } else if (timeType == LIST_MOSTVIEWED) {
                        return Dribbble.getMostViewedShots(page);
                    } else if (timeType == LIST_MOSTCOMMENTED) {
                        return Dribbble.getMostCommentedShots(page);
                    } else if (timeType == LIST_PASTWEEk) {
                        return Dribbble.getPastWeekShots(page);
                    } else if (timeType == LIST_PASTMONTH) {
                        return Dribbble.getPastMonthShots(page);
                    } else if (timeType == LIST_PASTYEAR) {
                        return Dribbble.getPastYearShots(page);
                    } else if (timeType == LIST_ALLTIME) {
                        return Dribbble.getAlltimeShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTWEEK) {
                        return Dribbble.getMostViewedPastWeekShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTMONTH) {
                        return Dribbble.getMostViewedPastMonthShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTYEAR) {
                        return Dribbble.getMostViewedPastYearShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_ALLTIME) {
                        return Dribbble.getMostViewedAlltimeShots(page);
                    } else {
                        return Dribbble.getShots(page);
                    }
                case LIST_TYPE_ANIMATEDGIFS:
                    if (timeType == LIST_RECENT) {
                        return Dribbble.getAnimatedGifsRecentShots(page);
                    } else if (timeType == LIST_MOSTVIEWED) {
                        return Dribbble.getAnimatedGifsMostViewedShots(page);
                    } else if (timeType == LIST_MOSTCOMMENTED) {
                        return Dribbble.getAnimatedGifsMostCommentedShots(page);
                    } else if (timeType == LIST_PASTWEEk) {
                        return Dribbble.getAnimatedGifsPastWeekShots(page);
                    } else if (timeType == LIST_PASTMONTH) {
                        return Dribbble.getAnimatedGifsPastMonthShots(page);
                    } else if (timeType == LIST_PASTYEAR) {
                        return Dribbble.getAnimatedGifsPastYearShots(page);
                    } else if (timeType == LIST_ALLTIME) {
                        return Dribbble.getAnimatedGifsAlltimeShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTWEEK) {
                        return Dribbble.getAnimatedGifsMostViewedPastWeekShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTMONTH) {
                        return Dribbble.getAnimatedGifsMostViewedPastMonthShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTYEAR) {
                        return Dribbble.getAnimatedGifsMostViewedPastYearShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_ALLTIME) {
                        return Dribbble.getAnimatedGifsMostViewedAlltimeShots(page);
                    } else {
                        return Dribbble.getAnimatedShots(page);
                    }
                case LIST_TYPE_ATTACHMENTS:
                    if (timeType == LIST_RECENT) {
                        return Dribbble.getAttachmentsRecentShots(page);
                    } else if (timeType == LIST_MOSTVIEWED) {
                        return Dribbble.getAttachmentsMostViewedShots(page);
                    } else if (timeType == LIST_MOSTCOMMENTED) {
                        return Dribbble.getAttachmentsMostCommentedShots(page);
                    } else if (timeType == LIST_PASTWEEk) {
                        return Dribbble.getAttachmentsPastWeekShots(page);
                    } else if (timeType == LIST_PASTMONTH) {
                        return Dribbble.getAttachmentsPastMonthShots(page);
                    } else if (timeType == LIST_PASTYEAR) {
                        return Dribbble.getAttachmentsPastYearShots(page);
                    } else if (timeType == LIST_ALLTIME) {
                        return Dribbble.getAttachmentsAllTimeShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTWEEK) {
                        return Dribbble.getAttachmentsMostViewedPastWeekShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTMONTH) {
                        return Dribbble.getAttachmentsMostViewedPastMonthShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTYEAR) {
                        return Dribbble.getAttachmentsMostViewedPastYearShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_ALLTIME) {
                        return Dribbble.getAttachmentsMostViewedAllTimeShots(page);
                    } else {
                        return Dribbble.getAttachmentsShots(page);
                    }
                case LIST_TYPE_DEBUTS:
                    if (timeType == LIST_RECENT) {
                        return Dribbble.getDebutsRecentShots(page);
                    } else if (timeType == LIST_MOSTVIEWED) {
                        return Dribbble.getDebutsMostViewedShots(page);
                    } else if (timeType == LIST_MOSTCOMMENTED) {
                        return Dribbble.getDebutsMostCommentedShots(page);
                    } else if (timeType == LIST_PASTWEEk) {
                        return Dribbble.getDebutsPastWeekShots(page);
                    } else if (timeType == LIST_PASTMONTH) {
                        return Dribbble.getDebutsPastMonthShots(page);
                    } else if (timeType == LIST_PASTYEAR) {
                        return Dribbble.getDebutsPastYearShots(page);
                    } else if (timeType == LIST_ALLTIME) {
                        return Dribbble.getDebutsAllTimeShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTWEEK) {
                        return Dribbble.getDebutsMostViewedPastWeekShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTMONTH) {
                        return Dribbble.getDebutsMostViewedPastMonthShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTYEAR) {
                        return Dribbble.getDebutsMostViewedPastYearShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_ALLTIME) {
                        return Dribbble.getDebutsMostViewedAlltimeShots(page);
                    } else {
                        return Dribbble.getDebutsShots(page);
                    }
                case LIST_TYPE_TEAMSHOTS:
                    if (timeType == LIST_RECENT) {
                        return Dribbble.getTeamRecentShots(page);
                    } else if (timeType == LIST_MOSTVIEWED) {
                        return Dribbble.getTeamMostViewedShots(page);
                    } else if (timeType == LIST_MOSTCOMMENTED) {
                        return Dribbble.getTeamMostCommentedShots(page);
                    } else if (timeType == LIST_PASTWEEk) {
                        return Dribbble.getTeamPastWeekShots(page);
                    } else if (timeType == LIST_PASTMONTH) {
                        return Dribbble.getTeamPastMonthShots(page);
                    } else if (timeType == LIST_PASTYEAR) {
                        return Dribbble.getTeamPastYearShots(page);
                    } else if (timeType == LIST_ALLTIME) {
                        return Dribbble.getTeamPastAllTimeShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTWEEK) {
                        return Dribbble.getTeamMostViewedPastWeekShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTMONTH) {
                        return Dribbble.getTeamMostViewedPastMonthShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTYEAR) {
                        return Dribbble.getTeamMostViewedPastYearShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_ALLTIME) {
                        return Dribbble.getTeamMostViewedAllTimeShots(page);
                    } else {
                        return Dribbble.getTeamShots(page);
                    }
                case LIST_PLAYOFFS:
                    if (timeType == LIST_RECENT) {
                        return Dribbble.getPlayoffsRecentShots(page);
                    } else if (timeType == LIST_MOSTVIEWED) {
                        return Dribbble.getPlayoffsMostViewedShots(page);
                    } else if (timeType == LIST_MOSTCOMMENTED) {
                        return Dribbble.getPlayoffsMostCommentedShots(page);
                    } else if (timeType == LIST_PASTWEEk) {
                        return Dribbble.getPlayoffsPastWeekShots(page);
                    } else if (timeType == LIST_PASTMONTH) {
                        return Dribbble.getPlayoffsPastMonthShots(page);
                    } else if (timeType == LIST_PASTYEAR) {
                        return Dribbble.getPlayoffsPastYearShots(page);
                    } else if (timeType == LIST_ALLTIME) {
                        return Dribbble.getPlayoffsAllTimeShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTWEEK) {
                        return Dribbble.getMostViewedPastWeekShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTMONTH) {
                        return Dribbble.getPlayoffsMostViewedPastMonthShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTYEAR) {
                        return Dribbble.getPlayoffsMostViewedPastYearShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_ALLTIME) {
                        return Dribbble.getPlayoffsMostViewedAllTimeShots(page);
                    } else {
                        return Dribbble.getPlayoffsShots(page);
                    }
                case LIST_REBOUNDS:
                    if (timeType == LIST_RECENT) {
                        return Dribbble.getReboundsRecentShots(page);
                    } else if (timeType == LIST_MOSTVIEWED) {
                        return Dribbble.getReboundsMostViewedShots(page);
                    } else if (timeType == LIST_MOSTCOMMENTED) {
                        return Dribbble.getReboundsMostCommentedShots(page);
                    } else if (timeType == LIST_PASTWEEk) {
                        return Dribbble.getReboundsPastWeekShots(page);
                    } else if (timeType == LIST_PASTMONTH) {
                        return Dribbble.getReboundsPastMonthShots(page);
                    } else if (timeType == LIST_PASTYEAR) {
                        return Dribbble.getReboundsPastYearShots(page);
                    } else if (timeType == LIST_ALLTIME) {
                        return Dribbble.getReboundsAllTimeShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTWEEK) {
                        return Dribbble.getReboundsMostViewedPastWeekShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTMONTH) {
                        return Dribbble.getReboundsMostViewedPastMonthShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_PASTYEAR) {
                        return Dribbble.getReboundsMostViewedPastYearShots(page);
                    } else if (timeType == LIST_MOSTVIEWED_ALLTIME) {
                        return Dribbble.getReboundsMostViewedAllTimeShots(page);
                    } else {
                        return Dribbble.getReboundsShots(page);
                    }
                case LIST_FOLLOWING:
                    return Dribbble.getFollowringShots(page);
                case LIST_TYPE_LIKED:
                    return Dribbble.getLikedShots(page);
                case LIST_TYPE_BUCKET:
                    String bucketId = getArguments().getString(KEY_BUCKET_ID);
                    return Dribbble.getBucketShots(bucketId, page);
                case LIST_USERSHOTS:
                    String userId = getArguments().getString(KEY_USERSHOTS_USER_ID);
                    return Dribbble.getShotsOfUser(page, userId);
                default:
                    return Dribbble.getShots(page);
            }
        }

        @Override
        protected void onSuccess(List<Shot> shots) {
            adapter.setShowLoading(shots.size() >= Dribbble.COUNT_PER_LOAD);

            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(shots);
            } else {
                swipeRefreshLayout.setEnabled(true);
                adapter.append(shots);
            }

        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

}
