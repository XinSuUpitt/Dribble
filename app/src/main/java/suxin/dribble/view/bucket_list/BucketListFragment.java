package suxin.dribble.view.bucket_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import suxin.dribble.R;
import suxin.dribble.dribble.Dribbble;
import suxin.dribble.dribble.DribbbleException;
import suxin.dribble.model.Bucket;
import suxin.dribble.view.MainActivity;
import suxin.dribble.view.base.DribbbleTask;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.base.SpaceItemDecoration;
import suxin.dribble.view.shot_detail.ShotFragment;

/**
 * Created by suxin on 10/22/16.
 */

public class BucketListFragment extends Fragment {

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_CHOOSING_MODE = "choosing_mode";
    public static final String KEY_DELETE_MODE = "deleting_mode";
    public static final String KEY_CHOSEN_BUCKET_IDS = "chosen_bucket_ids";
    public static final String KEY_COLLECTED_BUCKET_IDS = "collected_bucket_ids";

    public static final int REQ_CODE_NEW_BUCKET = 100;
    private static final int REQ_CODE_BUCKET = 101;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private BucketListAdapter adapter;


    private String userId;
    private boolean isChoosingMode;
    private boolean isDeletingMode;
    private Set<String> collectedBucketIdSet;

    private InfiniteAdapter.LoadMoreListener onLoadMore = new InfiniteAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            AsyncTaskCompat.executeParallel(new LoadBucketsTask(false));
        }
    };

    public static BucketListFragment newInstance(@Nullable String userId,
                                                 boolean isChoosingMode,
                                                 @Nullable ArrayList<String> chosenBucketIds,
                                                 boolean isDeletingMode) {
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userId);
        args.putBoolean(KEY_CHOOSING_MODE, isChoosingMode);
        args.putStringArrayList(KEY_COLLECTED_BUCKET_IDS, chosenBucketIds);
        args.putBoolean(KEY_DELETE_MODE, isDeletingMode);

        BucketListFragment fragment = new BucketListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_fab_recycler_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // get arguments
        final Bundle args = getArguments();
        userId = args.getString(KEY_USER_ID);
        isChoosingMode = args.getBoolean(KEY_CHOOSING_MODE);
        isDeletingMode = args.getBoolean(KEY_DELETE_MODE);

        if (isChoosingMode) {
            List<String> chosenBucketIdList = args.getStringArrayList(KEY_COLLECTED_BUCKET_IDS);
            if (chosenBucketIdList != null) {
                collectedBucketIdSet = new HashSet<>(chosenBucketIdList);
            }
        } else {
            collectedBucketIdSet = new HashSet<>();
        }

        // init UI
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadBucketsTask(true));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        adapter = new BucketListAdapter(getContext(), new ArrayList<Bucket>(), onLoadMore, isChoosingMode, isDeletingMode);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewBucketDialogFragment dialogFragment = NewBucketDialogFragment.newInstance();
                dialogFragment.setTargetFragment(BucketListFragment.this, REQ_CODE_NEW_BUCKET);
                dialogFragment.show(getFragmentManager(), NewBucketDialogFragment.TAG);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isChoosingMode) {
            if (isDeletingMode) {
                inflater.inflate(R.menu.bucket_list_delete_mode_menu, menu);
            } else {
                inflater.inflate(R.menu.bucket_list_choose_mode_menu, menu);
            }
        } else {
            inflater.inflate(R.menu.timeline2_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            ArrayList<String> chosenBucketIds = new ArrayList<>();
            for (Bucket bucket : adapter.getData()) {
                if (bucket.isChoosing) {
                    chosenBucketIds.add(bucket.id);
                }
            }

            Intent result = new Intent();
            result.putStringArrayListExtra(KEY_CHOSEN_BUCKET_IDS, chosenBucketIds);
            getActivity().setResult(Activity.RESULT_OK, result);
            getActivity().finish();
        }
        else if (item.getItemId() == R.id.deleteButton) {
            bucketDelete();
        }
        else if (item.getItemId() == R.id.delete_mode) {
            ArrayList<String> chosenBucketIds = new ArrayList<>();
            for (Bucket bucket : adapter.getData()) {
                if (bucket.isChoosing) {
                    chosenBucketIds.add(bucket.id);
                }
            }

            AsyncTaskCompat.executeParallel(new DeleteBucketTask(chosenBucketIds));



            getFragmentManager().popBackStackImmediate();


//            //getActivity().recreate();
//
//
//            getActivity().finish();
//
//            BucketListFragment.newInstance(null, false, null, false);
//            adapter.notifyDataSetChanged();
//
//            swipeRefreshLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    swipeRefreshLayout.setRefreshing(true);
//                }
//            });

            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("After Delete", 100);
            startActivity(intent);
            Toast.makeText(getContext(), R.string.delete_successfully, Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_NEW_BUCKET && resultCode == Activity.RESULT_OK) {
            String bucketName = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_NAME);
            String bucketDescription = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_DESCRIPTION);
            if (!TextUtils.isEmpty(bucketName)) {
                AsyncTaskCompat.executeParallel(new NewBucketTask(bucketName, bucketDescription));
            }
        }
    }

    public void bucketDelete() {
            Intent intent = new Intent(getContext(), BucketListActivity.class);
            intent.putExtra(BucketListFragment.KEY_CHOOSING_MODE, true);
            intent.putExtra(BucketListFragment.KEY_DELETE_MODE, true);
            intent.putStringArrayListExtra(BucketListFragment.KEY_COLLECTED_BUCKET_IDS,
                    new ArrayList<String>());
            startActivityForResult(intent, REQ_CODE_BUCKET);
    }
    private class LoadBucketsTask extends DribbbleTask<Void, Void, List<Bucket>> {

        private boolean refresh;

        public LoadBucketsTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Bucket> doJob(Void... params) throws DribbbleException {
            final int page = refresh ? 1 : adapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            return userId == null
                    ? Dribbble.getUserBuckets(page)
                    : Dribbble.getUserBuckets(userId, page);
        }

        @Override
        protected void onSuccess(List<Bucket> buckets) {
            adapter.setShowLoading(buckets.size() >= Dribbble.COUNT_PER_LOAD);

            for (Bucket bucket : buckets) {
                if (collectedBucketIdSet.contains(bucket.id)) {
                    bucket.isChoosing = true;
                }
            }

            if (refresh) {
                adapter.setData(buckets);
                swipeRefreshLayout.setRefreshing(false);
            } else {
                adapter.append(buckets);
            }

            swipeRefreshLayout.setEnabled(true);
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class NewBucketTask extends DribbbleTask<Void, Void, Bucket> {

        private String name;
        private String description;

        private NewBucketTask(String name, String description) {
            this.name = name;
            this.description = description;
        }

        @Override
        protected Bucket doJob(Void... params) throws DribbbleException {
            return Dribbble.newBucket(name, description);
        }

        @Override
        protected void onSuccess(Bucket bucket) {
            bucket.isChoosing = true;
            adapter.prepend(Collections.singletonList(bucket));
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class DeleteBucketTask extends DribbbleTask<Void, Void, Void> {

        private ArrayList<String> ids;

        private DeleteBucketTask(ArrayList<String> ids) {
            this.ids = ids;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            for (String id : ids) {
                Dribbble.deleteBucket(id);
            }
            return null;
        }


        @Override
        protected void onSuccess(Void s) {
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

}
